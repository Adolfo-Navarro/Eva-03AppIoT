package com.example.eva02iot_adolfo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddNewsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var newsId: String? = null

    private lateinit var etTitle: EditText
    private lateinit var etSummary: EditText
    private lateinit var etContent: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etImageUrl: EditText
    private lateinit var btnSave: Button
    private lateinit var tvScreenTitle: TextView
    private lateinit var btnBackToHome: Button // Nuevo botón

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_news)

        db = FirebaseFirestore.getInstance()
        newsId = intent.getStringExtra("news_id")

        // Encontrar vistas
        etTitle = findViewById(R.id.etNewsTitle)
        etSummary = findViewById(R.id.etNewsSummary)
        etContent = findViewById(R.id.etNewsContent)
        etAuthor = findViewById(R.id.etNewsAuthor)
        etImageUrl = findViewById(R.id.etImageUrl)
        btnSave = findViewById(R.id.btnSaveNews)
        tvScreenTitle = findViewById(R.id.tvAddNewsTitle)
        btnBackToHome = findViewById(R.id.btnBackToHome) // Encontrar el nuevo botón

        if (newsId != null) {
            // --- MODO EDICIÓN ---
            tvScreenTitle.text = "Modificar Noticia"
            btnSave.text = "Actualizar Noticia"
            loadNewsData()
        } else {
            // --- MODO CREACIÓN ---
            tvScreenTitle.text = "Agregar Nueva Noticia"
            btnSave.text = "Guardar Noticia"
        }

        // Lógica del botón de guardar
        btnSave.setOnClickListener {
            saveOrUpdateNews()
        }

        // Lógica del nuevo botón de regresar
        btnBackToHome.setOnClickListener {
            navigateToHomeClean()
        }
    }

    private fun loadNewsData() {
        db.collection("news").document(newsId!!).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    etTitle.setText(document.getString("title"))
                    etSummary.setText(document.getString("summary"))
                    etContent.setText(document.getString("content"))
                    etAuthor.setText(document.getString("author"))
                    etImageUrl.setText(document.getString("imageUrl"))
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToHomeClean() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun saveOrUpdateNews() {
        val title = etTitle.text.toString().trim()
        val summary = etSummary.text.toString().trim()
        val content = etContent.text.toString().trim()
        val author = etAuthor.text.toString().trim()
        val imageUrl = etImageUrl.text.toString().trim()

        if (title.isEmpty() || summary.isEmpty() || content.isEmpty() || author.isEmpty() || imageUrl.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val newsData: Map<String, Any> = mapOf(
            "title" to title,
            "summary" to summary,
            "content" to content,
            "author" to author,
            "imageUrl" to imageUrl,
            "date" to SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        )

        if (newsId == null) {
            // --- Guardar Nueva Noticia ---
            db.collection("news").add(newsData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Noticia guardada con éxito", Toast.LENGTH_SHORT).show()
                    navigateToHomeClean()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            // --- Actualizar Noticia Existente ---
            db.collection("news").document(newsId!!).update(newsData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Noticia actualizada con éxito", Toast.LENGTH_SHORT).show()
                    navigateToHomeClean()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}