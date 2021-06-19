package com.example.personidentification;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;



public class edit_patient_details extends AppCompatActivity {

    private static final String TAG = "edit_patient_details";
    String patient_name,contact,medical,prescription,add_info;
    EditText name,phone,medical_history,prescription_taken,additional_info;
    Button btn_upload;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient_details);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        medical_history = findViewById(R.id.medical_history);
        prescription_taken = findViewById(R.id.presription_taken);
        additional_info = findViewById(R.id.additional_info);
        btn_upload=findViewById(R.id.upload);
        // pb=findViewById(R.id.pb);
        //pb.setVisibility(View.GONE);
        final ProgressDialog dialog = new ProgressDialog(edit_patient_details.this);
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        Python py = Python.getInstance();
        final PyObject pyobj = py.getModule("Algorithm");   // here give name of python file



        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                patient_name = name.getText().toString();
                contact = phone.getText().toString();
                medical = medical_history.getText().toString();
                prescription = prescription_taken.getText().toString();
                add_info = additional_info.getText().toString();

                if (name.getText().toString().isEmpty()) {
                    Toast.makeText(edit_patient_details.this, "name is mandatory", Toast.LENGTH_SHORT).show();
                    name.requestFocus();
                } else {
                    if(contact.isEmpty() || contact.length()==10){

                        ProgressDialog.show(edit_patient_details.this, "", "Please wait...Uploading", true);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final PyObject obj = pyobj.callAttr("edit_details",
                                        patient_name,
                                        contact,
                                        medical,
                                        prescription,
                                        add_info);
                                name.setText("");
                                phone.setText("");
                                medical_history.setText("");
                                prescription_taken.setText("");
                                additional_info.setText("");
                                Log.d(TAG, obj.toString());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        Intent myintent = new Intent(edit_patient_details.this, MainActivity.class);
                                        //myintent.putExtra("translate", str);
                                        startActivity(myintent);
                                        Toast.makeText(edit_patient_details.this, obj.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }).start();
                    }else{
                        Toast.makeText(edit_patient_details.this,"Not a valid number",Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

    }




    @Override
    protected void onResume() {
        super.onResume();
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