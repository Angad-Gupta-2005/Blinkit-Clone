package com.angad.binkitclone.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.angad.binkitclone.CartListener
import com.angad.binkitclone.R
import com.angad.binkitclone.databinding.ActivityUsersMainBinding

class UsersMainActivity : AppCompatActivity(), CartListener {

//    Creating an instance of binding
    private lateinit var binding: ActivityUsersMainBinding

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
}