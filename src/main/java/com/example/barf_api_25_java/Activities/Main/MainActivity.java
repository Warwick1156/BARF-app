package com.example.barf_api_25_java.Activities.Main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barf_api_25_java.Activities.AddDog.AddDogActivity;
import com.example.barf_api_25_java.Data.DataBaseHelper;
import com.example.barf_api_25_java.Data.Dog;
import com.example.barf_api_25_java.Data.DogDatabaseHelper;
import com.example.barf_api_25_java.Data.MealDatabaseHelper;
import com.example.barf_api_25_java.Data.SettingsDatabaseHelper;
import com.example.barf_api_25_java.Data.Sync.DriveServiceHelper;
import com.example.barf_api_25_java.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.barf_api_25_java.Utils.ImageUtils.stringToBitmap;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_AUTHORIZATION = 10;
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<byte[]> images = new ArrayList<>();
    private ArrayList<Integer> ids = new ArrayList<>();

    RecyclerView dogsView;
    RecyclerViewAdapter dogsViewAdapter;

    DogDatabaseHelper dogDatabaseHelper;
    SettingsDatabaseHelper settingsDatabaseHelper;
    MealDatabaseHelper mealDatabaseHelper;

//    // The authority for the sync adapter's content provider
//    public static final String AUTHORITY = "com.example.barf_api_25_java.provider";
//    // An account type, in the form of a domain name
//    public static final String ACCOUNT_TYPE = "example.com";
//    // The account name
//    public static final String ACCOUNT = "default_account";
//    Account account;

    private static final int REQUEST_CODE_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    private DriveServiceHelper mDriveServiceHelper;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleDriveTest();
