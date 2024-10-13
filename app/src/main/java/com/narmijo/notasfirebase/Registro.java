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
import com.google.firebase.database.DatabaseReference;


//Experimental
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore; //  SE DEBE IMPORTAR PARA LLAMAR A DB


//Esto parece estar desactualizado

//import com.google.firebase.database.FirebaseDatabase;
/**Lo de arriba parece estar desactualizado */

import java.util.HashMap;


public class Registro extends AppCompatActivity {


    //private FirebaseAuth mAuth; aparece mas abajo , pero no private


    private FirebaseFirestore db; //FALTA AGREGAR INSTANCIA DB
    EditText NombreEt,CorreoEt,ContasenaEt,ConfirmarContrasenaEt;
    Button RegistrarUsuario;
    TextView TengounacuentaTXT;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    //
    String nombre = " " , correo = " ", password = "" , confirmarpassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //DEBEMOS LLAMAR A LA DB  Y SU INSTANCIA
        db = FirebaseFirestore.getInstance();


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

        firebaseAuth = FirebaseAuth.getInstance(); // equivalente al mAuth

        progressDialog = new ProgressDialog(Registro.this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);

        //BOTON de registrar usuario
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
        progressDialog.setMessage("Creando su cuenta...");
        progressDialog.show();

        //Crear un usuario en Firebase
        firebaseAuth.createUserWithEmailAndPassword(correo, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //
                        GuardarInformacion();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Registro.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    /**
     * Aqui se empieza a mover el asunto
     *
     *
     */
    private void GuardarInformacion() {
        progressDialog.setMessage("Guardando su información");

        // Obtener la identificación de usuario actual
        String uid = firebaseAuth.getUid();
        FirebaseUser user = firebaseAuth.getCurrentUser();  // Corrección aquí

        if (user != null) {
            // Crear un HashMap con los datos
            HashMap<String, String> Datos = new HashMap<>();
            Datos.put("uid", uid);
            Datos.put("correo", correo);
            Datos.put("nombres", nombre);
            Datos.put("password", password);

            // Agregar datos a la base de datos Firestore
            db.collection("Usuarios")
                    .add(Datos) // Almacena los datos en Firestore
                    .addOnSuccessListener(documentReference -> {
                        progressDialog.dismiss();  // Dismiss el dialog aquí
                        Toast.makeText(Registro.this, "Cuenta agregada con éxito.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Registro.this, MenuPrincipal.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();  // Dismiss también aquí
                        Toast.makeText(Registro.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            progressDialog.dismiss();
            Toast.makeText(Registro.this, "Hubo un error", Toast.LENGTH_SHORT).show();
        }
    }
//Aqui arriba esta lo critico

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}