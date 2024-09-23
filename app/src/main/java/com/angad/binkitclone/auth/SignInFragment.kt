package com.angad.binkitclone.auth

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.angad.binkitclone.R
import com.angad.binkitclone.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

    // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(layoutInflater)

        setStatusBarColor()
//        Getting the user number
        getUserNumber()

//        on click continue button this function called
        onContinueButtonClick()
        return binding.root
    }

//    Functionality after the continue button clicked
    private fun onContinueButtonClick() {
        binding.btnContinue.setOnClickListener {
            val number = binding.etUserNumber.text.toString()
            if (number.isEmpty() || number.length != 10){
                Toast.makeText(context, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            }
            else{
//                Jump signIn Fragment to OTP fragment and passing the number using the bundle
                val bundle = Bundle()
                bundle.putString("number", number)
                findNavController().navigate(R.id.action_signInFragment_to_OTPFragment, bundle)
            }
        }
    }

//    function to change the button color if user enter a valid user number
    private fun getUserNumber() {
        binding.etUserNumber.addTextChangedListener( object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(number: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val len = number?.length

                if (len == 10){
                    binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                }
                else{
                    binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grayish_blue))
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        } )
    }

//    Function to set the background color of status bar
    private fun setStatusBarColor(){
        activity?.window?.apply{
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

    }


}