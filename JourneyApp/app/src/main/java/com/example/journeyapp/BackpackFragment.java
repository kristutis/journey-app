package com.example.journeyapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BackpackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BackpackFragment extends Fragment {
    final String CHANNEL_ID = "channel1";
    View view;
    Context context;

    LinkedList<Item> items = new LinkedList<Item>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BackpackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BackpackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BackpackFragment newInstance(String param1, String param2) {
        BackpackFragment fragment = new BackpackFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_backpack, container, false);
        context = getActivity().getApplicationContext();

        final String selectedJourney = GetSelectedJourney(context);
        if (selectedJourney=="NULL")
        {
            HandleWhenJourneyIsNotSelected();
            return view;
        }

        items = Item.GetGeneratedItemsFromSM(context);
        createListAndTextView(selectedJourney, items);

        Button generateNot = view.findViewById(R.id.generateNotificationButton);
        generateNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Notification generated", Toast.LENGTH_SHORT).show();
                String titleMessage = "Prepare for an adventure to " + selectedJourney + "!";
                String contentMessage = "Don't forget to pack your items!";
                createNotification(titleMessage, contentMessage);
            }
        });

        Button postOnFb = view.findViewById(R.id.generatePostOnFacebookButton);
        postOnFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccessToken.getCurrentAccessToken() == null) {
                    //jei atsijunge nuo fb
                    Toast.makeText(context, "not logged in", Toast.LENGTH_SHORT).show();
                }
                AccessToken token = AccessToken.getCurrentAccessToken();
            }
        });

        final EditText editTextFilter = view.findViewById(R.id.editTextTextSortItems);
        editTextFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextFilter.setText("");
            }
        });

        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LinkedList<Item> items = Item.GetGeneratedItemsFromSM(context);
                String filter = editTextFilter.getText().toString();

                if ("".equals(filter)) {
                    createListAndTextView(selectedJourney, items);
                } else {
                    LinkedList<Item> sortedItems = new LinkedList<Item>();
                    for (Item item : items) {
                        if (item.contains(filter)) {
                            sortedItems.add(item);
                        }
                    }
                    createListAndTextView(selectedJourney, sortedItems);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final Button buttonFilterItems = view.findViewById(R.id.buttonFilterItems);
        buttonFilterItems.setOnClickListener(new View.OnClickListener() {
            LinkedList<Item> items = Item.GetGeneratedItemsFromSM(context);
            @Override
            public void onClick(View v) {
                String buttonText = buttonFilterItems.getText().toString();
                switch (buttonText) {
                    case "Filter items alphabetically":
                        //code
                        buttonFilterItems.setText("Filter items by count");
                        Collections.sort(items, new Comparator<Item>() {
                            @Override
                            public int compare(Item o1, Item o2) {
                                return o1.name.compareTo(o2.name);
                            }
                        });
                        break;
                    case "Filter items by count":
                        //code
                        buttonFilterItems.setText("Filter items by weight");
                        Collections.sort(items, new Comparator<Item>() {
                            @Override
                            public int compare(Item o1, Item o2) {
                                if (o1.count<o2.count) {
                                    return -1;
                                }
                                return 1;
                            }
                        });
                        break;
                    case "Filter items by weight":
                        //code
                        buttonFilterItems.setText("Filter items alphabetically");
                        Collections.sort(items, new Comparator<Item>() {
                            @Override
                            public int compare(Item o1, Item o2) {
                                if (o1.weight<o2.weight) {
                                    return -1;
                                }
                                return 1;
                            }
                        });
                        break;
                }
                createListAndTextView(selectedJourney, items);
            }
        });


        //NEW FEATURES
        final Button buttonAddItem = view.findViewById(R.id.buttonAddItem);
        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BackpackManagerActivity.class);
                intent.putExtra("ACTION", "ADD_ITEM");
                startActivity(intent);
            }
        });

        ListView listView = view.findViewById(R.id.listItemsView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object selectedItemAsObj = parent.getItemAtPosition(position);
                Item selectedItem = Item.class.cast(selectedItemAsObj);

                Intent intent = new Intent(context, BackpackManagerActivity.class);
                intent.putExtra("ACTION", "EDIT_OR_REMOVE_ITEM");
                intent.putExtra("EDITABLE_ITEM", selectedItem.PassItemToIntent());
                startActivity(intent);
            }
        });
        return view;
    }

    void createListAndTextView(String selectedJourney, LinkedList<Item> items) {
        ItemListAdapter adapter = new ItemListAdapter(context, R.layout.adapter_view_layout, items);
        ListView listView = view.findViewById(R.id.listItemsView);
        listView.setAdapter(adapter);

        TextView topText = view.findViewById(R.id.journeyTitleTextView);
        String topMessage = "You're going to: " + selectedJourney + "\n" +
                "Don't forget to pack your items!";
        topText.setText(topMessage);
    }

    void createNotification(String titleMessage, String contentMessage) {
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(titleMessage)
                .setContentText(contentMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        Intent intent = new Intent(context, secondpage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.navigateToBackpack.key, Constants.navigateToBackpack.value);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
            channel1.setDescription("just a channel 1");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

    String GetSelectedJourney(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String selectedJourney = settings.getString("SELECTED_JOURNEY", "NULL");
        return selectedJourney;
    }

    void HandleWhenJourneyIsNotSelected(){
        TextView topText = view.findViewById(R.id.journeyTitleTextView);
        ListView listView = view.findViewById(R.id.listItemsView);
        Button generateNot = view.findViewById(R.id.generateNotificationButton);
        Button postOnFb = view.findViewById(R.id.generatePostOnFacebookButton);
        Button filterItems = view.findViewById(R.id.buttonFilterItems);
        EditText editText = view.findViewById(R.id.editTextTextSortItems);
        Button addExtraItem = view.findViewById(R.id.buttonAddItem);

        topText.setText("Please choose your journey");
        listView.setVisibility(View.GONE);
        generateNot.setVisibility(View.GONE);
        postOnFb.setVisibility(View.GONE);
        filterItems.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        addExtraItem.setVisibility(View.GONE);
    }
}