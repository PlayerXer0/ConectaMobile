package com.stomas.conectamobile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private EditText inputEmail, inputPassword;
    private Button btnLogin, btnGoToRegister;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    private void navigateToChat() {
        ChatFragment chatFragment = new ChatFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, chatFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToRegister() {
        RegisterFragment registerFragment = new RegisterFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, registerFragment)
                .addToBackStack(null)
                .commit();
    }
    private void navigateToContacts() {
        ContactsFragment contactsFragment = new ContactsFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, contactsFragment)
                .addToBackStack(null)
                .commit();
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Referencias a las vistas
        inputEmail = view.findViewById(R.id.email);
        inputPassword = view.findViewById(R.id.password);
        btnLogin = view.findViewById(R.id.btn_login);
        btnGoToRegister = view.findViewById(R.id.btn_go_to_register);
        progressBar = view.findViewById(R.id.progressBar);




        Button btnViewContacts = view.findViewById(R.id.btn_view_contacts);
        btnViewContacts.setOnClickListener(v -> navigateToContacts());

        // Acción del botón de inicio de sesión
        btnLogin.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getActivity(), "Ingresa un correo electrónico", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getActivity(), "Ingresa una contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                            navigateToChat();
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                            Toast.makeText(getActivity(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Navegar a la pantalla de registro
        btnGoToRegister.setOnClickListener(v -> navigateToRegister());


        return view;
    }
}