//        account = CreateSyncAccount(MainActivity.this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddDogActivity.class);
                startActivity(intent);
            }
        });

        try {
            DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
            initDogsView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        super.onResume();
        try {
            List<Dog> dogs = loadDogs();
            clearDogViewData();
            getDogViewData(dogs);
            dogsViewAdapter.updateItemList(names, images, ids);
            dogsViewAdapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<Dog> loadDogs() throws IOException {
        List<Dog> dogs = new ArrayList<>();
        try {
            settingsDatabaseHelper = new SettingsDatabaseHelper(MainActivity.this);
            mealDatabaseHelper = new MealDatabaseHelper(MainActivity.this);
            dogDatabaseHelper = new DogDatabaseHelper(MainActivity.this);
            dogs = dogDatabaseHelper.getDogs(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dogs;
    }

    private void initDogsView() throws IOException {
        List<Dog> dogs = loadDogs();
        getDogViewData(dogs);
        dogsView = findViewById(R.id.dogs_view);
        dogsViewAdapter = new RecyclerViewAdapter(this, names, images, ids);
        dogsView.setLayoutManager(new LinearLayoutManager(this));
        dogsView.setAdapter(dogsViewAdapter);

        registerForContextMenu(dogsView);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        dogDatabaseHelper.removeDogById(item.getGroupId());
        int position = dogsViewAdapter.getPosition();
        int databaseId = dogsViewAdapter.getItemDatabaseId();

        dogDatabaseHelper.removeDogById(databaseId);
        settingsDatabaseHelper.deleteSettings(databaseId);
        mealDatabaseHelper.deleteMealPlan(databaseId);

        dogsViewAdapter.removeItem(position);
        dogsViewAdapter.notifyItemRemoved(position);
        dogsViewAdapter.notifyItemRangeChanged(position, dogsViewAdapter.getItemCount());

        return super.onContextItemSelected(item);
    }

    private void getDogViewData(List<Dog> dogs) {
        byte[] defaultImage = getDefaultImage_asBit_array(ContextCompat.getDrawable(MainActivity.this, R.drawable.dog_silhouette));
        for (Dog dog : dogs) {
            names.add(dog.getDogName());
            ids.add(dog.getId());
            if (dog.getStringImage() == null) {
                images.add((defaultImage));
            } else {
                images.add(stringToBitmap(dog.getStringImage()));
            }
        }
    }

    private void clearDogViewData() {
        names.clear();
        images.clear();
        ids.clear();
    }

    private byte[] getDefaultImage_asBit_array(Drawable resource) {
        Bitmap bitmap = ((BitmapDrawable)resource).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

/*    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        *//*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         *//*
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            *//*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             *//*
            return newAccount;
        } else {
            *//*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             *//*
            return null;
        }
    }*/

//    protected void onStart() {
//        super.onStart();
//            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
//
//            if (account == null) {
//                signIn();
//            } else {
//                mDriveServiceHelper = new DriveServiceHelper(getDriveService(account));
//            }
//            Task<String> createFileTask = mDriveServiceHelper.createFile();
//            createFileTask.addOnCompleteListener(new OnCompleteListener<String>() {
//                @Override
//                public void onComplete(@NonNull Task<String> task) {
////                    try {
//                        String id = task.getResult();
////                    } catch (UserRecoverableAuthIOException e) {
////
////                    }
//                    System.out.println("Task completed");
//                }
//            });
//    }

/*    private void signIn() {

        mGoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                        .requestScopes(Drive.SCOPE_FILE)
                        .requestEmail()
                        .build();
        return GoogleSignIn.getClient(getApplicationContext(), signInOptions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                }
                break;


        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }

    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        Log.d(TAG, "Signed in as " + googleSignInAccount.getEmail());
//                        email.setText(googleSignInAccount.getEmail());

                        mDriveServiceHelper = new DriveServiceHelper(getDriveService(googleSignInAccount));

                        Log.d(TAG, "handleSignInResult: " + mDriveServiceHelper);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to sign in.", e);
                    }
                });
    }

    private Drive getDriveService(GoogleSignInAccount account) {
        GoogleAccountCredential credential = GoogleAccountCredential
                .usingOAuth2(getApplicationContext(), Collections.singleton(DriveScopes.DRIVE_FILE));

        credential.setSelectedAccount(account.getAccount());

        return new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential)
                .setApplicationName("BarfApp")
                .build();
    }*/














/*    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        // Get the app's Drive folder
        DriveResourceClient client = Drive.getDriveResourceClient(this, account);
        client.getAppFolder().addOnCompleteListener(this, new OnCompleteListener<DriveFolder>() {
            @Override
            public void onComplete(@NonNull Task<DriveFolder> task) {
                System.out.println("Test");
            }
        });

    }*/

    protected void googleDriveTest() {
        String token = "924077573187-fujbsfce8au1sfaum4gr3qkdl9u1h3v0.apps.googleusercontent.com";
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token)
                .requestScopes(Drive.SCOPE_FILE, Drive.SCOPE_APPFOLDER)
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
//        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);

        // This account must have the necessary scopes to make the API call
        //   See https://developers.google.com/identity/sign-in/android/
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        GoogleSignInAccount account = ;
//        GoogleSignIn.requestPermissions(this, 0, account, Drive.SCOPE_FILE);
      // Get the app's Drive folder
        DriveResourceClient client = Drive.getDriveResourceClient(this, account);
        client.getAppFolder().addOnCompleteListener(this, new OnCompleteListener<DriveFolder>() {

            @Override
            public void onComplete(@NonNull Task<DriveFolder> task) {

            }
        });
//        OAuth2();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                }
//                handleSignInResult(resultData);
                break;
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }

    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount account) {
                        DriveResourceClient client = Drive.getDriveResourceClient(MainActivity.this, account);
                        client.getAppFolder().addOnCompleteListener(MainActivity.this, new OnCompleteListener<DriveFolder>() {
                            @Override
                            public void onComplete(@NonNull Task<DriveFolder> task) {

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to sign in.", e);
                    }
                });
    }

/*    private void OAuth2() {
        Credential credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(TasksScopes.TASKS));
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        service = new com.google.api.services.tasks.Tasks.Builder(httpTransport, jsonFactory, credential)
                        .setApplicationName("BarfApp").build();
    }*/
}
