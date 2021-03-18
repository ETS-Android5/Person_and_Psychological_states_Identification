package com.example.personidentification;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
        //implements NavigationView.OnNavigationItemSelectedListener
    {

    protected DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView welcome,welcome1;
    Toast t;
    private static final int MULTIPLE_PERMISSIONS =100 ;

    //store permissions in an array
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    Button upload_patient_details,identify_btn, depression_btn;


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //System.loadLibrary("jniLibs");

        identify_btn = findViewById(R.id.btnIdentify);
        depression_btn = findViewById(R.id.btnDepression);
        upload_patient_details = findViewById(R.id.btnUpload);

        welcome=findViewById(R.id.wel);
        welcome1=findViewById(R.id.welcome1);


        toolbar=findViewById(R.id.toolbar);
      //  String text = "<font color=#FFFFFF>P&P</font>";
      //  toolbar.setTitle(Html.fromHtml(text));
         //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //String s = "<font color=#FF0000>Team</font><font color=#000000>ML</font>";
        //welcome.setText(Html.fromHtml(s));




        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_id);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        upload_patient_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"button upload  pressed");
                if(checkPermissions()) {
                    Intent intent = new Intent(MainActivity.this, patient_record_upload.class);
                    startActivity(intent);
                }
            }
        });

        identify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"button identify pressed");
                if(checkPermissions()) {
                    Intent intent = new Intent(MainActivity.this, identifying_patient.class);
                    startActivity(intent);
                }
            }
        });

        depression_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "button depression detector pressed");
                if(checkPermissions()) {
                    Intent intent = new Intent(MainActivity.this, depression_detection.class);
                    startActivity(intent);
                }
            }

        });
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.
                    // Now you call here what ever you want :)
                } else {
                    String perStr = "";
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }   // permissions list of don't granted permission
                }
                return;
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }



   @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.edit_patient_details:
                Intent ia = new Intent(this,edit_patient_details.class);
                startActivity(ia);
                return true;


            case R.id.bug_report:

                Toast.makeText(this, "bug report", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.logout:
                Toast.makeText(this, "Logout..", Toast.LENGTH_SHORT).show();
                return true ;

        }

        return true;
    }



    private Boolean exit = false;
    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            },  3000); // if user pressess back again with in 3 seconds,close the application

        }
    }


}
