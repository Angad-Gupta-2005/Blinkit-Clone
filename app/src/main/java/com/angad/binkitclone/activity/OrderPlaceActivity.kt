package com.angad.binkitclone.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.angad.binkitclone.adapters.AdapterCartProducts
import com.angad.binkitclone.databinding.ActivityOrderPlaceBinding
import com.angad.binkitclone.databinding.AddressLayoutBinding
import com.angad.binkitclone.models.Users
import com.angad.binkitclone.objects.Utils
import com.angad.binkitclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch

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

    //    On place order click functionality
        onPlaceOrderClicked()
    }

    private fun onPlaceOrderClicked() {
        binding.btnPlaceOrder.setOnClickListener {
        //    Observe the live data
            viewModel.getAddressStatus().observe(this){ status ->
                if (status){
                //    Payment work
                    Toast.makeText(this, "Place Order button Clicked", Toast.LENGTH_SHORT).show()
                }
                else{
                    val addressLayoutBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))

                //    Creating an object of alert dialog
                    val alertDialog = AlertDialog.Builder(this)
                        .setView(addressLayoutBinding.root)
                        .create()
                //    Show the alert dialog
                    alertDialog.show()

                //    Calling the function that save the address of user in firebase on click add button
                    addressLayoutBinding.btnAdd.setOnClickListener {
                        saveAddress(alertDialog, addressLayoutBinding)
                    }
                }
            }
        }
    }

    private fun saveAddress(alertDialog: AlertDialog, addressLayoutBinding: AddressLayoutBinding) {
        Utils.showDialog(this, "Processing...")

    //    Accessing the all filed value
        val userPinCode = addressLayoutBinding.etPinCode.text.toString()
        val userPhoneNumber = addressLayoutBinding.etPhoneNumber.text.toString()
        val userState = addressLayoutBinding.etState.text.toString()
        val userDistrict = addressLayoutBinding.etDistrict.text.toString()
        val userAddress = addressLayoutBinding.etDescriptiveAddress.text.toString()

    //    Concatenate the address
        val address = "$userPinCode, $userDistrict( $userState ), $userAddress, $userPhoneNumber"


    //    Saving the address
        lifecycleScope.launch {
            viewModel.saveUserAddress(address)
            viewModel.saveAddressStatus()
        }

    //    After saving the address hide the alertDialog and loader
        alertDialog.dismiss()
        Toast.makeText(this, "Address saved", Toast.LENGTH_SHORT).show()
        Utils.hideDialog()
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