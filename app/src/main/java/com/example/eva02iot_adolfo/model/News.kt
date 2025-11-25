package com.example.eva02iot_adolfo.model

import com.google.firebase.firestore.DocumentId

/**
 * Modelo de datos para una noticia.
 * Se usan valores por defecto para que funcione con la deserialización de Firestore.
 */
data class News(
    @DocumentId
    val id: String = "", // Para almacenar el ID del documento de Firestore
    val title: String = "",
    val summary: String = "",
    val content: String = "",
    val author: String = "",
    val date: String = "",
    val imageUrl: String = "" // Nuevo campo para la URL de la imagen
)