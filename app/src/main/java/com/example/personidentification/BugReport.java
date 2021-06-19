package com.example.personidentification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BugReport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_report);
        Button startBtn = (Button) findViewById(R.id.sendEmail);
        final TextView t1=findViewById(R.id.t1);
        startBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendEmail(t1.toString());
            }
        });
    }
    protected void sendEmail(String t1) {
        Log.i("Send email", "");
        String[] TO = {"anubharathy00@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "BUG REPORT");
        emailIntent.putExtra(Intent.EXTRA_TEXT, t1);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(BugReport.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}