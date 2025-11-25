package com.example.eva02iot_adolfo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class NewsDetailActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var newsId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        db = FirebaseFirestore.getInstance()
        newsId = intent.getStringExtra("news_id")

        if (newsId == null) {
            Toast.makeText(this, "Error: No se encontró la noticia", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Cargar datos de la noticia
        loadNewsData()

        // Configurar el botón de eliminar
        val btnDelete = findViewById<Button>(R.id.btnDeleteNews)
        btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Configurar el botón de modificar
        val btnEdit = findViewById<Button>(R.id.btnEditNews)
        btnEdit.setOnClickListener {
            val intent = Intent(this, AddNewsActivity::class.java)
            intent.putExtra("news_id", newsId) // Pasar el ID para entrar en modo edición
            startActivity(intent)
        }

        // Configurar el nuevo botón de regresar
        val btnBack = findViewById<Button>(R.id.btnDetailBackToHome)
        btnBack.setOnClickListener {
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun loadNewsData() {
        val ivImage = findViewById<ImageView>(R.id.ivDetailImage)
        val tvTitle = findViewById<TextView>(R.id.tvDetailTitle)
        val tvAuthor = findViewById<TextView>(R.id.tvDetailAuthor)
        val tvDate = findViewById<TextView>(R.id.tvDetailDate)
        val tvContent = findViewById<TextView>(R.id.tvDetailContent)

        db.collection("news").document(newsId!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    tvTitle.text = document.getString("title")
                    tvAuthor.text = "Por: ${document.getString("author")}"
                    tvDate.text = document.getString("date")
                    tvContent.text = document.getString("content")

                    // Cargar imagen con Glide
                    val imageUrl = document.getString("imageUrl")
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this).load(imageUrl).into(ivImage)
                    }

                } else {
                    Toast.makeText(this, "La noticia ya no existe", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar la noticia: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Está seguro de que desea eliminar esta noticia? Esta acción no se puede deshacer.")
            .setPositiveButton("Sí, Eliminar") { _, _ ->
                deleteNews()
            }
            .setNegativeButton("No, Cancelar", null)
            .show()
    }

    private fun deleteNews() {
        db.collection("news").document(newsId!!)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Noticia eliminada con éxito", Toast.LENGTH_SHORT).show()
                navigateToHome() // Navegación explícita y forzada
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar la noticia: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}