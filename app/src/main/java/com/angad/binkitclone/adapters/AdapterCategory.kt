package com.angad.binkitclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.angad.binkitclone.databinding.ItemViewProductCategoryBinding
import com.angad.binkitclone.models.Category

class AdapterCategory(private val categoryList: ArrayList<Category>): RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>() {
    class CategoryViewHolder( val binding: ItemViewProductCategoryBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = ItemViewProductCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.binding.apply {
            ivCategoryImage.setImageResource(category.image)
            tvCategoryTitle.text = category.title
        }
    }
}