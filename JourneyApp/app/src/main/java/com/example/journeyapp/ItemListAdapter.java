package com.example.journeyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedList;

public class ItemListAdapter extends ArrayAdapter<Item> {
    Context context;
    int resource;

    public ItemListAdapter(Context context, int resource, LinkedList<Item> items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position).name;
        double weight = getItem(position).weight;
        int count = getItem(position).count;

        Item item = new Item(name, weight, count);
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView=inflater.inflate(resource, parent, false);

        TextView tvCount = convertView.findViewById(R.id.textView1);
        TextView tvName = convertView.findViewById(R.id.textView2);
        TextView tvWeight = convertView.findViewById(R.id.textView3);

        tvCount.setText(Integer.toString(count));
        tvName.setText(name);
        tvWeight.setText(Double.toString(weight)+" kg");

        return convertView;
    }
}
