package com.stomas.conectamobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private ContactsAdapter contactsAdapter;
    private ArrayList<String> usersList = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contacts_fragment, container, false);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar RecyclerView
        recyclerView = view.findViewById(R.id.recycler_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsAdapter = new ContactsAdapter(usersList);
        recyclerView.setAdapter(contactsAdapter);

        // Cargar usuarios desde Firebase
        loadUsers();

        return view;
    }

    private void loadUsers() {
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                usersList.clear(); // Asegúrate de limpiar la lista antes de agregar nuevos datos
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String email = document.getString("email");
                    if (email != null) {
                        usersList.add(email); // Agrega solo los correos a la lista
                    }
                }
                contactsAdapter.notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
            } else {
                Toast.makeText(getContext(), "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
