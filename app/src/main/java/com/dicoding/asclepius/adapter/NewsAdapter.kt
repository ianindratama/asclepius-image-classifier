package com.dicoding.asclepius.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.network.ArticlesItem
import com.dicoding.asclepius.parseNewsTimestamp

class NewsAdapter(
    private val listNews: List<ArticlesItem?>
): RecyclerView.Adapter<NewsAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.news_item_layout, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

        val newsData = listNews[position]

        Glide.with(holder.itemView.context)
            .load(newsData?.urlToImage)
            .into(holder.imgPhoto)

        holder.tvTitle.text = newsData?.title
        holder.tvDescription.text = newsData?.description
        holder.tvPublishedAt.text = newsData?.publishedAt?.let { parseNewsTimestamp(it) }

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(newsData?.url)
        }

    }

    override fun getItemCount(): Int = listNews.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        var tvTitle: TextView = itemView.findViewById(R.id.tv_item_title)
        var tvDescription: TextView = itemView.findViewById(R.id.tv_item_description)
        var tvPublishedAt: TextView = itemView.findViewById(R.id.tv_item_publishedAt)
    }

    interface OnItemClickCallback {
        fun onItemClicked(newsUrl: String?)
    }
}