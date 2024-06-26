package com.example.textguard2;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DashboardFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Card SMS
        View cardMessagesFiltered = view.findViewById(R.id.card_messages_filtered);
        TextView titleMessagesFiltered = cardMessagesFiltered.findViewById(R.id.card_title);
        TextView descriptionMessagesFiltered = cardMessagesFiltered.findViewById(R.id.card_description);
        ImageView iconSms = cardMessagesFiltered.findViewById(R.id.card_icon);

        titleMessagesFiltered.setText("SMS");
        descriptionMessagesFiltered.setText("890");
        iconSms.setImageResource(R.drawable.ic_sms);

        // Card Calls
        View cardSuspiciousCalls = view.findViewById(R.id.card_dashboard_calls);
        TextView titleSuspiciousCalls = cardSuspiciousCalls.findViewById(R.id.card_title);
        TextView descriptionSuspiciousCalls = cardSuspiciousCalls.findViewById(R.id.card_description);
        ImageView iconCalls = cardSuspiciousCalls.findViewById(R.id.card_icon);

        titleSuspiciousCalls.setText("Calls");
        descriptionSuspiciousCalls.setText("52");
        iconCalls.setImageResource(R.drawable.ic_call);

        TextView fragmentTitle = view.findViewById(R.id.fragment_title);
        fragmentTitle.setText("Dashboard");

        // Card Social
        View cardSocial = view.findViewById(R.id.card_dashboard_social);
        TextView titleSocial = cardSocial.findViewById(R.id.card_title);
        TextView descriptionSocial = cardSocial.findViewById(R.id.card_description);
        ImageView iconSocial = cardSocial.findViewById(R.id.card_icon);

        titleSocial.setText("Social");
        descriptionSocial.setText("11");
        iconSocial.setImageResource(R.drawable.ic_social);

        return view;
    }
}
