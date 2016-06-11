package com.example.user.pillreminder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.user.pillreminder.patients.Patient;

import java.util.ArrayList;

public class PatientsGroup extends AppCompatActivity implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener, View.OnClickListener {
    private ListView listViewPatients;
    private ArrayAdapter arrayAdapter;
    private int selected = -1;
    private AlertDialog.Builder dlg;
    private ArrayList<Patient> patients = new ArrayList<Patient>();

    private EditText eName, ePhone;
    private Button btnAdd, btnEdit, btnDelete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        initFields();
    }

    private void initFields() {
        eName = (EditText) findViewById(R.id.ePatientName);
        ePhone = (EditText) findViewById(R.id.ePatientPhone);
        btnAdd = (Button) findViewById(R.id.btnAddPatient);
        btnEdit = (Button) findViewById(R.id.btnEditPatient);
        btnDelete = (Button) findViewById(R.id.btnDeletePatient);

        listViewPatients = (ListView) findViewById(R.id.listViewPatients);
        Intent intent = getIntent();
        patients = (ArrayList<Patient>) intent.getSerializableExtra(MainActivity.GROUP);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,patients);
        listViewPatients.setAdapter(arrayAdapter);

        listViewPatients.setOnItemClickListener(this);
        btnAdd.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        dlg = new AlertDialog.Builder(this);
        dlg.setTitle(R.string.error);
        dlg.setPositiveButton("Ok", (DialogInterface.OnClickListener) this);
        dlg.setNegativeButton("Cancel", (DialogInterface.OnClickListener) this);
        // Some changes
    }

    private void cleanFields() {
        eName.setText("");
        ePhone.setText("");
        eName.requestFocus();
    }

    private void showAlertDialog(int messageID){
        dlg.setMessage(messageID);
        AlertDialog alertDialog = dlg.create();
        alertDialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selected = position;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnAddPatient){
            if(eName.getText().toString().equals("")){
                showAlertDialog(R.string.name_empty);
                //Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show();
                eName.requestFocus();
                return;
            }
            if(ePhone.getText().toString().equals("")){
                showAlertDialog(R.string.phone_empty);
                //Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show();
                ePhone.requestFocus();
                //return;
            }
            Patient patient = new Patient(eName.getText().toString(),ePhone.getText().toString());
            patients.add(patient);
            arrayAdapter.notifyDataSetChanged();
            listViewPatients.setAdapter(arrayAdapter);
            cleanFields();
        }
        else if(v.getId() == R.id.btnEditPatient){
            if(selected < 0 || selected > arrayAdapter.getCount())
            {
                //Toast.makeText(this,"Please select the item to edit",Toast.LENGTH_LONG).show();
                showAlertDialog(R.string.select_item_edit);
                eName.requestFocus();
                return;
            }
            if(eName.getText().toString().equals("")){
                showAlertDialog(R.string.name_empty);
                //Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show();
                eName.requestFocus();
                return;
            }
            if(ePhone.getText().toString().equals("")){
                showAlertDialog(R.string.phone_empty);
                //Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show();
                ePhone.requestFocus();
                //return;
            }
            Patient patient = new Patient(eName.getText().toString(),ePhone.getText().toString());
            patients.set(selected,patient);
            selected = -1;
            arrayAdapter.notifyDataSetChanged();
            cleanFields();
        }
        else {
            if(selected < 0 || selected > arrayAdapter.getCount())
            {
                //Toast.makeText(this,"Please select the item to delete",Toast.LENGTH_LONG).show();
                showAlertDialog(R.string.select_item_delete);
                eName.requestFocus();
                return;
            }
            patients.remove(selected);
            arrayAdapter.notifyDataSetChanged();
            selected = -1;
            cleanFields();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Alex","OnDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Alex","onStop");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Alex","onPause");
    }

    @Override
    public void onBackPressed() {
 //4       super.onBackPressed();
        Log.d("Alex","onBackPressed");

        Intent intent = new Intent();
        intent.putExtra(MainActivity.GROUP, patients);
        setResult(RESULT_OK,intent);

        finish();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

}
