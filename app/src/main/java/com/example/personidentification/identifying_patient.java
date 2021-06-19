package com.example.personidentification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class identifying_patient extends AppCompatActivity  {

    private static final int CAMERA_REQUEST = 121 ;

    TextView name,contact,medical_history,prescription_taken,additional_info;
    ImageView imageview;
    Button btn_take_pic,btn_upload;
    String mPath;
    public String currentimagepath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identifying_patient);

        // setAlert();
        imageview=findViewById(R.id.imageview_pic);
        btn_take_pic=findViewById(R.id.button1);
        btn_upload=findViewById(R.id.upload);
        name=findViewById(R.id.name);
        contact=findViewById(R.id.phone);
        medical_history=findViewById(R.id.medical_history);
        prescription_taken=findViewById(R.id.presription_taken);
        additional_info=findViewById(R.id.additional_info);
        //final ProgressDialog dialog = new ProgressDialog(identifying_patient.this);
        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }
        Python py = Python.getInstance();
        final PyObject pyobj = py.getModule("Algorithm");   // here give name of python file

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

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              btn_upload.setBackgroundColor(getResources().getColor(R.color.red));
                BitmapDrawable drawable = (BitmapDrawable) imageview.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                try {
                    File file = new File(mPath+ "/image.jpg");
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                //dialog.show(identifying_patient.this, "", "Please wait...Fetching", true);
                //final PyObject obj;
                final PyObject obj = pyobj.callAttr("find_patient",mPath+"/image.jpg");
                name.setText(obj.toString());
                Toast.makeText(identifying_patient.this,"Results are successfully fetched from database", Toast.LENGTH_SHORT).show();
                //btn_upload.setBackground(drawable.ge)
             /*final PyObject obj = pyobj.callAttr("find_patient",mPath+"/image.jpg");
              if (obj.toString()!="") {
                  dialog.dismiss();
                  name.setText(obj.toString());
                  Toast.makeText(identifying_patient.this,"Results are successfully fetched from database", Toast.LENGTH_SHORT).show();
                 // dialog.cancel();
                  //dialog.setOnCancelListener();
              }*/
                //name.setText(obj.toString());
                //Toast.makeText(identifying_patient.this,"Results are successfully fetched from database", Toast.LENGTH_SHORT).show();
            }
        });

        btn_take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = getImagefile();
                    } catch (IOException ex) {

                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(identifying_patient.this,
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                    }
                }
            }
        });

    }
/*
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

*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CAMERA_REQUEST){
            int targetW = imageview.getWidth();
            int targetH = imageview.getHeight();
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(currentimagepath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            Bitmap bitmap = BitmapFactory.decodeFile(currentimagepath, bmOptions);
            imageview.setImageBitmap(bitmap);
        }
    }

    private File getImagefile() throws IOException {
        String imageFileName =  "Image";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentimagepath = image.getAbsolutePath();
        return image;
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