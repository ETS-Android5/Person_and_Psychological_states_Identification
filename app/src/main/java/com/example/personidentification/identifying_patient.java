package com.example.personidentification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class identifying_patient extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE=123;
    private static final int CAMERA_REQUEST = 121 ;


    ImageView imageview;
    Button btn_gallery,btn_take_pic;
    String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identifying_patient);

        setAlert();
        imageview=findViewById(R.id.imageview_pic);
        btn_gallery=findViewById(R.id.button1);
        btn_take_pic=findViewById(R.id.button2);


        mPath = Environment.getExternalStorageDirectory() + "/PersonIdentifier/";
        Log.e("Path", mPath);
        File f = new File(mPath);
        if (!f.exists()) {
            f.mkdir();
        }

        File dir = new File("/storage/emulated/0/PersonIdentifier/");
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
        }


        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"pick an image"),GALLERY_REQUEST_CODE);
            }
        });

        btn_take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera,CAMERA_REQUEST);
            }
        });

    }

    private void setAlert() {

        AlertDialog.Builder al = new AlertDialog.Builder(identifying_patient.this);
        al.setTitle("Alert");
        al.setMessage("Make sure you only upload good quality image");
        al.setCancelable(true);

        al.setIcon(R.drawable.ic_launcher_background);

        al.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //  Toast.makeText(getApplicationContext(), "You clicked on YES :   ", Toast.LENGTH_SHORT).show();
            }
        });
        al.show();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE) {
            Uri imagedata = data.getData();
            imageview.setImageURI(imagedata);
        }

        if(requestCode==CAMERA_REQUEST){
            Bitmap photo=(Bitmap)data.getExtras().get("data");
            imageview.setImageBitmap(photo);
        }
    }

    public void upload(View view) throws IOException {

    // here match lbp of input input vht firebase images and output the results
       Toast.makeText(this,"results are successfully fetched from database", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.back_press,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.back) {
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }


}
