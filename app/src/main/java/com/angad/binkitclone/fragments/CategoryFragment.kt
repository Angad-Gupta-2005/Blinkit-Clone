package com.angad.binkitclone.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.angad.binkitclone.R
import com.angad.binkitclone.adapters.AdapterProduct
import com.angad.binkitclone.databinding.FragmentCategoryBinding
import com.angad.binkitclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {

//    Creating an instance of binding
    private lateinit var binding: FragmentCategoryBinding
    private var category: String? = null

    private lateinit var adapterProduct: AdapterProduct

//    Initialised the view model
    private val viewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCategoryBinding.inflate(layoutInflater)


        getProductCategory()

    //    Calling the function that set the toolbar title
        setToolBarTitle()

    //    Calling the function that perform back button functionality
        onNavigationIconClicked()

    //    Calling the function that fetch the product based on category
        fetchCategoryProduct()

    //    Calling the function that perform functionality on search menu click
        onSearchMenuClicked()
        return binding.root
    }

    private fun onNavigationIconClicked() {
        binding.tbSearchFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_homeFragment)
        }
    }

    //    Function that perform search functionality
    private fun onSearchMenuClicked() {
        binding.tbSearchFragment.setOnMenuItemClickListener{ menuItem ->
            when(menuItem.itemId){
                R.id.searchMenu -> {
                    findNavController().navigate(R.id.action_categoryFragment_to_searchFragment)
                    true
                }
                else -> { false }
            }
        }
    }

    private fun fetchCategoryProduct() {
    //    Initially showing the visibility of shimmer effect
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getCategoryProduct(category!!).collect{

            //    If category is empty then hide the recyclerview and show the required text
                if (it.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                } else {
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }

            //    Creating an object of adapter class
                adapterProduct = AdapterProduct()

            //    Set the adapter to the recyclerview
                binding.rvProducts.adapter = adapterProduct

            //    After setting the adapter to the recyclerview we pass differ list
                adapterProduct.differ.submitList(it)

            //    After loaded the data hide the shimmer effect
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }

    }

    private fun setToolBarTitle() {
        binding.tbSearchFragment.title = category
    }

    private fun getProductCategory() {
        val bundle = arguments
        category = bundle?.getString("category")
    }

}