package com.example.journeyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

public class BackpackManagerActivity extends AppCompatActivity {
    //toastai
    Item item;

    Intent intent;
    TextView titleText;
    EditText nameText;
    EditText countText;
    EditText weightText;

    Button applyButton;
    Button removeButton;
    Button addButton;

    ArrayList<String> selectedItems = new ArrayList<>();
    ListView checkboxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backpack_manager2);

        titleText = findViewById(R.id.textViewEditItemTitleText);
        nameText = findViewById(R.id.editTextEditItemName);
        countText = findViewById(R.id.editTextEditItemCount);
        weightText = findViewById(R.id.editTextEditItemWeight);
        intent = getIntent();
        applyButton = findViewById(R.id.buttonApplyChanges);
        removeButton = findViewById(R.id.buttonRemoveItem);
        addButton = findViewById(R.id.buttonAddItem);

        checkboxes = findViewById(R.id.checkable_list);

        String action = intent.getStringExtra("ACTION");
        if (action.equals("EDIT_OR_REMOVE_ITEM")) {
            addButton.setVisibility(View.GONE);

            String itemInStringFormat = intent.getStringExtra("EDITABLE_ITEM");
            item = Item.ParseFromIntent(itemInStringFormat);

            titleText.setText("Edit or remove item: \n"+item.name);
            nameText.setText(item.name);
            countText.setText(String.valueOf(item.count));
            weightText.setText(String.valueOf(item.weight));

            checkboxes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            final int counter = item.count;
            String[] items = new String[counter];
            for (int i=0; i<counter; i++) {
                items[i]=String.valueOf(i+1);
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < counter; i++) {
                sb.append(items[i]).append(";");
            }
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("SELECTED_ITEMS", sb.toString());
            editor.commit();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rawlayout, R.id.txt_lan, items );
            checkboxes.setAdapter(adapter);
            checkboxes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem =((TextView)view).getText().toString();
                    Toast toast = Toast.makeText(getApplicationContext(), selectedItem, Toast.LENGTH_SHORT);
                    toast.show();

                    LinkedList<Item> allItems = Item.GetGeneratedItemsFromSM(getApplicationContext());
                    for (Item i : allItems) {
                        if (i.name==item.name) {
                            Item it = i;
                            it.count--;
                            i=it;
                            break;
                        }
                    }

                    Item.SaveGeneratedItemsToSM(allItems, getApplicationContext());
                }
            });

        } else if (action.equals("ADD_ITEM")) {
            applyButton.setVisibility(View.GONE);
            removeButton.setVisibility(View.GONE);
            TextView oras = findViewById(R.id.textViewOR);
            oras.setVisibility(View.GONE);
            titleText.setText("Please add a new item");
        }


        //add functionality
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkedList<Item> items = Item.GetGeneratedItemsFromSM(getApplicationContext());
                String oldName = item.name;
                Item newItem = new Item(nameText.getText().toString(), Double.parseDouble(weightText.getText().toString()), Integer.parseInt(countText.getText().toString()));
                for (Item i : items) {
                    if (i.name.equals(oldName)) {
                        items.remove(i);
                        items.add(newItem);
                        break;
                    }
                }
                Item.SaveGeneratedItemsToSM(items, getApplicationContext());
                Toast.makeText(getApplicationContext(), "Changes to item "+oldName+" applied", Toast.LENGTH_SHORT).show();
                NavigateBack();
            }
        });

        //remove functionality
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkedList<Item> items = Item.GetGeneratedItemsFromSM(getApplicationContext());
                for (Item i : items) {
                    if (i.name.equals(item.name)) {
                        items.remove(i);
                        break;
                    }
                }
                Toast.makeText(getApplicationContext(), "Item "+item.name+" removed", Toast.LENGTH_SHORT).show();
                Item.SaveGeneratedItemsToSM(items, getApplicationContext());
                NavigateBack();
            }
        });

        //add new item functionality
        nameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameText.setText("");
            }
        });

        countText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countText.setText("");
            }
        });

        weightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightText.setText("");
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item newItem = new Item();
                try {
                    newItem = new Item(nameText.getText().toString(), Double.parseDouble(weightText.getText().toString()), Integer.parseInt(countText.getText().toString()));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Cannot add item", Toast.LENGTH_SHORT).show();
                    return;
                }
                LinkedList<Item> items = Item.GetGeneratedItemsFromSM(getApplicationContext());
                items.add(newItem);
                Item.SaveGeneratedItemsToSM(items, getApplicationContext());
                Toast.makeText(getApplicationContext(), "Item "+newItem.name+" added successfully", Toast.LENGTH_SHORT).show();
                NavigateBack();
            }
        });
    }

    void NavigateBack() {
        Intent intent = new Intent(getApplicationContext(), secondpage.class);
        intent.putExtra(Constants.navigateToBackpack.key, Constants.navigateToBackpack.value);
        startActivity(intent);
    }
}