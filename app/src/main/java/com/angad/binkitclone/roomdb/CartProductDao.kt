package com.angad.binkitclone.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CartProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCartProduct(products: CartProducts)

    @Update
    fun updateCartProduct(products: CartProducts)

    @Query("SELECT * FROM CartProductTable")
    fun getAllCartProducts(): LiveData<List<CartProducts>>

    @Query("DELETE FROM CartProductTable WHERE productId = :productId")
    fun deleteCartProduct(productId: String)

}