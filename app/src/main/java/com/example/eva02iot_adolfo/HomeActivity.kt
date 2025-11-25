package com.example.eva02iot_adolfo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eva02iot_adolfo.adapter.NewsAdapter
import com.example.eva02iot_adolfo.model.News
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var newsAdapter: NewsAdapter
    private val newsList = mutableListOf<News>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = FirebaseFirestore.getInstance()

        val recyclerView = findViewById<RecyclerView>(R.id.rvNews)
        recyclerView.layoutManager = LinearLayoutManager(this)

        newsAdapter = NewsAdapter(newsList) { news ->
            // Navegar a Ver Noticia
            val intent = Intent(this, NewsDetailActivity::class.java)
            intent.putExtra("news_id", news.id)
            startActivity(intent)
        }
        recyclerView.adapter = newsAdapter

        val fabAddNews = findViewById<FloatingActionButton>(R.id.fabAddNews)
        fabAddNews.setOnClickListener {
            // Navegar a Agregar Noticia
            val intent = Intent(this, AddNewsActivity::class.java)
            startActivity(intent)
        }

        loadNews()
    }

    private fun loadNews() {
        db.collection("news")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("HomeActivity", "Error al cargar noticias", e)
                    return@addSnapshotListener
                }

                newsList.clear()
                for (document in snapshots!!) {
                    val news = document.toObject(News::class.java)
                    newsList.add(news)
                }
                newsAdapter.updateData(newsList)
            }
    }
}