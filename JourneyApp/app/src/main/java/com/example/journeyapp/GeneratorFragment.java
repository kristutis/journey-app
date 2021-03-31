package com.example.journeyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.internal.NavigationMenu;

import java.util.LinkedList;

import javax.xml.namespace.QName;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GeneratorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeneratorFragment extends Fragment implements RequestOperator.RequestOperatorListener, GenerateItemsRqOperator.RequestOperatorListener {
    private String[] journeys;
    private Item[] items;

    View view;
    Context context;
    Spinner spinner;
    IndicatingView indicator;
    ProgressBarIndicatingView whiteProgressIndicator;
    IndicatingView progressIndicator;
    Thread t;
//    boolean touched=false;
    String defaultChoice="choose your destination";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GeneratorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeneratorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeneratorFragment newInstance(String param1, String param2) {
        GeneratorFragment fragment = new GeneratorFragment();
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
        view = inflater.inflate(R.layout.fragment_generator, container, false);
        context = getActivity().getApplicationContext();
        spinner = view.findViewById(R.id.spinner1);

        String[] values = {defaultChoice};
        PopulateSpinner(values);

        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setProgressBarBelow();

                //draw in progress
                //----------------------------------------------------------------------------------
                indicator = view.findViewById(R.id.generated_graphic);
                setIndicatorStatus(IndicatingView.INPROGRESS);

                whiteProgressIndicator = view.findViewById(R.id.progress_bar_white);
                final int w = whiteProgressIndicator.getWidth();

                final Handler handler = new Handler();
                t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i=w-10; i>0; i-=10) {
                            final int x=i;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    drawInProgress(x);
                                }
                            });
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e)  {

                            }
                        }
                        drawInProgress(0);
                    }
                });
                t.start();
                //----------------------------------------------------------------------------------

                sendRequest();
                return false;
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String j = spinner.getSelectedItem().toString();
                //Toast.makeText(getApplicationContext(), j, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Toast.makeText(getApplicationContext(), "nothing", Toast.LENGTH_SHORT).show();
            }
        });

        final Button generateButton = view.findViewById(R.id.generateItemsButton);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedJourney = spinner.getSelectedItem().toString();
                if (selectedJourney==defaultChoice)
                {
                    Toast.makeText(context, "Please select a journey", Toast.LENGTH_SHORT).show();
                    return;
                }
                //pasirinko journey
                sendItemsRequest();

                //save journey to SM
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("SELECTED_JOURNEY", selectedJourney);
                editor.commit();

                //navigate to backpack after items and journey are saved
                Activity act = getActivity();
                NavController navController = Navigation.findNavController(act, R.id.navHostFragment);
                navController.navigate(R.id.backpackFragment);
            }
        });

        return view;
    }

    void PopulateSpinner(String[] values)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    void drawInProgress(final int w) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                whiteProgressIndicator.setTransWidth(w);
                whiteProgressIndicator.setState(ProgressBarIndicatingView.LOADING);
                whiteProgressIndicator.invalidate();
            }
        });
    }

    void drawSuccess() {
        indicator = view.findViewById(R.id.generated_graphic);
        setIndicatorStatus(IndicatingView.SUCCESS);
    }

    public void setIndicatorStatus(final int status) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                indicator.setState(status);
                indicator.invalidate();
            }
        });
    }

    private void sendRequest() {
        RequestOperator db = new RequestOperator();
        db.setListener(this);
        db.start();
    }

    @Override
    public void success(String[] j) {
        this.journeys = j;
        updateSpinner();
        drawSuccess();
    }

    @Override
    public void failed(int responseCode) {
        this.journeys=null;
    }

    private void updateSpinner() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] values = journeys;
                PopulateSpinner(values);
            }
        });
    }

    void setProgressBarBelow() {
        final ProgressBarIndicatingView progressBarBelow = view.findViewById(R.id.progress_bar);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarBelow.setState(ProgressBarIndicatingView.BAR_BELOW);
                progressBarBelow.invalidate();
            }
        });
    }

    void sendItemsRequest() {
        GenerateItemsRqOperator ro = new GenerateItemsRqOperator();
        ro.setListener(this);
        ro.start();
    }

    @Override
    public void success(Item[] items) {
        this.items = items;

        LinkedList<Item> generatedItems = new LinkedList<>();
        for (int i=0; i<items.length; i++) {
            generatedItems.add(items[i]);
        }

        Item.SaveGeneratedItemsToSM(generatedItems, context);
    }
}