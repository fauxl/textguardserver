package com.example.textguard2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {

    public ContactAdapter(@NonNull Context context, @NonNull List<Contact> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_contacts, parent, false);
        }

        Contact contact = getItem(position);

        TextView contactName = convertView.findViewById(R.id.contact_name);
        TextView contactPhone = convertView.findViewById(R.id.contact_phone);

        if (contact != null) {
            contactName.setText(contact.getName());
            contactPhone.setText(contact.getPhoneNumber());
        }

        return convertView;
    }
}
