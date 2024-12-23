package com.angad.binkitclone.viewmodels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.angad.binkitclone.models.Product
import com.angad.binkitclone.models.Users
import com.angad.binkitclone.objects.Utils
import com.angad.binkitclone.roomdb.CartProductDao
import com.angad.binkitclone.roomdb.CartProducts
import com.angad.binkitclone.roomdb.CartProductsDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserViewModel(application: Application): AndroidViewModel(application) {

//    Creating an instance of sharedPreferences
    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("My_Pref", MODE_PRIVATE)

//    Initialised the room database
    private val cartProductDao: CartProductDao? = CartProductsDatabase.getDatabaseInstance(application)?.cartProductsDao()

//    RoomDB
    //  For inserting the data in room database
    suspend fun insertCartProduct(products: CartProducts){
        cartProductDao?.insertCartProduct(products)
    }

    fun getAll(): LiveData<List<CartProducts>>{
        return cartProductDao!!.getAllCartProducts()
    }

    //  For updating the data in room database
    suspend fun updateCartProduct(products: CartProducts){
        cartProductDao?.updateCartProduct(products)
    }

    //  For delete the data in room
    suspend fun deleteCartProduct(productId: String){
        cartProductDao?.deleteCartProduct(productId)
    }


    //    Function that fetch all the product details from firebase
    fun fetchAllTheProducts(): Flow<List<Product>> = callbackFlow {

        val db =  FirebaseDatabase.getInstance("https://blinkit-clone-f610a-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Admins")
            .child("AllProducts")

        //    For fetching the product we create a event listener
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
            //    Creating a list for storing the product details that fetch from the firebase
                val products = ArrayList<Product>()

                for (product in snapshot.children){
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)
                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        db.addValueEventListener(eventListener)

        //    After complete the fetching stop the fetching
        awaitClose{db.removeEventListener(eventListener)}
    }

//    Function that fetch categoryWise product from firebase
    fun getCategoryProduct(category: String): Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance("https://blinkit-clone-f610a-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Admins")
            .child("ProductCategory/${category}")

        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
            //    Creating a list for storing the product details that fetch from the firebase
                val products = ArrayList<Product>()

                for (product in snapshot.children){
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)
                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        db.addValueEventListener(eventListener)
    //    After complete the fetching stop the fetching
        awaitClose{db.removeEventListener(eventListener)}
    }

//    Function that update the itemCount in the firebase
    fun updateItemCount(product: Product, itemCount: Int){
    //    Creating a node for adding all product
        FirebaseDatabase.getInstance("https://blinkit-clone-f610a-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Admins")
            .child("AllProducts/${product.productRandomId}").child("itemCount").setValue(itemCount)

    //    Creating a node for adding ProductCategory
        FirebaseDatabase.getInstance("https://blinkit-clone-f610a-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Admins")
            .child("ProductCategory/${product.productCategory}/${product.productRandomId}").child("itemCount").setValue(itemCount)

    //    Creating a node for adding ProductType
        FirebaseDatabase.getInstance("https://blinkit-clone-f610a-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Admins")
            .child("ProductType/${product.productType}/${product.productRandomId}").child("itemCount").setValue(itemCount)

    }

//    Function that save the cart item count in the sharedPreferences
    fun savingCartItemCount(itemCount: Int){
        sharedPreferences.edit().putInt("itemCount", itemCount).apply()
    }

//    Function that access the current itemCount from the sharePreferences
    fun fetchTotalCartItemCount(): MutableLiveData<Int> {
        val totalItemCount = MutableLiveData<Int>()
        totalItemCount.value = sharedPreferences.getInt("itemCount", 0)
        return totalItemCount
    }

//    Function that check user address status i.e., user is first time or not
    fun getAddressStatus(): MutableLiveData<Boolean>{
        val status = MutableLiveData<Boolean>()
        status.value = sharedPreferences.getBoolean("addressStatus", false)
        return status
    }

//    Function that save address status
    fun saveAddressStatus(){
        sharedPreferences.edit().putBoolean("addressStatus", true).apply()
    }

//    Function that save the user address in firebase
    fun saveUserAddress(address: String){
    //    Save the user data into the firebase realtime database
    FirebaseDatabase.getInstance("https://blinkit-clone-f610a-default-rtdb.asia-southeast1.firebasedatabase.app")
        .getReference("AllUsers")
        .child("Users")
        .child(Utils.getCurrentUserId().toString())
        .child("userAddress").setValue(address)
    }
}