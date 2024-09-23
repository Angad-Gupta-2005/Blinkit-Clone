package com.angad.binkitclone

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.angad.binkitclone.databinding.ProgressDialogBinding
import com.google.firebase.auth.FirebaseAuth

object Utils {

    private var dialog: AlertDialog? = null

    fun showDialog(context: Context, message: String){
        val progress = ProgressDialogBinding.inflate(LayoutInflater.from(context))
        progress.tvMessage.text = message
        dialog = AlertDialog.Builder(context).setView(progress.root).setCancelable(false).create()
        dialog!!.show()
    }

    fun hideDialog(){
        dialog?.dismiss()
    }

//    For authentication
    private var firebaseAuthInstance: FirebaseAuth? = null
    fun getAuthInstance(): FirebaseAuth{
        if (firebaseAuthInstance == null){
            firebaseAuthInstance = FirebaseAuth.getInstance()
        }
        return firebaseAuthInstance!!
    }
}