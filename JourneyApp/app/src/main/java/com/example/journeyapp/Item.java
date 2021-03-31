package com.example.journeyapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.LinkedList;

public class Item implements Comparable<Item>  {
    private final static String generatedItemsSMkey = "GENERATED_ITEMS";
    private final static String parser = ";";

    public String name;
    public double weight;
    public int count;

    public Item() {
        this.name = "NULL";
        this.weight = 0;
        this.count = 0;
    }

    public Item (String name, double weight) {
        this.name = name;
        this.weight = weight;
        this.count = 0;
    }

    public Item (String name, double weight, int count) {
        this.name = name;
        this.weight = weight;
        this.count = count;
    }

    @Override
    public int compareTo(Item item) {
        return this.name.compareTo(item.name);
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", weight=" + weight +
                ", count=" + count +
                '}';
    }

    public String PassItemToIntent() {
        return this.name+parser+
                this.weight+parser+
                this.count;
    }

    public static Item ParseFromIntent(String i) {
        String[] values = i.split(parser);
        Item item = new Item(values[0], Double.parseDouble(values[1]), Integer.parseInt(values[2]));
        return item;
    }

    public boolean equals(Item item) {
        if (this.name==item.name && this.count==item.count && this.weight==item.weight) {
            return true;
        }
        return false;
    }

    public boolean contains(String itemName) {
        if (this.name.contains(itemName)) {
            return true;
        }
        return false;
    }

    public static void SaveGeneratedItemsToSM(LinkedList<Item> items, Context context) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            sb.append(items.get(i).name).append(parser);
            sb.append(items.get(i).weight).append(parser);
            sb.append(items.get(i).count).append(parser);
        }
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(generatedItemsSMkey, sb.toString());
        editor.commit();
    }

    public static LinkedList<Item> GetGeneratedItemsFromSM(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String itemsInStringBuilder = settings.getString(generatedItemsSMkey, "NULL");
        if (itemsInStringBuilder.equals("NULL")) {
            return null;
        }
        LinkedList<Item> items = new LinkedList<Item>();
        String[] strings = itemsInStringBuilder.split(parser);
        for (int i=0; i<strings.length; i=i+3) {
            Item it = new Item();
            it.name=strings[i];
            it.weight=Double.parseDouble(strings[i+1]);
            it.count= Integer.parseInt(strings[i+2]);
            items.add(it);
        }
        return items;
    }
}
