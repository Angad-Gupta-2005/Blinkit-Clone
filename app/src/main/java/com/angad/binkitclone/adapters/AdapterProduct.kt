package com.angad.binkitclone.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.angad.binkitclone.FilteringProducts
import com.angad.binkitclone.databinding.ItemViewProductBinding
import com.angad.binkitclone.models.Product
import com.denzcoskun.imageslider.models.SlideModel

class AdapterProduct(
    val onAddButtonClicked: (Product, ItemViewProductBinding) -> Unit,
    val onIncrementButtonClicked: (Product, ItemViewProductBinding) -> Unit,
    val onDecrementButtonClicked: (Product, ItemViewProductBinding) -> Unit
) :
    RecyclerView.Adapter<AdapterProduct.ProductViewHolder>(), Filterable {

    class ProductViewHolder(val binding: ItemViewProductBinding):RecyclerView.ViewHolder(binding.root)

    private val diffUtil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productRandomId == newItem.productRandomId
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = ItemViewProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = differ.currentList[position]

        holder.binding.apply {
            //    Creating a arrayList for storing the image
            val imageList = ArrayList<SlideModel>()

            //    Accessing the product image
            val productImage = product.productImageUris

            for (i in 0 until productImage?.size!!){
                //    Adding the image
                imageList.add(SlideModel(product.productImageUris!![i].toString()))
            }

            imageSlider.setImageList(imageList)
            tvProductTitle.text = product.productTitle
            val quantity = product.productQuantity.toString() + product.productUnit
            tvProductQuantity.text = quantity
            tvProductPrice.text = "₹" + product.productPrice.toString()

            if (product.itemCount!! > 0){
                tvProductCount.text = product.itemCount.toString()
                tvAdd.visibility = View.GONE
                llProductCount.visibility = View.VISIBLE
            }
            //    Perform functionality on click add button
            tvAdd.setOnClickListener {
                onAddButtonClicked(product, this)
            }
            //    Perform functionality on click + button
            tvIncrementCount.setOnClickListener {
                onIncrementButtonClicked(product, this)
            }
            //    Perform functionality on click - button
            tvDecrementCount.setOnClickListener {
                onDecrementButtonClicked(product, this)
            }
        }

    }

    private val filter: FilteringProducts? = null
    var originalProduct = ArrayList<Product>()
    override fun getFilter(): Filter {
        if (filter == null){
            return FilteringProducts(this, originalProduct)
        }
        return filter
    }
}