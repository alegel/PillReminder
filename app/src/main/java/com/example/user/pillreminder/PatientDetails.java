package com.example.user.pillreminder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PatientDetails extends AppCompatActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int exists =0;
    private Handler handler = new Handler();

    private TextView txtName;
    private TextView txtPhone;
    private Button btnSign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        txtName = (TextView) findViewById(R.id.editName);
        if(txtName != null)
            txtName.setHintTextColor(Color.BLACK);
        txtPhone = (TextView) findViewById(R.id.editPhone);
        btnSign = (Button) findViewById(R.id.btnSign);

        final Intent intent = getIntent();
        final String mPhoneNumber = intent.getStringExtra("phone");
        exists = intent.getIntExtra(MainActivity.PATIENT_EXISTS,0);
        if(mPhoneNumber != null && !mPhoneNumber.isEmpty()){
            txtPhone.setHintTextColor(Color.BLACK);
            txtPhone.setHint(mPhoneNumber);
        }

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtName.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter the name", Toast.LENGTH_LONG).show();
                    return;
                }
                if(txtPhone.getText().toString().isEmpty() && txtPhone.getHint().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter your phone number", Toast.LENGTH_LONG).show();
                    return;
                }
                if(exists == 0) {
                    SaveDetails();
                }
                Intent intentDetails = new Intent();
                intentDetails.putExtra("name",txtName.getText().toString());
                intentDetails.putExtra("phone",mPhoneNumber);
                intentDetails.putExtra(MainActivity.PATIENT_COUNT, exists);
                setResult(RESULT_OK,intentDetails);

                finish();
//                startThread();
            }
        });
    }

    private void startThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(runnable);
            }
        }).start();
    }

    private void SaveDetails() {
        try {
            preferences = getSharedPreferences("userDetails", MODE_PRIVATE);
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Impossible readinng from the UserDetails file",Toast.LENGTH_LONG).show();
            return;
        }
        try {
            String strPhone = txtPhone.getText().toString();
            if(strPhone.isEmpty())
                strPhone = txtPhone.getHint().toString();
            editor = preferences.edit()
                    .putString(MainActivity.PATIENT_NAME,txtName.getText().toString())
                    .putString(MainActivity.PATIENT_PHONE, strPhone)
                    .putInt(MainActivity.PATIENT_COUNT, exists);
            editor.commit();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Impossible writing to the UserDetails file",Toast.LENGTH_LONG).show();
            return;
        }
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            finish();
        }
    };

}
