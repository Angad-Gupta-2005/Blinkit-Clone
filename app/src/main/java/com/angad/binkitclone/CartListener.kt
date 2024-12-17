package com.angad.binkitclone

interface CartListener {
    fun showCartLayout(itemCount: Int)

    fun savingCartItemCount(itemCount: Int)
}