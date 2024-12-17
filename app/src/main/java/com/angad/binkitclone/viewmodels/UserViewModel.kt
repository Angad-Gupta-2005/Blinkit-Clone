package com.angad.binkitclone.viewmodels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.angad.binkitclone.models.Product
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

//    Function that fetch categoryWise product
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

}