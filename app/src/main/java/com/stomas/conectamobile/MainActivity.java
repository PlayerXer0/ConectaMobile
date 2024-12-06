package com.stomas.conectamobile;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cargar el fragmento inicial (LoginFragment)
        if (savedInstanceState == null) {
            loadFragment(new LoginFragment());
        }
    }

    // Método genérico para cargar cualquier fragmento
    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // Agregar a la pila para manejar la navegación hacia atrás
                .commit();
    }
}
