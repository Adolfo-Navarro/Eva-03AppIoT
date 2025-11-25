package com.example.eva02iot_adolfo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eva02iot_adolfo.R
import com.example.eva02iot_adolfo.model.News

class NewsAdapter(private var newsList: List<News>, private val onItemClicked: (News) -> Unit) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.bind(news, onItemClicked)
    }

    override fun getItemCount(): Int = newsList.size

    fun updateData(newNewsList: List<News>) {
        newsList = newNewsList
        notifyDataSetChanged()
    }

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tvNewsTitle)
        private val summary: TextView = itemView.findViewById(R.id.tvNewsSummary)
        private val image: ImageView = itemView.findViewById(R.id.ivNewsImage)

        fun bind(news: News, onItemClicked: (News) -> Unit) {
            title.text = news.title
            summary.text = news.summary
            itemView.setOnClickListener { onItemClicked(news) }

            // Cargar la imagen con Glide
            if (news.imageUrl.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(news.imageUrl)
                    .into(image)
            }
        }
    }
}