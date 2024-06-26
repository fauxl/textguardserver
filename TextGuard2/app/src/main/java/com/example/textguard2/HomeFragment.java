package com.example.textguard2;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Configura il pulsante per i termini e condizioni
        Button termsButton = view.findViewById(R.id.button_terms);
        termsButton.setOnClickListener(v -> {
            // Aggiungi l'azione per visualizzare i termini e condizioni
            Toast.makeText(getActivity(), "Termini e condizioni", Toast.LENGTH_SHORT).show();
        });

        // Configura il pulsante per uscire
        Button logoutButton = view.findViewById(R.id.button_logout);
        logoutButton.setOnClickListener(v -> {
            // Aggiungi l'azione per uscire dall'account
            Toast.makeText(getActivity(), "Esci", Toast.LENGTH_SHORT).show();
        });

        // Configura l'immagine del profilo (opzionale)
        ImageView profileImage = view.findViewById(R.id.profile_image);
        profileImage.setOnClickListener(v -> {
            // Aggiungi l'azione per cambiare l'immagine del profilo
            Toast.makeText(getActivity(), "Cambia immagine del profilo", Toast.LENGTH_SHORT).show();
        });


        TextView fragmentTitle = view.findViewById(R.id.fragment_title);
        fragmentTitle.setText("Home");

        return view;
    }
}
