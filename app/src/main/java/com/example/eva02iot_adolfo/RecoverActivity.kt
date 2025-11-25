package com.example.eva02iot_adolfo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoverActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover)

        // Inicializar Firebase Auth
        auth = Firebase.auth

        val etRecoverEmail = findViewById<EditText>(R.id.etRecoverEmail)
        val btnRecover = findViewById<Button>(R.id.btnRecover)
        val btnBackToLogin = findViewById<Button>(R.id.btnBackToLogin)

        // Lógica del botón "Enviar Instrucciones"
        btnRecover.setOnClickListener {
            val email = etRecoverEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese su email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pedirle a Firebase que envíe el correo de recuperación
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Si funciona, mostrar mensaje y volver al Login
                        Toast.makeText(this, "Correo de recuperación enviado. Por favor, revise su bandeja de entrada.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        // Si falla, mostrar el error que nos da Firebase
                        Toast.makeText(baseContext, "Error: No se pudo enviar el correo. Verifique el email ingresado.", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // Lógica del botón para volver
        btnBackToLogin.setOnClickListener {
            finish() // Simplemente cierra esta pantalla
        }
    }
}