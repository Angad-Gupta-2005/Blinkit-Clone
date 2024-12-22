package com.angad.binkitclone.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.angad.binkitclone.adapters.AdapterCartProducts
import com.angad.binkitclone.databinding.ActivityOrderPlaceBinding
import com.angad.binkitclone.viewmodels.UserViewModel

class OrderPlaceActivity : AppCompatActivity() {

//    Creating an instance of binding
    private lateinit var binding: ActivityOrderPlaceBinding
//    Initialised the viewModel
    private val viewModel: UserViewModel by viewModels()
//    Creating an instance of adapterCartProduct
    private lateinit var adapterCartProducts: AdapterCartProducts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //    Initialised the binding
        binding = ActivityOrderPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    //    On back click functionality
        onBackButtonClicked()

        getAllCartProducts()
    }

    private fun onBackButtonClicked() {
        binding.tbOrderFragment.setNavigationOnClickListener {
            finish()
        }
    }

    //    Function that fetch add to cart product's details from the room database
    @SuppressLint("SetTextI18n")
    private fun getAllCartProducts() {
        viewModel.getAll().observe(this){ cartProductList ->
        //    Initialised the adapter
            adapterCartProducts = AdapterCartProducts()
        //    Assign the adapter to the recyclerView
            binding.rvProductsItems.adapter = adapterCartProducts
        //    Passing the list of data to the adapter
            adapterCartProducts.differ.submitList(cartProductList)

        //    Sub total logic
            var totalPrice = 0

            for (product in cartProductList){
                val price = product.productPrice?.substring(1)?.toInt()   // remove the ₹ symbol
                val itemCount = product.productCount!!
            //    Multiply product price and no of product item
                totalPrice += (price?.times(itemCount)!!)
            }

            binding.tvSubTotal.text = totalPrice.toString()

            if (totalPrice < 200){
                binding.tvDeliveryCharge.text = "₹ 50"
                totalPrice += 50
            }
            binding.tvGrandTotal.text = totalPrice.toString()
        }
    }
}