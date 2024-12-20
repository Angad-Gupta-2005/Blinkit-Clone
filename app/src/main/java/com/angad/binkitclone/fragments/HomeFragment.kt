package com.angad.binkitclone.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.angad.binkitclone.R
import com.angad.binkitclone.adapters.AdapterCategory
import com.angad.binkitclone.databinding.FragmentHomeBinding
import com.angad.binkitclone.models.Category
import com.angad.binkitclone.objects.Constants
import com.angad.binkitclone.viewmodels.UserViewModel

class HomeFragment : Fragment() {

//    Creating an instance of binding
    private lateinit var binding: FragmentHomeBinding
//    Initialised the view model
    private val viewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

    //    Calling the function that set all category
        setAllCategories()

        navigatingToSearchFragment()

        get()
        return binding.root
    }

    private fun get(){
        viewModel.getAll().observe(viewLifecycleOwner){
            for (i in it){
                Log.d("VVV", i.productTitle.toString())
                Log.d("VVV", i.productCount.toString())
            }
        }
    }


    private fun navigatingToSearchFragment() {
        binding.searchEt.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun setAllCategories() {
        val categoryList = ArrayList<Category>()

        for (i in 0 until Constants.allProductsCategory.size){
            categoryList.add(Category(Constants.allProductsCategory[i], Constants.allProductsCategoryIcon[i]))
        }

        binding.rvCategories.adapter = AdapterCategory(categoryList, ::onCategoryIconClicked)
    }

    private fun onCategoryIconClicked(category:Category){
        val bundle = Bundle()
        bundle.putString("category", category.title)
        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment, bundle)
    }

}