package com.jonathan.pam_tugas3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener{

    private EditText senderName, numCellSender, recipientName, numCellRecipient, jenis;
    private Button btnSave;
    private TextView txtLatitude, txtLongitude, address, txtAdressAwal;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String id = "";
    private GoogleMap gMap;
    private Marker selectedMarker;
    private LatLng selectedPlace;
    private Double latitude, longitude;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit);
            senderName = (EditText) findViewById(R.id.senderName);
            numCellRecipient = (EditText) findViewById(R.id.numCellRecipient);
            numCellSender = (EditText) findViewById(R.id.numCellSender);
            recipientName = (EditText) findViewById(R.id.recipientName);
            jenis = (EditText) findViewById(R.id.jenis);
            btnSave = findViewById(R.id.btn_save);
            txtLongitude = findViewById(R.id.txtLongitude);
            txtLatitude = findViewById(R.id.txtLatitude);
            address = findViewById(R.id.txtAddress);
            txtAdressAwal = findViewById(R.id.txtAddressAwal);

            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            assert supportMapFragment != null;
            supportMapFragment.getMapAsync(EditActivity.this);


            btnSave.setOnClickListener(v -> {
                if( TextUtils.isEmpty(recipientName.getText())){
                    recipientName.setError( "Recipient name is required!" );
                }
                else if(TextUtils.isEmpty(senderName.getText())){
                    senderName.setError( "Recipient phone number is required!" );
                }
                else if(TextUtils.isEmpty(numCellSender.getText())){
                    numCellSender.setError( "Recipient phone number is required!" );
                }
                else if(TextUtils.isEmpty(numCellRecipient.getText())){
                    numCellRecipient.setError( "Recipient phone number is required!" );
                }
                else if(TextUtils.isEmpty(jenis.getText())){
                    jenis.setError( "Recipient phone number is required!" );
                }
                else{
                    //set data2 ke db
                    DocumentReference order = db.collection("orders").document(id);
                    order.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                order.update("pengirim.Sender name",senderName.getText().toString());
                                order.update("pengirim.Sender phone number",numCellSender.getText().toString());
                                order.update("name", senderName.getText().toString());
                                order.update("penerima.Recipient name",recipientName.getText().toString());
                                order.update("penerima.Recipient phone number",numCellRecipient.getText().toString());

                                order.update("jenis barang", jenis.getText().toString());
                                if(selectedPlace != null) {
                                    order.update("penerima.latDelivery", selectedPlace.latitude);
                                    order.update("penerima.lngDelivery", selectedPlace.longitude);
                                }
                                order.update("penerima.Recipient delivery address", address.getText().toString());

                            }
                            }
                        });
                    startActivity(new Intent(getApplicationContext(), ListActivity.class)
                            .putExtra("success",true));
                }
            });

            Intent intent = getIntent();
            id = intent.getStringExtra("id");
            jenis.setText(id);


        }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        selectedPlace = latLng;
        selectedMarker.setPosition(selectedPlace);
        gMap.animateCamera(CameraUpdateFactory.newLatLng(selectedPlace));

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(selectedPlace.latitude, selectedPlace.longitude, 1);
            if (addresses != null) {
                Address place = addresses.get(0);
                StringBuilder street = new StringBuilder();

                for (int i=0; i <= place.getMaxAddressLineIndex(); i++) {
                    street.append(place.getAddressLine(i)).append("\n");
                }

                address.setText(street.toString());
                selectedMarker.setTitle(address.getText().toString());
            }
            else {
                Toast.makeText(this, "Could not find Address!", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(this, "Error get Address!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        DocumentReference order = db.collection("orders").document(id);
        order.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    senderName.setText(document.getString("name"));
                    jenis.setText(document.getString("jenis barang"));
                    Map<String, Object> pengirim = (Map<String, Object>) document.getData().get("pengirim");

                    numCellSender.setText(pengirim.get("Sender phone number").toString());
                    txtAdressAwal.setText(pengirim.get("Sender pickup address").toString());
                    Map<String, Object> penerima = (Map<String, Object>) document.getData().get("penerima");
                    numCellRecipient.setText(penerima.get("Recipient phone number").toString());
                    recipientName.setText(penerima.get("Recipient name").toString());
                    txtLatitude.setText(penerima.get("latDelivery").toString());
                    txtLongitude.setText(penerima.get("lngDelivery").toString());
                    gMap = googleMap;
                    latitude = Double.parseDouble(txtLatitude.getText().toString());
                    longitude = Double.parseDouble(txtLongitude.getText().toString());
                    LatLng latLng = new LatLng(latitude, longitude);

                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (addresses != null) {
                            Address place = addresses.get(0);
                            StringBuilder street = new StringBuilder();

                            for (int i=0; i <= place.getMaxAddressLineIndex(); i++) {
                                street.append(place.getAddressLine(i)).append("\n");
                            }

                            address.setText(street.toString());
                        }
                        else {
                            Toast.makeText(this, "Could not find Address!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e) {
                        Toast.makeText(this, "Error get Address!", Toast.LENGTH_SHORT).show();
                    }

                    selectedMarker = gMap.addMarker(new MarkerOptions().position(latLng ).title(address.getText().toString()));
                    gMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                    gMap.setOnMapClickListener(this);
                }
                else {
                    Toast.makeText(this, "Document does not exist!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}