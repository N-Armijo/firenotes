package com.narmijo.notasfirebase;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText CorreoLogin, PassLogin;
    Button Btn_Logeo;
    TextView UsuarioNuevoTXT;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    // Validar los datos
    String correo = "", password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        CorreoLogin = findViewById(R.id.CorreoLogin);
        PassLogin = findViewById(R.id.PassLogin);
        Btn_Logeo = findViewById(R.id.Btn_Logeo);
        UsuarioNuevoTXT = findViewById(R.id.UsuarioNuevoTXT);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();  // Inicializa Firestore

        Btn_Logeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidarDatos();
            }
        });

        UsuarioNuevoTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Registro.class));
            }
        });
    }

    private void ValidarDatos() {

        correo = CorreoLogin.getText().toString();
        password = PassLogin.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Ingrese contraseña", Toast.LENGTH_SHORT).show();
        } else {
            LoginDeUsuario();
        }
    }

    private void LoginDeUsuario() {
        firebaseAuth.signInWithEmailAndPassword(correo, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            VerificarUsuarioEnFirestore(user);
                        }
                    } else {
                        Toast.makeText(Login.this, "Error de autenticación: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Verificar si el usuario ya existe en Firestore y agregarlo si es necesario
    private void VerificarUsuarioEnFirestore(FirebaseUser user) {
        final String userId = user.getUid();
        firestore.collection("Usuarios").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Usuario ya existe, simplemente entra a la app
                            IrAlMenuPrincipal();
                        } else {
                            // Usuario no existe, agregarlo
                            AgregarUsuarioAFirestore(user);
                        }
                    } else {
                        Toast.makeText(Login.this, "Error al verificar el usuario.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void AgregarUsuarioAFirestore(FirebaseUser user) {
        String uid = user.getUid();
        String correo = user.getEmail();

        // Crear un mapa con los datos del usuario
        Map<String, Object> usuarioData = new HashMap<>();
        usuarioData.put("uid", uid);
        usuarioData.put("correo", correo);

        // Agregar a la colección "Usuarios"
        firestore.collection("Usuarios").document(uid)
                .set(usuarioData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Login.this, "Usuario registrado en Firestore.", Toast.LENGTH_SHORT).show();
                    IrAlMenuPrincipal();
                })
                .addOnFailureListener(e -> Toast.makeText(Login.this, "Error al registrar usuario en Firestore.", Toast.LENGTH_SHORT).show());
    }

    private void IrAlMenuPrincipal() {
        Intent intent = new Intent(Login.this, MenuPrincipal.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
