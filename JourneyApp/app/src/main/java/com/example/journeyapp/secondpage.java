package com.example.journeyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.material.navigation.NavigationView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class secondpage extends AppCompatActivity {

    RoundedImageView menuProfileImage;
    TextView menuProfileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondpage);

        Intent intent = getIntent();
        String directions = intent.getStringExtra(Constants.navigateToBackpack.key);
        if (directions!=null) {
            NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
            navController.navigate(R.id.backpackFragment);
        }

        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        //navigationView.setItemIconTintList(null);

        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        final TextView textTitle = findViewById(R.id.textTitle);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                textTitle.setText(destination.getLabel());
            }
        });

        if (AccessToken.getCurrentAccessToken() != null) {
            //jei neatsijunge nuo fb
            AccessToken token = AccessToken.getCurrentAccessToken();
            loadUserData(token);
        }
    }

    void loadUserData(AccessToken token) {
        NavigationView navView = findViewById(R.id.navigationView);
        View headerLayout = navView.getHeaderView(0);
        menuProfileName = headerLayout.findViewById(R.id.header_fb_name);
        menuProfileImage = headerLayout.findViewById(R.id.imageProfile);

        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
            String firstName;
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    firstName = object.getString("first_name");
                    menuProfileName.setText(firstName);
                } catch (Exception e) {
                    firstName = e.getMessage();
                    menuProfileName.setText(firstName);
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name");
        request.setParameters(parameters);
        request.executeAsync();

        String imageUrl = "https://graph.facebook.com/"+token.getUserId()+"/picture?return_ssl_resources=1";
        Picasso.get().load(imageUrl).into(menuProfileImage);
    }


}