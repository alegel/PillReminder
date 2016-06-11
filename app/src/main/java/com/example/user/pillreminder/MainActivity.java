package com.example.user.pillreminder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.pillreminder.patients.Patient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, View.OnFocusChangeListener, AdapterView.OnItemSelectedListener {
    public static final String PATIENT_NAME = "name";
    public static final String PATIENT_PHONE = "phone";
    public static final String GROUP = "group";
    public static final String PATIENT_COUNT = "count";
    public static final String PATIENT_EXISTS = "user_exists";
    public static final String LOG_TAG = "Alex";
    public static final int FIRST_ACCESS = 1;
    public static final int CHANGE_DETAILS = 2;
    public static final int PATIENTS_GROUP = 3;

    private Button btnMed, btnHistory, btnProcedures,  btnDoctors, btnSchedule, btnMeasurements, btnChange;
    private TextView eName, ePhone;

    private ArrayList<Patient> patientGrpList = new ArrayList<>();
    private Patient patient;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int count = 0, selectedPatient = 0;
    Spinner spinNames;
    String[] data = {"one", "two", "three", "four", "five"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Initialize();

        SetUserNameAndPhone();
        ShowNameAndPhone();
    }

    private void SetUserNameAndPhone() {
        try {
            preferences = getSharedPreferences("userDetails", MODE_PRIVATE);
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Impossible readinng from the UserDetails file", Toast.LENGTH_LONG).show();
            return;
        }
        String name, phone;
        name = preferences.getString(MainActivity.PATIENT_NAME,"error");
        phone = preferences.getString(MainActivity.PATIENT_PHONE,"error");
        selectedPatient = preferences.getInt(MainActivity.PATIENT_COUNT,0);
        if(name.equals("error") || phone.equals("error")){
            // First access, the name should be set
            Intent intent = new Intent(this, PatientDetails.class);
            TelephonyManager tMgr = (TelephonyManager)getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();
            intent.putExtra("phone",mPhoneNumber);
            intent.putExtra(PATIENT_EXISTS,0);  // First patient
            startActivityForResult(intent,FIRST_ACCESS);
        }
//        else {
//            //eName.setText(name);  spinNames.setSelection(count);
//            ePhone.setText(phone);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            String name = data.getStringExtra("name");
            String phone = data.getStringExtra("phone");
            int ind = data.getIntExtra(PATIENT_COUNT, 0);
            patient = new Patient(name, phone);
            switch (requestCode) {
                case FIRST_ACCESS:
                    patientGrpList.clear();
                    patientGrpList.add(patient);
                    selectedPatient = 0;
                    break;
                case CHANGE_DETAILS:
//                patientGrpList.set(ind,patient);
                    SetUserNameAndPhone();
                    break;
                case PATIENTS_GROUP:
                    patientGrpList.clear();
                    patientGrpList = (ArrayList<Patient>) data.getSerializableExtra(GROUP);
                    count = patientGrpList.size();
                    ShowNameAndPhone();
                    break;
            }
        }

    }

    private void ShowNameAndPhone() {
        ArrayAdapter<Patient> adapter = new ArrayAdapter<Patient>(this, android.R.layout.simple_spinner_item, patientGrpList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinNames.setAdapter(adapter);
//        spinNames.setPrompt("Title");
        spinNames.setSelection(selectedPatient);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Alex","Add Patient menu selected");
        switch (item.getItemId()){
            case R.id.add_patient:
                Intent intent = new Intent(this,PatientsGroup.class);
                if(patientGrpList.size() > 0)
                    intent.putExtra(GROUP,patientGrpList);
                else
                    intent.putExtra(GROUP,new ArrayList<Patient>());
                startActivityForResult(intent,PATIENTS_GROUP);
    //            startActivity(intent);
                break;
        }


        return true;
    }

    private void Initialize() {
        btnMed = (Button) findViewById(R.id.btnMed);
        btnHistory = (Button) findViewById(R.id.btnHistory);
        btnProcedures = (Button) findViewById(R.id.btnProcedures);
        btnDoctors = (Button) findViewById(R.id.btnDoctors);
        btnSchedule = (Button) findViewById(R.id.btnSchedule);
        btnMeasurements = (Button) findViewById(R.id.btnMeasurements);
        btnChange = (Button) findViewById(R.id.btnChange);
        eName = (TextView) findViewById(R.id.editName);
        ePhone = (TextView) findViewById(R.id.editPhone);
        spinNames = (Spinner) findViewById(R.id.spinName);
        spinNames.setOnItemSelectedListener(this);
        btnChange.setOnClickListener(this);

        btnMed.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
        btnProcedures.setOnClickListener(this);
        btnSchedule.setOnClickListener(this);
        btnDoctors.setOnClickListener(this);
        btnMeasurements.setOnClickListener(this);

        btnMed.setOnTouchListener(this);
        btnHistory.setOnTouchListener(this);
        btnProcedures.setOnTouchListener(this);
        btnDoctors.setOnTouchListener(this);
        btnSchedule.setOnTouchListener(this);
        btnMeasurements.setOnTouchListener(this);

        btnMed.setOnFocusChangeListener(this);
        btnProcedures.setOnFocusChangeListener(this);
        btnDoctors.setOnFocusChangeListener(this);
        btnSchedule.setOnFocusChangeListener(this);
        btnMeasurements.setOnFocusChangeListener(this);
        btnHistory.setOnFocusChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d("Alex","OnClick");
        switch (v.getId()){
            case R.id.btnChange:
                ChangeClicked();
                break;
        }

    }

    private void ChangeClicked() {
        Intent intent = new Intent(getApplicationContext(), PatientDetails.class);
        intent.putExtra(PATIENT_EXISTS,0);
        // count = choosen
        startActivityForResult(intent,CHANGE_DETAILS);

    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d("Alex", "event: " + event.getAction() );
        switch (v.getId()){
            case R.id.btnMed:
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    btnMed.setBackgroundResource(R.drawable.btn_pressed);
                else if(event.getAction() == MotionEvent.ACTION_UP)
                    btnMed.setBackgroundResource(R.drawable.btn_normal);
                break;
            case R.id.btnProcedures:
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    btnProcedures.setBackgroundResource(R.drawable.btn_pressed);
                else if(event.getAction() == MotionEvent.ACTION_UP)
                    btnProcedures.setBackgroundResource(R.drawable.btn_normal);
                break;
            case R.id.btnDoctors:
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    btnDoctors.setBackgroundResource(R.drawable.btn_pressed);
                else if(event.getAction() == MotionEvent.ACTION_UP)
                    btnDoctors.setBackgroundResource(R.drawable.btn_normal);
                break;
            case R.id.btnSchedule:
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    btnSchedule.setBackgroundResource(R.drawable.btn_pressed);
                else if(event.getAction() == MotionEvent.ACTION_UP)
                    btnSchedule.setBackgroundResource(R.drawable.btn_normal);
                break;
            case R.id.btnMeasurements:
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    btnMeasurements.setBackgroundResource(R.drawable.btn_pressed);
                else if(event.getAction() == MotionEvent.ACTION_UP)
                    btnMeasurements.setBackgroundResource(R.drawable.btn_normal);
                break;
            case R.id.btnHistory:
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    btnHistory.setBackgroundResource(R.drawable.btn_pressed);
                else if(event.getAction() == MotionEvent.ACTION_UP)
                    btnHistory.setBackgroundResource(R.drawable.btn_normal);
                break;
        }

        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.d("Alex","FocusChanged");
        switch (v.getId()){
            case R.id.btnDoctors:
                if(!hasFocus)
                    btnDoctors.setBackgroundResource(R.drawable.btn_focus);
                else
                    btnDoctors.setBackgroundResource(R.drawable.btn_normal);
                break;
            case R.id.btnMed:
                if(!hasFocus)
                    btnMed.setBackgroundResource(R.drawable.btn_focus);
                else
                    btnMed.setBackgroundResource(R.drawable.btn_normal);
                break;
            case R.id.btnProcedures:
                if(!hasFocus)
                    btnProcedures.setBackgroundResource(R.drawable.btn_focus);
                else
                    btnProcedures.setBackgroundResource(R.drawable.btn_normal);
                break;
            case R.id.btnSchedule:
                if(!hasFocus)
                    btnSchedule.setBackgroundResource(R.drawable.btn_focus);
                else
                    btnSchedule.setBackgroundResource(R.drawable.btn_normal);
                break;
            case R.id.btnMeasurements:
                if(!hasFocus)
                    btnMeasurements.setBackgroundResource(R.drawable.btn_focus);
                else
                    btnMeasurements.setBackgroundResource(R.drawable.btn_normal);
                break;
            case R.id.btnHistory:
                if(!hasFocus)
                    btnHistory.setBackgroundResource(R.drawable.btn_focus);
                else
                    btnHistory.setBackgroundResource(R.drawable.btn_normal);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
