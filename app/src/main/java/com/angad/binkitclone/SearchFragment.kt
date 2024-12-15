package com.angad.binkitclone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.angad.binkitclone.adapters.AdapterProduct
import com.angad.binkitclone.databinding.FragmentSearchBinding
import com.angad.binkitclone.models.Product
import com.angad.binkitclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {

//    Creating an instance of binding
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapterProduct: AdapterProduct

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

        return binding.root
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
                adapterProduct = AdapterProduct()

                //    Set the adapter to the recyclerview
                binding.rvProducts.adapter = adapterProduct

                //    After setting the adapter to the recyclerview we pass differ list
                adapterProduct.differ.submitList(it)

                //    For search functionality
              //  adapterProduct.originalProduct = it as ArrayList<Product>
                //    After loaded the data hide the shimmer effect
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }
    }

}