package com.alegel.user.pillreminder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alegel.user.pillreminder.db_connection.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PatientDetails extends AppCompatActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int exists =0;
    private Handler handler = new Handler();
    private JSONParser jsonParser = new JSONParser();
    // Progress Dialog
    private ProgressDialog pDialog;
    TextView txtName;
    TextView txtPhone;
    String strName, strPhone;
    private Button btnSign, btnSaveToDB;

    // url to create new product
    private static String url_create_product = "http://10.0.2.2/PillReminder/php/patient_table/add_patient.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        txtName = (TextView) findViewById(R.id.editName);
        if(txtName != null)
            txtName.setHintTextColor(Color.BLACK);
        txtPhone = (TextView) findViewById(R.id.editPhone);
        btnSign = (Button) findViewById(R.id.btnSign);
        btnSaveToDB = (Button) findViewById(R.id.btnSaveToDB);


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

    public void SaveToDataBase(View view) {
        strName = txtName.getText().toString();
        strPhone = txtPhone.getText().toString();
        if(strPhone.isEmpty())
            strPhone = txtPhone.getHint().toString();
        new CreateNewPatient().execute();
    }

    /**
     * Background Async Task to Create new product
     * */
    class CreateNewPatient extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PatientDetails.this);
            pDialog.setMessage("Creating Patient..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", strName));
            params.add(new BasicNameValuePair("phone", strPhone));
            params.add(new BasicNameValuePair("server_id", "server_id123"));
            params.add(new BasicNameValuePair("pakage_id", "pakage_id123"));


            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_product,"POST", params);

            // check log cat fro response
            Log.d("Alex", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created patient
                    Log.d("Alex", "Successfully created patient");

                    // closing this screen
                    finish();
                } else {
                    // failed to create product
                    Log.d("Alex", "Failed to create patient");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }

}
