package com.example.barf_api_25_java.Activities.Main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
import com.example.barf_api_25_java.Data.Sync.GoogleDriveFileHolder;
import com.example.barf_api_25_java.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
//import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

    private void googleDriveTest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signIn();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_AUTHORIZATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == REQUEST_AUTHORIZATION) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            mDriveServiceHelper = new DriveServiceHelper(getDriveService(account));
            Task<GoogleDriveFileHolder> task =  mDriveServiceHelper.buckUpDatabase();
            task.addOnCompleteListener(new OnCompleteListener<GoogleDriveFileHolder>() {
                @Override
                public void onComplete(@NonNull Task<GoogleDriveFileHolder> task) {
                    Log.w(TAG, "Database bucked up");
                }
            });
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
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
    }
}
