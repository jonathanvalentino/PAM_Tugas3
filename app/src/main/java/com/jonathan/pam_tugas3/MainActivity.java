package com.jonathan.pam_tugas3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseFirestore db;
    private Button containedButton;
    private EditText senderName, numCell;
    private ImageView typeFood, typeCloth, typeMedicine, typeDocument;
    private TextView typeOther;
    private static final int REQUEST_CODE = 101;
    private TextView txtAddress, txtOrderId;
    public String jenis, orderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtAddress = findViewById(R.id.txtAddress);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();
        fetchLocation();

        typeFood = findViewById(R.id.typeFood);
        typeDocument = findViewById(R.id.typeDocument);
        typeMedicine = findViewById(R.id.typeMedicine);
        typeCloth = findViewById(R.id.typeCloth);
        typeOther = findViewById(R.id.typeOther);
        senderName = findViewById(R.id.senderName);
        numCell = findViewById(R.id.numCell);
        containedButton = findViewById(R.id.containedButton);
        txtOrderId = findViewById(R.id.txtOrderId);

        typeFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeFood.setColorFilter(Color.argb(255, 255, 165, 0));
                typeDocument.clearColorFilter();
                typeMedicine.clearColorFilter();
                typeCloth.clearColorFilter();
                typeOther.setTextColor(Color.argb(255, 169, 169, 169));
                jenis = "Food";
            }
        });

        typeDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeFood.clearColorFilter();
                typeDocument.setColorFilter(Color.argb(255, 105, 144, 29));
                typeMedicine.clearColorFilter();
                typeCloth.clearColorFilter();
                typeOther.setTextColor(Color.argb(255, 169, 169, 169));
                jenis = "Document";
            }
        });

        typeMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeFood.clearColorFilter();
                typeDocument.clearColorFilter();
                typeMedicine.setColorFilter(Color.argb(255, 138, 3, 3));
                typeCloth.clearColorFilter();
                typeOther.setTextColor(Color.argb(255, 169, 169, 169));
                jenis = "Medicine";
            }
        });

        typeCloth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeFood.clearColorFilter();
                typeDocument.clearColorFilter();
                typeMedicine.clearColorFilter();
                typeCloth.setColorFilter(Color.argb(255, 28, 94, 144));
                typeOther.setTextColor(Color.argb(255, 169, 169, 169));
                jenis = "Cloth";
            }
        });

        typeOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeFood.clearColorFilter();
                typeDocument.clearColorFilter();
                typeMedicine.clearColorFilter();
                typeCloth.clearColorFilter();
                typeOther.setTextColor(Color.argb(255, 64, 224, 208));
                jenis = "Other";
            }
        });

        containedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( TextUtils.isEmpty(senderName.getText())){
                    senderName.setError( "Sender name is required!" );
                }
                else if(TextUtils.isEmpty(numCell.getText())){
                    numCell.setError( "Sender phone number is required!" );
                }
                else if(TextUtils.isEmpty(jenis)){
                    Toast.makeText(getApplicationContext(),"Pilih jenis barang yang akan dikirim", Toast.LENGTH_LONG).show();
                }
                else{
                    //Execute to firebase db and store sender data
                    saveOrder();

                }

            }
        });
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MainActivity.this);
                }
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Your location pickup point");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        googleMap.addMarker(markerOptions);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null) {
                Address place = addresses.get(0);
                StringBuilder street = new StringBuilder();

                for (int i=0; i <= place.getMaxAddressLineIndex(); i++) {
                    street.append(place.getAddressLine(i)).append("\n");
                }

                txtAddress.setText(street.toString());
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }

    private void saveOrder() {
        Map<String, Object> order = new HashMap<>();
        Map<String, Object> pengirim = new HashMap<>();

        String name = senderName.getText().toString();

        pengirim.put("Sender name ", senderName.getText().toString());
        pengirim.put("Sender phone number", numCell.getText().toString());
        pengirim.put("Sender pickup address", txtAddress.getText().toString());

        order.put("name", name);
        order.put("createdDate", new Date());
        order.put("jenis barang", jenis);
        order.put("pengirim", pengirim);



            db.collection("orders")
                    .add(order)
                    .addOnSuccessListener(documentReference -> {
                        txtOrderId.setText(documentReference.getId());
                        orderId = documentReference.getId();
                        startActivity(new Intent(getApplicationContext(), RecipientActivity.class)
                                .putExtra("orderId", orderId));
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Gagal tambah data order", Toast.LENGTH_SHORT).show();
                    });


        }
    }
