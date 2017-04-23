package com.campusnavigation.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.campusnavigation.Model.MapRequest;
import com.campusnavigation.Model.Signal;
import com.campusnavigation.R;
import com.campusnavigation.Response.MapResponse;
import com.campusnavigation.Rest.ApiClient;
import com.campusnavigation.Rest.ApiInterface;
import com.campusnavigation.Tools.Tools;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private SupportMapFragment mapFragment;
    private double latitude =26.936281;
    private double longitude = 75.923496;
    private WifiManager wifiManager;
    private LocationManager locationManager;
    private boolean gpsEnabled= false;
    private List<ScanResult> wifiList = new ArrayList<>();
    private ArrayList<String> bssidList = new ArrayList<>();
    private ArrayList<String> ssidList = new ArrayList<>();
    private ArrayList<String> levelList = new ArrayList<>();
    private ArrayList<Signal> signalList = new ArrayList<>();
    private GoogleApiClient googleApiClient;
    private Location location;
    private String lat;
    private String log;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pd = Tools.getProgressDialog(MainActivity.this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<levelList.size();i++)
                {
                    Signal signal = new Signal();
                    signal.setMac(bssidList.get(i));
                    signal.setStrength(levelList.get(i));
                    signalList.add(signal);
                }
                MapRequest mapRequest = new MapRequest();
                mapRequest.setLatitude(lat);
                mapRequest.setLongitude(log);
                mapRequest.setSignalEntries(signalList);
                pd.show();
                ApiInterface apiService = ApiClient.getRetrofitClient().create(ApiInterface.class);
                retrofit2.Call<MapResponse> call = apiService.getLatLog(mapRequest);
                call.enqueue(new Callback<MapResponse>() {
                    @Override
                    public void onResponse(Call<MapResponse> call, Response<MapResponse> response) {
                        pd.dismiss();
                        Log.d("error",response.code()+"");
                        Toast.makeText(MainActivity.this,response.code()+"",Toast.LENGTH_SHORT).show();
                        if(response.code()==200)
                        {
                            Toast.makeText(MainActivity.this,response.body().getResLatitude()+","+response.body().getResLongitude(),Toast.LENGTH_SHORT).show();
                        }
                        if(response.code()==404)
                        {
                            Toast.makeText(MainActivity.this,"404: Not Found",Toast.LENGTH_SHORT).show();
                        }
                        if(response.code()==500)
                        {
                            Toast.makeText(MainActivity.this,"500:Server error",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MapResponse> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this,"UnSuccessful",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        askForPermission();
        if(!gpsEnabled)
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage("Please Enable GPS");
            dialog.setPositiveButton("Enable Location", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    buildGoogleApiClient();
                }
            });
            dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
        buildGoogleApiClient();
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(MainActivity.this, "Wifi Disabled", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        WifiScanReceiver receiver = new WifiScanReceiver();
        registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng marker = new LatLng(latitude,longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 17));
        googleMap.addMarker(new MarkerOptions().title("LNMIIT").position(marker));
    }

    class WifiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
                                wifiList = wifiManager.getScanResults();
            ssidList = new ArrayList<>();
            bssidList = new ArrayList<>();
            levelList = new ArrayList<>();
            for (int i = 0; i < wifiList.size(); i++) {
                ssidList.add(wifiList.get(i).SSID);
                bssidList.add(wifiList.get(i).BSSID);
                levelList.add(wifiList.get(i).level + "");
            }
        }
    }

    private void askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION
            },10);
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(location!=null)
        {
            lat =location.getLatitude()+"";
            log = location.getLongitude()+"";
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
