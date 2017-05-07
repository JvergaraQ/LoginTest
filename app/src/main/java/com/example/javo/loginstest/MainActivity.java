package com.example.javo.loginstest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView uidTextView;
    private ImageView photoImageView;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        uidTextView = (TextView) findViewById(R.id.uidTextView);
        photoImageView = (ImageView) findViewById(R.id.imageView);
        /*
        if(AccessToken.getCurrentAccessToken() == null){
            goLoginScreen();
        }
        */

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String photoUrl = user.getPhotoUrl().toString();
            String uid = user.getUid();

            //new ImageLoadTask(photoUrl,photoImageView).execute();
            Glide.with(this).load(photoUrl).into(photoImageView);

            nameTextView.setText(name);
            emailTextView.setText(email);
            uidTextView.setText(uid);
        } else {
            goLoginScreen();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    /*
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }
    */

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {

            GoogleSignInAccount account = result.getSignInAccount();

            nameTextView.setText(account.getDisplayName());
            emailTextView.setText(account.getEmail());
            uidTextView.setText(account.getId());

            Glide.with(this).load(account.getPhotoUrl()).into(photoImageView);

        } else {
            goLoginScreen();
        }
    }
    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goLoginScreen();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.not_close_session, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /*
    public void revoke(View view) {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goLoginScreen();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.not_revoke, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "La conexión con google ha fallado", Toast.LENGTH_SHORT).show();
    }

}
