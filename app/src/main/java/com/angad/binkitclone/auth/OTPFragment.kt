package com.angad.binkitclone.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.angad.binkitclone.R
import com.angad.binkitclone.databinding.FragmentOTPBinding

class OTPFragment : Fragment() {

    private lateinit var binding: FragmentOTPBinding
    private lateinit var userNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOTPBinding.inflate(layoutInflater)

//        calling the function to set the user number into the OTP fragment
        getUserNumber()

//        calling the function to fill the OTP number into the each OTP box
        customizingEnteringOTP()

//        calling the function to perform the backButton functionality
        onBackButtonClicked()
        return binding.root
    }

    private fun onBackButtonClicked() {
         binding.tbOtpFragment.setNavigationOnClickListener {
             findNavController().navigate(R.id.action_OTPFragment_to_signInFragment)
         }
    }

    //    Functionality  to fill the OTP in the OTP fragment
    private fun customizingEnteringOTP() {
        val editTexts = arrayOf(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4, binding.etOtp5, binding.etOtp6)
        for (i in editTexts.indices){
            editTexts[i].addTextChangedListener (object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(p0: Editable?) {
                    if (p0?.length == 1) {
                        if (i < editTexts.size - 1) {
                            editTexts[i + 1].requestFocus()
                        }
                        else if (i == editTexts.size - 1){
                            editTexts[i].clearFocus()
                        }
                    }
                    else if (p0?.length == 0){
                        if (i > 0){
                            editTexts[i - 1].requestFocus()
                        }
                    }

                }

            })
        }
    }

//    Function to set the user number onto the OTP fragment
    private fun getUserNumber() {
        val bundle = arguments
        userNumber = bundle?.getString("number").toString()

        binding.tvUserNumber.text = userNumber
    }

}