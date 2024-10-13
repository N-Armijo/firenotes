package com.narmijo.notasfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.narmijo.notasfirebase.AgregarNota.Agregar_Nota;
import com.narmijo.notasfirebase.ListarNotas.Listar_Notas;
import com.narmijo.notasfirebase.NotasArchivadas.Notas_Archivadas;
import com.narmijo.notasfirebase.Perfil.Perfil_Usuario;

public class MenuPrincipal extends AppCompatActivity {

    Button AgregarNotas, ListarNotas, Archivados, Perfil, AcercaDe, CerrarSesion;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore db;  // Declara la instancia de Firestore

    TextView UidPrincipal, NombresPrincipal, CorreoPrincipal;
    ProgressBar progressBarDatos;

    LinearLayoutCompat Linear_Nombres, Linear_Correo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("FireNotes");

        // Inicializa los elementos del layout
        UidPrincipal = findViewById(R.id.UidPrincipal);
        NombresPrincipal = findViewById(R.id.NombresPrincipal);
        CorreoPrincipal = findViewById(R.id.CorreoPrincipal);
        progressBarDatos = findViewById(R.id.progressBarDatos);

        Linear_Nombres = findViewById(R.id.Linear_Nombres);
        Linear_Correo = findViewById(R.id.Linear_Correo);

        // Inicializa Firebase Auth y Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();  // Inicializa Firestore

        // Botones del menú
        AgregarNotas = findViewById(R.id.AgregarNotas);
        ListarNotas = findViewById(R.id.ListarNotas);
        Archivados = findViewById(R.id.Archivados);
        Perfil = findViewById(R.id.Perfil);
        AcercaDe = findViewById(R.id.AcercaDe);
        CerrarSesion = findViewById(R.id.CerrarSesion);

        // Listeners para los botones
        AgregarNotas.setOnClickListener(view -> {
            String uid_usuario = UidPrincipal.getText().toString();
            String correo_usuario = CorreoPrincipal.getText().toString();

            Intent intent = new Intent(MenuPrincipal.this, Agregar_Nota.class);
            intent.putExtra("Uid", uid_usuario);
            intent.putExtra("Correo", correo_usuario);
            startActivity(intent);
        });

        ListarNotas.setOnClickListener(view -> {
            startActivity(new Intent(MenuPrincipal.this, Listar_Notas.class));
            Toast.makeText(MenuPrincipal.this, "Listar Notas", Toast.LENGTH_SHORT).show();
        });

        Archivados.setOnClickListener(view -> {
            startActivity(new Intent(MenuPrincipal.this, Notas_Archivadas.class));
            Toast.makeText(MenuPrincipal.this, "Notas Archivadas", Toast.LENGTH_SHORT).show();
        });

        Perfil.setOnClickListener(view -> {
            startActivity(new Intent(MenuPrincipal.this, Perfil_Usuario.class));
            Toast.makeText(MenuPrincipal.this, "Perfil Usuario", Toast.LENGTH_SHORT).show();
        });

        AcercaDe.setOnClickListener(view -> Toast.makeText(MenuPrincipal.this, "Acerca De", Toast.LENGTH_SHORT).show());

        CerrarSesion.setOnClickListener(view -> SalirAplicacion());
    }

    @Override
    protected void onStart() {
        ComprobarInicioSesion();
        super.onStart();
    }

    private void ComprobarInicioSesion() {
        if (user != null) {
            // El usuario ha iniciado sesión
            CargaDeDatos();
        } else {
            // Lo dirigirá al MainActivity
            startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
            finish();
        }
    }

    private void CargaDeDatos() {
        // Realiza una consulta para buscar el documento que tenga el campo "uid" igual al UID del usuario autenticado.
        db.collection("Usuarios")
                .whereEqualTo("uid", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Si la consulta devuelve resultados
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Obtener el primer documento que coincida
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                        // Ocultar el progress bar
                        progressBarDatos.setVisibility(View.GONE);
                        // Mostrar los LinearLayouts
                        Linear_Nombres.setVisibility(View.VISIBLE);
                        Linear_Correo.setVisibility(View.VISIBLE);

                        // Obtener los datos del documento
                        String uid = documentSnapshot.getString("uid");
                        String nombres = documentSnapshot.getString("nombres");
                        String correo = documentSnapshot.getString("correo");

                        // Setear los datos en los respectivos TextView
                        UidPrincipal.setText(uid);
                        NombresPrincipal.setText(nombres);
                        CorreoPrincipal.setText(correo);

                        // Habilitar los botones del menú
                        AgregarNotas.setEnabled(true);
                        ListarNotas.setEnabled(true);
                        Archivados.setEnabled(true);
                        Perfil.setEnabled(true);
                        AcercaDe.setEnabled(true);
                        CerrarSesion.setEnabled(true);

                    } else {
                        // Si no encuentra el documento
                        Toast.makeText(MenuPrincipal.this, "No se encontró el usuario en la base de datos.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar errores en la consulta
                    Toast.makeText(MenuPrincipal.this, "Error al obtener datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void SalirAplicacion() {
        firebaseAuth.signOut();
        startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
        Toast.makeText(this, "Cerraste sesión exitosamente", Toast.LENGTH_SHORT).show();
    }
}
