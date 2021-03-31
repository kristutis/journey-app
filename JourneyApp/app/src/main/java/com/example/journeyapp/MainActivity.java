package com.example.journeyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.FaceDetector;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button getStarted;
    VideoView video2;

    TextView welcome;
    ImageView profile;
    LoginButton login;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getStarted=(Button)findViewById(R.id.getStarted);
        video2 = (VideoView)findViewById(R.id.video2);

        String path = "android.resource://com.example.journeyapp/"+R.raw.nature;
        Uri u = Uri.parse(path);
        video2.setVideoURI(u);
        video2.start();

        video2.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.setVolume(0, 0);
            }
        });

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(MainActivity.this, secondpage.class);
                //a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(a);
            }
        });

        //fb stuff
        login = findViewById(R.id.login_button);
        welcome = findViewById(R.id.textView);
        profile = findViewById(R.id.profile);
        callbackManager = CallbackManager.Factory.create();
        checkLoginStatus();

        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onResume() {
        video2.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        video2.suspend();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        video2.stopPlayback();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker tracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken==null) {
                //jei useris logoutines
            } else {
                loadUserData(currentAccessToken);
            }
        }
    };

    void loadUserData(AccessToken token) {
        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
            String firstName;
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    firstName = object.getString("first_name");
                    welcome.setText("Welcome, " + firstName + "!");
                } catch (Exception e) {
                    firstName = e.getMessage();
                    welcome.setText(firstName);
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name");
        request.setParameters(parameters);
        request.executeAsync();

        String imageUrl = "https://graph.facebook.com/"+token.getUserId()+"/picture?return_ssl_resources=1";
        Picasso.get().load(imageUrl).into(profile);
    }

    void checkLoginStatus() {
        if (AccessToken.getCurrentAccessToken() != null) {
            loadUserData(AccessToken.getCurrentAccessToken());
        }
    }
}