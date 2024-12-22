package com.angad.binkitclone.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.angad.binkitclone.CartListener
import com.angad.binkitclone.R
import com.angad.binkitclone.adapters.AdapterCartProducts
import com.angad.binkitclone.databinding.ActivityUsersMainBinding
import com.angad.binkitclone.databinding.BsCartProductsBinding
import com.angad.binkitclone.roomdb.CartProducts
import com.angad.binkitclone.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class UsersMainActivity : AppCompatActivity(), CartListener {

//    Creating an instance of binding
    private lateinit var binding: ActivityUsersMainBinding

//    Initialised the view model
    private val viewModel: UserViewModel by viewModels()

    private lateinit var cartProductList: List<CartProducts>
    private lateinit var adapterCartProducts:AdapterCartProducts
    private lateinit var bsCartProductsBinding: BsCartProductsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    //    Initialised the binding
        binding = ActivityUsersMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    //    Calling the function that set status bar color
        setStatusBarColor()

    //    Calling the function that perform the functionality to store the add to cart product
        getAllCartProducts()
    //    Function that set the total item in the cart from sharedPreferences
        getTotalItemCountInCart()

    //    Calling the function that show add to cart product
        onCartClick()

    //    Calling the function that perform the functionality to go on the order place activity
        onNextButtonClicked()
    }

    private fun onNextButtonClicked() {
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, OrderPlaceActivity::class.java))
            Log.d("TAG", "onNextButtonClicked: CLicked")
        }
    }

//     Function that perform functionality to set status bar color
    private fun setStatusBarColor() {
            window.statusBarColor = ContextCompat.getColor(this, R.color.orange)
    }

//    Function that perform functionality of add to cart
    @SuppressLint("SetTextI18n")
    override fun showCartLayout(itemCount: Int) {
    //    Getting the product count text and updating the text
        val previousCount = binding.tvNumberOfProductCount.text.toString().toInt()
        val updatedCount = previousCount + itemCount

    //    Updating the number of product
        if (updatedCount > 0){
            binding.llCart.visibility = View.VISIBLE
            binding.tvNumberOfProductCount.text = updatedCount.toString()
        } else {
            binding.llCart.visibility = View.GONE
            binding.tvNumberOfProductCount.text = "0"
        }
    }

//    Function that save the itemCount in the sharePreferences
    override fun savingCartItemCount(itemCount: Int) {
        viewModel.fetchTotalCartItemCount().observe(this){
            viewModel.savingCartItemCount( it + itemCount)
        }
    }

    private fun getTotalItemCountInCart() {
        viewModel.fetchTotalCartItemCount().observe(this){
             if (it > 0){
                 binding.llCart.visibility = View.VISIBLE
                 binding.tvNumberOfProductCount.text = it.toString()
             } else {
                 binding.llCart.visibility = View.GONE
             }
        }
    }

//    Function that show the add to cart product using bottom sheet navigation
    private fun onCartClick() {
        binding.llItemCart.setOnClickListener {
            bsCartProductsBinding = BsCartProductsBinding.inflate(LayoutInflater.from(this))

        //    Creating an object of bottom sheet
            val bs = BottomSheetDialog(this)
        //    Passing the view to the bottom sheet
            bs.setContentView(bsCartProductsBinding.root)

        //    Passing the number of item in the cart
            bsCartProductsBinding.tvNumberOfProductCount.text = binding.tvNumberOfProductCount.text

        //    On click the next button go to the OrderPlace Activity
            bsCartProductsBinding.btnNext.setOnClickListener {
                startActivity(Intent(this, OrderPlaceActivity::class.java))
            }

        //    Close the bottom sheet when user click second item on itemCart
            bsCartProductsBinding.llItemCart.setOnClickListener {
                bs.hide()
            }

        //    Initialised the adapter
            adapterCartProducts = AdapterCartProducts()
        //    Initialised the recycler view
            bsCartProductsBinding.rvProductsItems.adapter = adapterCartProducts
        //    Passing the list of data to the adapter
            adapterCartProducts.differ.submitList(cartProductList)

        //    for showing the bottom sheet
            bs.show()
        }
    }

//    Function that store add to cart product details
    private fun getAllCartProducts(){
        viewModel.getAll().observe(this){
            for (i in it){
                //    Initialised and storing the cartProductList
                cartProductList = it

            }
        }
    }

}