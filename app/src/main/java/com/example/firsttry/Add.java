package com.example.firsttry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Add extends AppCompatActivity {

    private Spinner typSpinner, positionSpinner, kieferSpinner, farbeSpinner, formSpinner;
    private Button submitButton;
    private DBHelper dbHelper;
    public TextView Add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        typSpinner = findViewById(R.id.typ_spinner);
        positionSpinner = findViewById(R.id.position_spinner);
        kieferSpinner = findViewById(R.id.kiefer_spinner);
        farbeSpinner = findViewById(R.id.farbe_spinner);
        formSpinner = findViewById(R.id.form_spinner);
        submitButton = findViewById(R.id.submit_button);
        Add = findViewById(R.id.Add);
        dbHelper = new DBHelper(this);

        populateSpinners();
        setupSpinnerListeners();

        submitButton.setEnabled(false);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String addText = Add.getText().toString();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("add_text", addText);
                setResult(RESULT_OK, resultIntent);
                finish();

                Add.setText("");
                populateSpinners();
            }
        });
    }

    private void populateSpinners() {
        populateSpinner(typSpinner, "Zahnsorte auswählen:", dbHelper.getUniqueValuesForColumn("typ"));
        populateSpinner(positionSpinner, "Front/Backen auswählen:", dbHelper.getUniqueValuesForColumn("position"));
        populateSpinner(kieferSpinner, "OK/UK auswählen:", dbHelper.getUniqueValuesForColumn("kiefer"));
        populateSpinner(farbeSpinner, "Farbe auswählen:", dbHelper.getUniqueValuesForColumn("farbe"));
        populateSpinner(formSpinner, "Form auswählen:", dbHelper.getUniqueValuesForColumn("form"));
    }

    private void populateSpinner(Spinner spinner, String label, List<String> values) {
        values.add(0, label);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    private void setupSpinnerListeners() {
        typSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Add.setText("");
                String selectedTyp = parent.getItemAtPosition(position).toString();
                if (!selectedTyp.equals("Zahnsorte auswählen:")) {
                    populateSpinner(positionSpinner, "Front/Backen auswählen:", dbHelper.getFilteredValuesForColumns("position", new String[]{"typ"}, new String[]{selectedTyp}));
                    positionSpinner.setEnabled(true);
                } else {
                    positionSpinner.setEnabled(false);
                    kieferSpinner.setEnabled(false);
                    farbeSpinner.setEnabled(false);
                    formSpinner.setEnabled(false);
                    submitButton.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        positionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Add.setText("");
                String selectedPosition = parent.getItemAtPosition(position).toString();
                String selectedTyp = typSpinner.getSelectedItem().toString();
                if (!selectedPosition.equals("Front/Backen auswählen:")) {
                    populateSpinner(kieferSpinner, "OK/UK auswählen:", dbHelper.getFilteredValuesForColumns("kiefer", new String[]{"typ", "position"}, new String[]{selectedTyp, selectedPosition}));
                    kieferSpinner.setEnabled(true);
                } else {
                    kieferSpinner.setEnabled(false);
                    farbeSpinner.setEnabled(false);
                    formSpinner.setEnabled(false);
                    submitButton.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        kieferSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Add.setText("");
                String selectedKiefer = parent.getItemAtPosition(position).toString();
                String selectedTyp = typSpinner.getSelectedItem().toString();
                String selectedPosition = positionSpinner.getSelectedItem().toString();
                if (!selectedKiefer.equals("OK/UK auswählen:")) {
                    populateSpinner(farbeSpinner, "Farbe auswählen:", dbHelper.getFilteredValuesForColumns("farbe", new String[]{"typ", "position", "kiefer"}, new String[]{selectedTyp, selectedPosition, selectedKiefer}));
                    farbeSpinner.setEnabled(true);
                } else {
                    farbeSpinner.setEnabled(false);
                    formSpinner.setEnabled(false);
                    submitButton.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        farbeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Add.setText("");
                String selectedFarbe = parent.getItemAtPosition(position).toString();
                String selectedTyp = typSpinner.getSelectedItem().toString();
                String selectedPosition = positionSpinner.getSelectedItem().toString();
                String selectedKiefer = kieferSpinner.getSelectedItem().toString();
                if (!selectedFarbe.equals("Farbe auswählen:")) {
                    populateSpinner(formSpinner, "Form auswählen:", dbHelper.getFilteredValuesForColumns("form", new String[]{"typ", "position", "kiefer", "farbe"}, new String[]{selectedTyp, selectedPosition, selectedKiefer, selectedFarbe}));
                    formSpinner.setEnabled(true);
                } else {
                    formSpinner.setEnabled(false);
                    submitButton.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        formSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Add.setText("");
                String selectedForm = parent.getItemAtPosition(position).toString();
                if (!selectedForm.equals("Form auswählen:")) {

                    String typ = typSpinner.getSelectedItem().toString();
                    String pos = positionSpinner.getSelectedItem().toString();
                    String kiefer = kieferSpinner.getSelectedItem().toString();
                    String farbe = farbeSpinner.getSelectedItem().toString();
                    String form = formSpinner.getSelectedItem().toString();

                    String artikelnummer = dbHelper.getArtikelnummer(typ, pos, kiefer, farbe, form);
                    if (artikelnummer != null) {
                        // Toast.makeText(Add.this, "Artikelnummer: " + artikelnummer, Toast.LENGTH_LONG).show();
                        Add.setText(artikelnummer);
                    } else {
                        Toast.makeText(Add.this, "Keine passenden Artikel gefunden.", Toast.LENGTH_LONG).show();
                    }
                    submitButton.setEnabled(true);
                } else {
                    submitButton.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}