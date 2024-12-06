package com.stomas.conectamobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ContactsFragment extends Fragment {

    private EditText searchContactInput;
    private ImageButton btnAddContact;
    private RecyclerView recyclerView;
    private ContactsAdapter contactsAdapter;
    private ArrayList<String> contactList = new ArrayList<>();
    private FirebaseFirestore db;
    private CollectionReference contactsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();
        contactsRef = db.collection("contacts");

        // Inicializar vistas
        searchContactInput = view.findViewById(R.id.search_contact_input);
        btnAddContact = view.findViewById(R.id.btn_add_contact);
        recyclerView = view.findViewById(R.id.recycler_contacts);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsAdapter = new ContactsAdapter(contactList);
        recyclerView.setAdapter(contactsAdapter);

        // Cargar contactos desde Firestore
        loadContacts();

        // Agregar nuevo contacto
        btnAddContact.setOnClickListener(v -> addNewContact());

        return view;
    }

    private void loadContacts() {
        contactsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                contactList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String contact = document.getString("name");
                    contactList.add(contact);
                }
                contactsAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error al cargar contactos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNewContact() {
        String newContact = searchContactInput.getText().toString().trim();
        if (newContact.isEmpty()) {
            Toast.makeText(getContext(), "Ingresa un nombre de contacto", Toast.LENGTH_SHORT).show();
            return;
        }

        Contact contact = new Contact(newContact);

        contactsRef.add(contact).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Contacto agregado", Toast.LENGTH_SHORT).show();
                searchContactInput.setText("");
            } else {
                Toast.makeText(getContext(), "Error al agregar contacto", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
