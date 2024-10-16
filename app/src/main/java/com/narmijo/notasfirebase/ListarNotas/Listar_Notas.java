package com.narmijo.notasfirebase.ListarNotas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.narmijo.notasfirebase.ActualizarNota.Actualizar_Nota;
import com.narmijo.notasfirebase.Detalle.Detalle_Nota;
import com.narmijo.notasfirebase.Objetos.Nota;
import com.narmijo.notasfirebase.R;
import com.narmijo.notasfirebase.ViewHolder.ViewHolder_Nota;

import org.jetbrains.annotations.NotNull;

public class Listar_Notas extends AppCompatActivity {

    RecyclerView recyclerviewNotas;
    FirebaseFirestore firestore;
    FirestoreRecyclerAdapter<Nota, ViewHolder_Nota> firestoreRecyclerAdapter;
    FirestoreRecyclerOptions<Nota> options;

    Dialog dialog;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_notas);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Mis notas");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        recyclerviewNotas = findViewById(R.id.recyclerviewNotas);
        recyclerviewNotas.setHasFixedSize(true);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        firestore = FirebaseFirestore.getInstance();
        dialog = new Dialog(Listar_Notas.this);

        ListarNotasUsuarios();
    }

    private void ListarNotasUsuarios(){
        Query query = firestore.collection("Notas_Publicadas")
                .whereEqualTo("uid_usuario", user.getUid());

        options = new FirestoreRecyclerOptions.Builder<Nota>()
                .setQuery(query, Nota.class)
                .build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Nota, ViewHolder_Nota>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Nota viewHolder_nota, int position, @NotNull Nota nota) {
                viewHolder_nota.SetearDatos(
                        getApplicationContext(),
                        nota.getId_nota(),
                        nota.getUid_usuario(),
                        nota.getCorreo_usuario(),
                        nota.getFecha_hora_actual(),
                        nota.getTitulo(),
                        nota.getDescripcion(),
                        nota.getFecha_nota(),
                        nota.getEstado()
                );
            }

            @Override
            public ViewHolder_Nota onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota,parent,false);
                ViewHolder_Nota viewHolder_nota = new ViewHolder_Nota(view);
                viewHolder_nota.setOnClickListener(new ViewHolder_Nota.ClickListener() {
                    public void onItemClick(View view, int position) {
                        // Obtener el uid del documento Firestore
                        String id_nota = getSnapshots().getSnapshot(position).getId();  // Aquí obtenemos el ID correcto
                        String uid_usuario = getItem(position).getUid_usuario();
                        String correo_usuario = getItem(position).getCorreo_usuario();
                        String fecha_registro = getItem(position).getFecha_hora_actual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_nota = getItem(position).getFecha_nota();
                        String estado = getItem(position).getEstado();

                        // Enviar los datos a la siguiente actividad
                        Intent intent = new Intent(Listar_Notas.this, Detalle_Nota.class);
                        intent.putExtra("id_nota", id_nota);
                        intent.putExtra("uid_usuario", uid_usuario);
                        intent.putExtra("correo_usuario", correo_usuario);
                        intent.putExtra("fecha_registro", fecha_registro);
                        intent.putExtra("titulo", titulo);
                        intent.putExtra("descripcion", descripcion);
                        intent.putExtra("fecha_nota", fecha_nota);
                        intent.putExtra("estado", estado);
                        startActivity(intent);
                    }


                    @Override
                    public void onItemLongClick(View view, int position) {

                        // Obtener el id del documento (id del documento de Firestore)
                        String id_nota = getSnapshots().getSnapshot(position).getId();  // Obtener el ID del documento
                        String uid_usuario = getItem(position).getUid_usuario();
                        String correo_usuario = getItem(position).getCorreo_usuario();
                        String fecha_registro = getItem(position).getFecha_hora_actual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_nota = getItem(position).getFecha_nota();
                        String estado = getItem(position).getEstado();

                        //Declarar las vistas
                        Button CD_Eliminar, CD_Actualizar;

                        //Realizar la conexión con el diseño
                        dialog.setContentView(R.layout.dialogo_opciones);

                        //Inicializar las vistas
                        CD_Eliminar = dialog.findViewById(R.id.CD_Eliminar);
                        CD_Actualizar = dialog.findViewById(R.id.CD_Actualizar);

                        CD_Eliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EliminarNota(id_nota);  // Ahora usa el id del documento Firestore
                                dialog.dismiss();
                            }
                        });

                        CD_Actualizar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Listar_Notas.this, Actualizar_Nota.class);
                                intent.putExtra("id_nota", id_nota);  // Ahora usa el id del documento Firestore
                                intent.putExtra("uid_usuario", uid_usuario);
                                intent.putExtra("correo_usuario", correo_usuario);
                                intent.putExtra("fecha_registro", fecha_registro);
                                intent.putExtra("titulo", titulo);
                                intent.putExtra("descripcion", descripcion);
                                intent.putExtra("fecha_nota", fecha_nota);
                                intent.putExtra("estado", estado);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }

                });
                return viewHolder_nota;
            }
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Listar_Notas.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerviewNotas.setLayoutManager(linearLayoutManager);
        recyclerviewNotas.setAdapter(firestoreRecyclerAdapter);
    }

    private void mostrarOpciones(Nota nota) {
        dialog.setContentView(R.layout.dialogo_opciones);
        Button CD_Eliminar = dialog.findViewById(R.id.CD_Eliminar);
        Button CD_Actualizar = dialog.findViewById(R.id.CD_Actualizar);

        CD_Eliminar.setOnClickListener(view -> {
            EliminarNota(nota.getId_nota());
            dialog.dismiss();
        });

        CD_Actualizar.setOnClickListener(view -> {
            Intent intent = new Intent(Listar_Notas.this, Actualizar_Nota.class);
            intent.putExtra("id_nota", nota.getId_nota());
            intent.putExtra("uid_usuario", nota.getUid_usuario());
            intent.putExtra("correo_usuario", nota.getCorreo_usuario());
            intent.putExtra("fecha_registro", nota.getFecha_hora_actual());
            intent.putExtra("titulo", nota.getTitulo());
            intent.putExtra("descripcion", nota.getDescripcion());
            intent.putExtra("fecha_nota", nota.getFecha_nota());
            intent.putExtra("estado", nota.getEstado());
            startActivity(intent);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void EliminarNota(String id_nota) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Listar_Notas.this);
        builder.setTitle("Eliminar nota");
        builder.setMessage("¿Desea eliminar la nota?");
        builder.setPositiveButton("Sí", (dialogInterface, i) -> {
            firestore.collection("Notas_Publicadas").document(id_nota)  // id_nota es el ID del documento Firestore
                    .delete()
                    .addOnSuccessListener(aVoid -> Toast.makeText(Listar_Notas.this, "Nota eliminada", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(Listar_Notas.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("No", (dialogInterface, i) -> Toast.makeText(Listar_Notas.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show());

        builder.create().show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (firestoreRecyclerAdapter != null) {
            firestoreRecyclerAdapter.startListening();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}