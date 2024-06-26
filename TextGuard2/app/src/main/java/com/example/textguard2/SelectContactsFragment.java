package com.example.textguard2;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class SelectContactsFragment extends Fragment {

    private ContactAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_contacts, container, false);

        ListView listView = view.findViewById(R.id.contacts_list_view);

        List<Contact> favoriteContacts = getFavoriteContacts();
        adapter = new ContactAdapter(getContext(), favoriteContacts);
        listView.setAdapter(adapter);

        ImageView addContactButton = view.findViewById(R.id.add_contact);
        addContactButton.setOnClickListener(v -> openContactsApp());

        TextView fragmentTitle = view.findViewById(R.id.fragment_title);
        fragmentTitle.setText("Friends Contact List");

        return view;
    }

    private List<Contact> getFavoriteContacts() {
        List<Contact> contacts = new ArrayList<>();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.STARRED
        };
        String selection = ContactsContract.CommonDataKinds.Phone.STARRED + "=?";
        String[] selectionArgs = {"1"};

        Cursor cursor = getActivity().getContentResolver().query(uri, projection, selection, selectionArgs, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.add(new Contact(name, number));
            }
            cursor.close();
        }

        return contacts;
    }

    private void openContactsApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
        startActivity(intent);
    }
}
