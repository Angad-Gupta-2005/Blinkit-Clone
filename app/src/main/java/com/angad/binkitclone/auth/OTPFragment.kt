package com.angad.binkitclone.auth

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
import com.angad.binkitclone.R
import com.angad.binkitclone.Utils
import com.angad.binkitclone.databinding.FragmentOTPBinding
import com.angad.binkitclone.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class OTPFragment : Fragment() {

    private lateinit var binding: FragmentOTPBinding
    private lateinit var userNumber: String

//    calling the function to send the OTP
    private val viewModel : AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOTPBinding.inflate(layoutInflater)

//        calling the function to set the user number into the OTP fragment
        getUserNumber()

//        calling the function to fill the OTP number into the each OTP box
        customizingEnteringOTP()

//        calling the function to send the otp
        sendOTP()

//        calling the function to perform action when Login button clicked
        onLoginButtonClicked()

//        calling the function to perform the backButton functionality
        onBackButtonClicked()
        return binding.root
    }



    //    Function to sendOTP
    private fun sendOTP() {
        Utils.showDialog(requireContext(), "Sending OTP...")

        viewModel.apply {
            sendOTP(userNumber, requireActivity())
            lifecycleScope.launch {
                optSent.collect{
                    if (it){
                        Utils.hideDialog()
                        Toast.makeText(context, "Otp sent...", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

//    back button functionality
    private fun onBackButtonClicked() {
         binding.tbOtpFragment.setNavigationOnClickListener {
             findNavController().navigate(R.id.action_OTPFragment_to_signInFragment)
         }
    }

    //    Function to set the user number onto the OTP fragment
    private fun getUserNumber() {
        val bundle = arguments
        userNumber = bundle?.getString("number").toString()

        binding.tvUserNumber.text = userNumber
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

    //    Functionality to perform action when Login button clicked
    private fun onLoginButtonClicked() {
        binding.btnLogin.setOnClickListener {
            Toast.makeText(context, "Signing You...", Toast.LENGTH_SHORT).show()
            val editTexts = arrayOf(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4, binding.etOtp5, binding.etOtp6)
            val otp = editTexts.joinToString(""){ it.text.toString() }

            if (otp.length < editTexts.size){
                Toast.makeText(context, "Please enter right otp", Toast.LENGTH_SHORT).show()
            }
            else{
//                removing the text and focus from the editTexts i.e., otp
                editTexts.forEach { it.text?.clear(); it.clearFocus() }
                verifyOtp(otp)
            }
        }
    }

//    function to verify the otp entered by the user
    private fun verifyOtp(otp: String) {
        viewModel.signInWithPhoneAuthCredential(otp, userNumber)

        lifecycleScope.launch {
            viewModel.isSignedInSuccessfully.collect{
                if (it){
                    Utils.hideDialog()
                    Toast.makeText(context, "Logged In...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}