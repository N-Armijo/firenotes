package com.narmijo.notasfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.narmijo.notasfirebase.R;


import java.util.HashMap;


public class Registro extends AppCompatActivity {

    EditText NombreEt,CorreoEt,ContasenaEt,ConfirmarContrasenaEt;
    Button RegistrarUsuario;
    TextView TengounacuentaTXT;

    FirebaseAuth firebaseAuth;



    private FirebaseFirestore db;
    ProgressDialog progressDialog;

    //
    String nombre = " " , correo = " ", password = "" , confirmarpassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Registrar");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        NombreEt = findViewById(R.id.NombreEt);
        CorreoEt = findViewById(R.id.CorreoEt);
        ContasenaEt = findViewById(R.id.ContasenaEt);
        ConfirmarContrasenaEt = findViewById(R.id.ConfirmarContrasenaEt);
        RegistrarUsuario = findViewById(R.id.RegistrarUsuario);
        TengounacuentaTXT = findViewById(R.id.TengounacuentaTXT);

        firebaseAuth = FirebaseAuth.getInstance();


        RegistrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                ValidarDatos();
            }
        });

        TengounacuentaTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registro.this, Login.class));
            }
        });
    }

    private void ValidarDatos(){
        nombre = NombreEt.getText().toString();
        correo = CorreoEt.getText().toString();
        password = ContasenaEt.getText().toString();
        confirmarpassword = ConfirmarContrasenaEt.getText().toString();

        if (TextUtils.isEmpty(nombre)){
            Toast.makeText(this, "Ingrese nombre", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            Toast.makeText(this, "Ingrese correo", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Ingrese contraseña", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(confirmarpassword)){
            Toast.makeText(this, "Confirme contraseña", Toast.LENGTH_SHORT).show();

        }
        else if (!password.equals(confirmarpassword)){
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        }
        else {
            CrearCuenta();
        }
    }

    private void CrearCuenta() {
        firebaseAuth.createUserWithEmailAndPassword(correo, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        GuardarInformacion();
                        Intent intent = new Intent(Registro.this, MenuPrincipal.class);
                        startActivity(intent); // Inicia la actividad después de registrar
                        finish(); // Cierra la actividad de login
                    } else {
                        Toast.makeText(Registro.this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void GuardarInformacion() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null ) {
            //Obtener la identificación de usuario actual
            String uid = firebaseAuth.getUid();

            HashMap<String, String> Datos = new HashMap<>();
            Datos.put("uid",  uid);
            Datos.put("correo", correo);
            Datos.put("nombres", nombre);
            Datos.put("password", password);
            //Es la db

            db.collection("Usuarios")
                    .add(Datos)
                    .addOnSuccessListener(documentReference -> {

                        Toast.makeText(Registro.this, "Nota guardada con éxito.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(Registro.this, "Error al guardar la nota.", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(Registro.this, "El campo de la nota está vacío o el usuario no está autenticado.", Toast.LENGTH_SHORT).show();
        }

    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}