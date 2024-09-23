package com.angad.binkitclone.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.angad.binkitclone.Utils
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class AuthViewModel: ViewModel() {

//    stateflow variable
    private val _verificationId = MutableStateFlow<String?>(null)

//    state flow variable of type boolean
    private val _otpSent = MutableStateFlow(false)
    val optSent = _otpSent

//    functionality after the user signIn successfully
    private val _isSignedInSuccessfully =MutableStateFlow(false)
    val isSignedInSuccessfully = _isSignedInSuccessfully

    fun sendOTP(userNumber : String, activity: Activity){

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {}

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                _verificationId.value = verificationId
                _otpSent.value = true
            }
        }

        val options = PhoneAuthOptions.newBuilder(Utils.getAuthInstance())
            .setPhoneNumber("+91$userNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    fun signInWithPhoneAuthCredential(otp: String, userNumber: String) {
        val credential = PhoneAuthProvider.getCredential(_verificationId.value.toString(), otp)
        Utils.getAuthInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    _isSignedInSuccessfully.value = true
                } else {
                    // Sign in failed, display a message and update the UI

                }
            }
    }
}