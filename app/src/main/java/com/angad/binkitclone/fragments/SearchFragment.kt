package com.angad.binkitclone.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.angad.binkitclone.CartListener
import com.angad.binkitclone.R
import com.angad.binkitclone.adapters.AdapterProduct
import com.angad.binkitclone.databinding.FragmentSearchBinding
import com.angad.binkitclone.databinding.ItemViewProductBinding
import com.angad.binkitclone.models.Product
import com.angad.binkitclone.roomdb.CartProducts
import com.angad.binkitclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {

//    Creating an instance of binding
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapterProduct: AdapterProduct

//    Creating an object of Interface CartListener
    private var cartListener: CartListener? = null

//    Initialised the viewModel
    private val viewModel: UserViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
    // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(layoutInflater)

    //    calling the function that get all the product data
        getAllTheProducts()

        backToHomeFragment()


    //    Calling the function that implement the search functionality
        searchProducts()

        return binding.root
    }

//    Function the perform the search functionality
    private fun searchProducts() {
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                adapterProduct.filter.filter(query)
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }


    private fun backToHomeFragment() {
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }
    }

    private fun getAllTheProducts() {
        lifecycleScope.launch {

            //    Initially showing the visibility of shimmer effect
            binding.shimmerViewContainer.visibility = View.VISIBLE

            viewModel.fetchAllTheProducts().collect{

                //    If category is empty then hide the recyclerview and show the required text
                if (it.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                } else {
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }

                //    Creating an object of adapter class
                adapterProduct = AdapterProduct(
                    ::onAddButtonClicked,
                    ::onIncrementButtonClicked,
                    ::onDecrementButtonClicked
                )

                //    Set the adapter to the recyclerview
                binding.rvProducts.adapter = adapterProduct

                //    After setting the adapter to the recyclerview we pass differ list
                adapterProduct.differ.submitList(it)

                //    For search functionality
                adapterProduct.originalProduct = it as ArrayList<Product>
                //    After loaded the data hide the shimmer effect
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }
    }


    //    Function that perform functionality to hide the add button and show the product count button
    private fun onAddButtonClicked(product: Product, productBinding: ItemViewProductBinding){
        productBinding.tvAdd.visibility = View.GONE
        productBinding.llProductCount.visibility = View.VISIBLE

        //    Step 1:
        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount++
        productBinding.tvProductCount.text = itemCount.toString()

        cartListener?.showCartLayout(1)

        //    Step 2:
        product.itemCount = itemCount
        //    Calling the function that save the itemCount in sharedPreferences
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(1)
            saveProductInRoomDb(product)

            //    Calling the function that update the itemCount on firebase
            viewModel.updateItemCount(product, itemCount)
        }

    }

    //    Function that perform functionality on increment button clicked
    private fun onIncrementButtonClicked(product: Product, productBinding: ItemViewProductBinding){
        //    Step 1:
        var itemCountInc = productBinding.tvProductCount.text.toString().toInt()
        itemCountInc++

        //    ItemCount is less than stock of product always
        if (product.productStock!! + 1 > itemCountInc){
            productBinding.tvProductCount.text = itemCountInc.toString()

            cartListener?.showCartLayout(1)

            //    Step 2:
            product.itemCount = itemCountInc
            //    Calling the function that save the itemCount in sharedPreferences
            lifecycleScope.launch {
                cartListener?.savingCartItemCount(1)
                saveProductInRoomDb(product)
                //    Calling the function that update the itemCount on firebase
                viewModel.updateItemCount(product, itemCountInc)
            }
        }
        else{
            Toast.makeText(requireContext(), "Can't add more item of this product", Toast.LENGTH_SHORT).show()
        }

    }

    //    Function that perform functionality on decrement button clicked i.e., -
    @SuppressLint("SetTextI18n")
    private fun onDecrementButtonClicked(product: Product, productBinding: ItemViewProductBinding){
        //    Step 1:
        var itemCountDec = productBinding.tvProductCount.text.toString().toInt()
        itemCountDec--

        //    Step 2:
        product.itemCount = itemCountDec
        //    Calling the function that save the itemCount in sharedPreferences
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(-1)
            saveProductInRoomDb(product)
            //    Calling the function that update the itemCount on firebase
            viewModel.updateItemCount(product, itemCountDec)
        }

        if (itemCountDec>0){
            productBinding.tvProductCount.text = itemCountDec.toString()
        } else {
            lifecycleScope.launch {
                viewModel.deleteCartProduct(product.productRandomId!!)
            }
            productBinding.tvAdd.visibility = View.VISIBLE
            productBinding.llProductCount.visibility = View.GONE
            productBinding.tvProductCount.text = "0"
        }

        cartListener?.showCartLayout(-1)

    }

    //    Function that save the data in room database
    private fun saveProductInRoomDb(product: Product) {
        //    Creating an instance of data class of room database i.e., CartProducts
        val cartProduct = CartProducts(
            productId = product.productRandomId!!,
            productTitle = product.productTitle,
            productQuantity = product.productQuantity.toString() + product.productUnit.toString(),
            productPrice = "₹$product.productPrice",
            productCount = product.itemCount,
            productStock = product.productStock,
            productImage = product.productImageUris?.get(0)!!,
            productCategory = product.productCategory,
            adminUid = product.adminUid
        )

        lifecycleScope.launch {
            viewModel.insertCartProduct(cartProduct)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CartListener){
            cartListener = context
        }
        else{
            throw ClassCastException("Please implement cart listener")
        }
    }

}