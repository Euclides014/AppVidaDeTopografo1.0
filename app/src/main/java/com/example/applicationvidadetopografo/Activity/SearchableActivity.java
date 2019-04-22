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
import android.os.Looper;
import android.provider.SearchRecentSuggestions;
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
import com.example.applicationvidadetopografo.Providers.SearchableProvider;
import com.example.applicationvidadetopografo.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
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

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class SearchableActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private LocationManager locationManager;
    private DatabaseReference reference;

    private String nome;
    private String latitude;
    private String longitude;
    private String profissaoAux, keyUser;
    private String profissao;
    private ArrayList<String> ocupacao = new ArrayList<>();
    private ArrayList<String> ListAux = new ArrayList<>();
    private String strOcup;
    private Toolbar toolbar;
    private Double endLat;
    private Double endLong;
    private Double latCurrent;
    private Double longCurrent;
    private String aux;

    private static final long UPDATE_INTERVAL = 10000;
    private static final long FASTEST_INTERVAL = 5000;
    public static final  int CONST_TELA_PERFIL = 1;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        startLocationUpdates();
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
        } else if( id == R.id.action_delete){
            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,
                    SearchableProvider.AUTHORITY,
                    SearchableProvider.MODE);
            searchRecentSuggestions.clearHistory();
            Toast.makeText(this, "Historico removido com sucesso", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                reference = ConfiguracaoFirebase.getFirebase();
                reference.child("usuarios").orderByChild("nome").equalTo(marker.getTitle())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    keyUser = snapshot.child("keyUsuario").getValue().toString();

                                }
                                Bundle params = new Bundle();
                                params .putString("keyUser", keyUser);

                                Intent intent = new Intent(getApplicationContext(), PerfilUsersActivity.class);
                                intent.putExtras(params);

                                startActivityForResult(intent, CONST_TELA_PERFIL);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });
        handleSearch2(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        marker.remove();
        handleSearch2(intent);


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
                        if (ActivityCompat.checkSelfPermission(SearchableActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchableActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        if (latitude == null && longitude == null && nome == null) {
                            marker.remove();
                        } else {
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)))
                                    .title(nome)
                                    .snippet(profissao));
                        }
                        Criteria criteria = new Criteria();
                        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                        if (location != null) {
                            LatLng zoom2 = new LatLng(location.getLatitude(), location.getLongitude());
                        } else {
                            LatLng zom3 = new LatLng(latCurrent, longCurrent);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void handleSearch2 (Intent intent) {
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            String q = intent.getStringExtra(SearchManager.QUERY);
            switch (q.toLowerCase()){
                case "topografo":
                    filterOcupacion(q);
                    SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,
                            SearchableProvider.AUTHORITY,
                            SearchableProvider.MODE);
                    searchRecentSuggestions.saveRecentQuery(q, null);
                    break;
                case "auxiliar":
                    filterOcupacion(q);
                    SearchRecentSuggestions searchRecentSuggestions2 = new SearchRecentSuggestions(this,
                            SearchableProvider.AUTHORITY,
                            SearchableProvider.MODE);
                    searchRecentSuggestions2.saveRecentQuery(q, null);
                    break;
                case "nivelador":
                    filterOcupacion(q);
                    SearchRecentSuggestions searchRecentSuggestions3 = new SearchRecentSuggestions(this,
                            SearchableProvider.AUTHORITY,
                            SearchableProvider.MODE);
                    searchRecentSuggestions3.saveRecentQuery(q, null);
                    break;
                case "piloto de drone":
                    filterOcupacion(q);
                    SearchRecentSuggestions searchRecentSuggestions4 = new SearchRecentSuggestions(this,
                            SearchableProvider.AUTHORITY,
                            SearchableProvider.MODE);
                    searchRecentSuggestions4.saveRecentQuery(q, null);
                    break;
                case "desenhista":
                    filterOcupacion(q);
                    SearchRecentSuggestions searchRecentSuggestions5 = new SearchRecentSuggestions(this,
                            SearchableProvider.AUTHORITY,
                            SearchableProvider.MODE);
                    searchRecentSuggestions5.saveRecentQuery(q, null);
                    break;
                case "projetista":
                    filterOcupacion(q);
                    SearchRecentSuggestions searchRecentSuggestions6 = new SearchRecentSuggestions(this,
                            SearchableProvider.AUTHORITY,
                            SearchableProvider.MODE);
                    searchRecentSuggestions6.saveRecentQuery(q, null);
                 default:
                     filterLocation(q);
                     customAddMarker();
                     SearchRecentSuggestions searchRecentSuggestions7 = new SearchRecentSuggestions(this,
                             SearchableProvider.AUTHORITY,
                             SearchableProvider.MODE);
                     searchRecentSuggestions7.saveRecentQuery(q, null);
            }
        }
    }

    public void filterLocation(String q) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(q, 1);
            if (addresses != null){
                Address address = addresses.get(0);
                endLat = address.getLatitude();
                endLong = address.getLongitude();
            } else {
                LatLng zoomFilter = new LatLng(latCurrent, longCurrent);
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomFilter, 10));
                Toast.makeText(this, "Sem resultados para pesquisa", Toast.LENGTH_LONG).show();
            }

        }catch (IllegalArgumentException e) {
            e.printStackTrace();
            LatLng zoomFilter = new LatLng(latCurrent, longCurrent);
            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomFilter, 10));
            Toast.makeText(this, "Pesquisa inválida", Toast.LENGTH_LONG).show();

        }catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Sem resultados para pesquisa", Toast.LENGTH_LONG).show();
        }
        if (endLat == null && endLong == null ){
            LatLng zoomFilter = new LatLng(latCurrent, longCurrent);
            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomFilter, 10));
            Toast.makeText(this, "Sem resultados para pesquisa", Toast.LENGTH_LONG).show();
        } else {
            LatLng zoomFilter = new LatLng(endLat, endLong);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomFilter, 10));
        }
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

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        latCurrent =location.getLatitude();
        longCurrent = location.getLongitude();

    }
}
