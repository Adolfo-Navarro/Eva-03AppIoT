package com.example.eva02iot_adolfo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar Firebase Auth y Firestore
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etRegisterEmail = findViewById<EditText>(R.id.etRegisterEmail)
        val etRegisterPassword = findViewById<EditText>(R.id.etRegisterPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegisterAccount = findViewById<Button>(R.id.btnRegisterAccount)
        val btnBackToLogin = findViewById<Button>(R.id.btnBackToLoginFromRegister)

        btnRegisterAccount.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val email = etRegisterEmail.text.toString().trim()
            val password = etRegisterPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // Validaciones
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. Crear usuario en Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registro en Auth exitoso, obtener el nuevo usuario
                        val firebaseUser = auth.currentUser
                        val uid = firebaseUser?.uid

                        // 2. Guardar información adicional en Firestore
                        if (uid != null) {
                            val user = hashMapOf(
                                "fullName" to fullName,
                                "email" to email
                            )

                            db.collection("users").document(uid)
                                .set(user)
                                .addOnSuccessListener { 
                                    // Éxito al guardar en Firestore
                                    Toast.makeText(this, "Registro completado con éxito", Toast.LENGTH_SHORT).show()
                                    // Navegar a Login
                                    val intent = Intent(this, LoginActivity::class.java)
                                    // Limpiar el historial para que no pueda volver a esta pantalla
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    // Error al guardar en Firestore
                                    Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        } else {
                            Toast.makeText(this, "Error al obtener el ID del usuario.", Toast.LENGTH_LONG).show()
                        }

                    } else {
                        // Si el registro en Auth falla, mostrar mensaje.
                        Toast.makeText(baseContext, "Falló el registro: ${task.exception?.message}",
                            Toast.LENGTH_LONG).show()
                    }
                }
        }

        btnBackToLogin.setOnClickListener {
            finish() // Simplemente cierra esta actividad para volver a Login
        }
    }
}