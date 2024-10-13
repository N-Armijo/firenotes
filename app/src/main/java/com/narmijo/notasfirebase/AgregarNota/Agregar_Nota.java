package com.narmijo.notasfirebase.AgregarNota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.narmijo.notasfirebase.Objetos.Nota;
import com.narmijo.notasfirebase.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Agregar_Nota extends AppCompatActivity {

    TextView Uid_Usuario, Correo_usuario, Fecha_hora_actual, Fecha, Estado;
    EditText Titulo, Descripcion;
    Button Btn_Calendario;

    int dia, mes, anio;

    // Referencia a Firestore
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_nota);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Inicializar variables
        InicializarVariables();
        ObtenerDatos();
        Obtener_Fecha_Hora_Actual();

        Btn_Calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendario = Calendar.getInstance();

                dia = calendario.get(Calendar.DAY_OF_MONTH);
                mes = calendario.get(Calendar.MONTH);
                anio = calendario.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Agregar_Nota.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int AnioSeleccionado, int MesSeleccionado, int DiaSeleccionado) {

                        String diaFormateado, mesFormateado;

                        // Formatear el día
                        if (DiaSeleccionado < 10) {
                            diaFormateado = "0" + DiaSeleccionado;
                        } else {
                            diaFormateado = String.valueOf(DiaSeleccionado);
                        }

                        // Formatear el mes
                        int Mes = MesSeleccionado + 1;
                        if (Mes < 10) {
                            mesFormateado = "0" + Mes;
                        } else {
                            mesFormateado = String.valueOf(Mes);
                        }

                        // Establecer la fecha en el TextView
                        Fecha.setText(diaFormateado + "/" + mesFormateado + "/" + AnioSeleccionado);
                    }
                }, anio, mes, dia);
                datePickerDialog.show();
            }
        });
    }

    private void InicializarVariables() {
        Uid_Usuario = findViewById(R.id.Uid_Usuario);
        Correo_usuario = findViewById(R.id.Correo_usuario);
        Fecha_hora_actual = findViewById(R.id.Fecha_hora_actual);
        Fecha = findViewById(R.id.Fecha);
        Estado = findViewById(R.id.Estado);

        Titulo = findViewById(R.id.Titulo);
        Descripcion = findViewById(R.id.Descripcion);
        Btn_Calendario = findViewById(R.id.Btn_Calendario);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();
    }

    private void ObtenerDatos() {
        String uid_recuperado = getIntent().getStringExtra("Uid");
        String correo_recuperado = getIntent().getStringExtra("Correo");

        Uid_Usuario.setText(uid_recuperado);
        Correo_usuario.setText(correo_recuperado);
    }

    private void Obtener_Fecha_Hora_Actual() {
        String Fecha_hora_registro = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss a",
                Locale.getDefault()).format(System.currentTimeMillis());
        Fecha_hora_actual.setText(Fecha_hora_registro);
    }

    private void Agregar_Nota() {

        // Obtener los datos de los campos
        String uid_usuario = Uid_Usuario.getText().toString();
        String correo_usuario = Correo_usuario.getText().toString();
        String fecha_hora_actual = Fecha_hora_actual.getText().toString();
        String titulo = Titulo.getText().toString();
        String descripcion = Descripcion.getText().toString();
        String fecha = Fecha.getText().toString();
        String estado = Estado.getText().toString();

        // Validar que todos los campos estén llenos
        if (!uid_usuario.equals("") && !correo_usuario.equals("") && !fecha_hora_actual.equals("") &&
                !titulo.equals("") && !descripcion.equals("") && !fecha.equals("") && !estado.equals("")) {

            // Crear un mapa de datos para Firestore
            Map<String, Object> nota = new HashMap<>();
            nota.put("uid_usuario", uid_usuario);
            nota.put("correo_usuario", correo_usuario);
            nota.put("fecha_hora_actual", fecha_hora_actual);
            nota.put("titulo", titulo);
            nota.put("descripcion", descripcion);
            nota.put("fecha", fecha);
            nota.put("estado", estado);

            // Agregar la nota a Firestore en la colección "Notas_Publicadas"
            db.collection("Notas_Publicadas")
                    .add(nota)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(Agregar_Nota.this, "Se ha agregado la nota exitosamente", Toast.LENGTH_SHORT).show();
                        onBackPressed();  // Volver a la pantalla anterior
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Agregar_Nota.this, "Error al agregar la nota: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } else {
            Toast.makeText(this, "Llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_agregar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Agregar_Nota_BD) {
            Agregar_Nota();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
