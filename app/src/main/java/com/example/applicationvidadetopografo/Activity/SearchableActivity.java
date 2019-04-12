package com.example.applicationvidadetopografo.Activity;

import android.Manifest;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.applicationvidadetopografo.DAO.ConfiguracaoFirebase;
import com.example.applicationvidadetopografo.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchableActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private Context context;
    private LocationManager locationManager;
    private DatabaseReference reference;

    private String nome;
    private String latitude;
    private String longitude;
    private String profissaoAux;
    private String profissao;
    private ArrayList<String> ocupacao = new ArrayList<>();
    private ArrayList<String> ListAux = new ArrayList<>();
    private String strOcup;
    private Toolbar toolbar;
    private Double endLat;
    private Double endLong;
    private String aux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_searchable_activity, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView;
        MenuItem item = menu.findItem(R.id.action_searchable_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchView = (SearchView) item.getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint(getResources().getString(R.string.search_hint));
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();

        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        handleSearch2(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleSearch2(intent);
        marker.remove();

    }

    private void customAddMarker() {
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        nome = s.child("nome").getValue().toString();
                        latitude = s.child("latitude").getValue().toString();
                        longitude = s.child("longitude").getValue().toString();
                        ocupacao = (ArrayList<String>) s.child("ocupacao").getValue();
                        profissaoAux = ocupacao.toString();
                        profissaoAux = profissaoAux.replace("[", "");
                        profissao = profissaoAux.replace("]", "");
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();
                        if (ActivityCompat.checkSelfPermission(SearchableActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchableActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                        LatLng zoom2 = new LatLng(location.getLatitude(), location.getLongitude());
                        if (latitude == null && longitude == null) {
                            marker.remove();
                        } else {
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)))
                                    .title(nome)
                                    .snippet(profissao));
                        }
                    }
                } else {
                    Log.i("Eu passei aqui!", "Deu erro");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void handleSearch(Intent intent) {
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            String q = intent.getStringExtra(SearchManager.QUERY);
            filterLocation(q);
            customAddMarker();
        }
    }

    public void handleSearch2 (Intent intent) {
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            String q = intent.getStringExtra(SearchManager.QUERY);
            filterOcupacion(q);
        }
    }

    public void filterLocation(String q) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(q, 1);
            Address address = addresses.get(0);
            endLat = address.getLatitude();
            endLong = address.getLongitude();

        } catch (IOException e) {
            e.printStackTrace();
        }

        LatLng zoomFilter = new LatLng(endLat, endLong);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomFilter, 12));
    }

    public void filterOcupacion(String q) {
        reference = FirebaseDatabase.getInstance().getReference();
        aux = q;
        reference.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        nome = snapshot.child("nome").getValue().toString();
                        latitude = snapshot.child("latitude").getValue().toString();
                        longitude = snapshot.child("longitude").getValue().toString();
                        ocupacao = (ArrayList<String>) snapshot.child("ocupacao").getValue();
                        strOcup = ocupacao.toString();
                        strOcup = strOcup.replace("[", "");
                        strOcup = strOcup.replace("]", "");

                        if (strOcup.toLowerCase().startsWith(aux.toLowerCase()) || strOcup.toLowerCase().contains(aux.toLowerCase())) {
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)))
                                    .title(nome)
                                    .snippet(strOcup));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
