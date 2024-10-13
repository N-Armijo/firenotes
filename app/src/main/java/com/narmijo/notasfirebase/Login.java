package com.narmijo.notasfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    EditText CorreoLogin, PassLogin;
    Button Btn_Logeo;
    TextView UsuarioNuevoTXT;

    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;

    private FirebaseFirestore firestore; // Añade Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance(); // Inicializa Firestore
        CorreoLogin = findViewById(R.id.CorreoLogin);
        PassLogin = findViewById(R.id.PassLogin);
        Button buttonLogin = findViewById(R.id.Btn_Logeo);
        UsuarioNuevoTXT = findViewById(R.id.UsuarioNuevoTXT);

        buttonLogin.setOnClickListener(v -> loginUser());
        UsuarioNuevoTXT.setOnClickListener(v -> registerUser());
    }

    private void loginUser() {
        String email = CorreoLogin.getText().toString().trim();
        String password = PassLogin.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(Login.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Obtener datos del usuario desde Firestore
                            obtenerDatosUsuario(user.getUid());
                        }
                    } else {
                        Toast.makeText(Login.this, "Error de autenticación: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void obtenerDatosUsuario(String uid) {
        firestore.collection("Usuarios").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Extraer datos del documento
                        String nombres = task.getResult().getString("nombres");
                        String correo = task.getResult().getString("correo");

                        // Pasar datos a NotesActivity
                        Intent intent = new Intent(Login.this, MenuPrincipal.class);
                        intent.putExtra("uid", uid);
                        intent.putExtra("nombres", nombres);
                        intent.putExtra("correo", correo);
                        startActivity(intent);
                        finish(); // Cierra la actividad de login
                    } else {
                        Toast.makeText(Login.this, "Error al obtener datos del usuario.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUser() {
        String email = CorreoLogin.getText().toString().trim();
        String password = PassLogin.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(Login.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(Login.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, MenuPrincipal.class);
                        startActivity(intent); // Inicia la actividad después de registrar
                        finish(); // Cierra la actividad de login
                    } else {
                        Toast.makeText(Login.this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
