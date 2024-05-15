package com.task.contacts.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.task.contacts.R
import com.task.contacts.databinding.ContactsLayoutsBinding
import com.task.contacts.databinding.ItemLayoutBinding
import com.task.contacts.model.ApiContactModel
import java.util.ArrayList

class ContactsAdapter (var applicationContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val item: Int = 0
    private val loading: Int = 1
    private var isLoadingAdded: Boolean = false
    private var retryPageLoad: Boolean = false
    private var errorMsg: String? = ""
    private lateinit var recyclerListener: RecyclerListener
    var homeList = ArrayList<ApiContactModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == item) ContactViewHolder(ContactsLayoutsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
        else LoadingViewHolder(ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = homeList[position]

        if (getItemViewType(position) == item) {
            val myHolder = holder as ContactViewHolder
            myHolder.bind(model, myHolder)
        } else {
            val loadingViewHolder = holder as LoadingViewHolder
            if (retryPageLoad) {
                loadingViewHolder.itemRowBinding.retryLayout.visibility = View.VISIBLE
                loadingViewHolder.itemRowBinding.progress.visibility = View.GONE
                if ((errorMsg ?: "").isEmpty()) loadingViewHolder.itemRowBinding.errorTxt.text = applicationContext.getString(R.string.error_msg_unknown)
                else loadingViewHolder.itemRowBinding.errorTxt.text = errorMsg
            } else {
                loadingViewHolder.itemRowBinding.retryLayout.visibility = View.GONE
                loadingViewHolder.itemRowBinding.progress.visibility = View.VISIBLE
            }
            loadingViewHolder.itemRowBinding.retry.setOnClickListener {
                showRetry(false, "")
                recyclerListener.onclick(holder.adapterPosition, "loadMore", model)
            }
            loadingViewHolder.itemRowBinding.retryLayout.setOnClickListener {
                showRetry(false, "")
                recyclerListener.onclick(holder.adapterPosition, "loadMore", model)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (homeList.size > 0) homeList.size else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) item
        else {
            if (position == homeList.size - 1 && isLoadingAdded) loading
            else item
        }
    }


    inner class ContactViewHolder(val binding: ContactsLayoutsBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(product: ApiContactModel, holder: ContactViewHolder) {

            holder.binding.name.text = product.name
            holder.binding.phone.text = product.number

            Glide.with(itemView.context).load(product.image).listener(object :
                RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    binding.image.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.white))
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }

            }).sizeMultiplier(0.5f).transition(DrawableTransitionOptions.withCrossFade()).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.shape_retry_round).into(holder.binding.image)

        }
    }





            class LoadingViewHolder(binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        var itemRowBinding: ItemLayoutBinding = binding
    }

    fun showRetry(show: Boolean, errorMsg: String) {
        retryPageLoad = show
        notifyItemChanged(homeList.size - 1)
        this.errorMsg = errorMsg
    }

    fun addAll(auction: ArrayList<ApiContactModel>) {
        for (a in auction) {
            add(a)
        }
    }

    fun add(auction: ApiContactModel) {
        homeList.add(auction)
        notifyItemInserted(homeList.size - 1)
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(ApiContactModel())
    }

    fun removeLoadingFooter() {
        if (isLoadingAdded && homeList.isNotEmpty()) {
            isLoadingAdded = false
            val position: Int = homeList.size - 1
            homeList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun filterList(filteredlist: ArrayList<ApiContactModel>) {
        this.homeList = filteredlist
        notifyDataSetChanged()
    }


    fun clearList() {
        isLoadingAdded = false
        this.homeList.clear()
        notifyDataSetChanged()
    }

    fun removePosition(p: Int) {
        this.homeList.removeAt(p)
        notifyItemRemoved(p)
    }

    interface RecyclerListener {
        fun onclick(position: Int, clickType: String, product: ApiContactModel)
    }

    fun setRecyclerListener(recyclerListener: RecyclerListener) {
        this.recyclerListener = recyclerListener
    }



}