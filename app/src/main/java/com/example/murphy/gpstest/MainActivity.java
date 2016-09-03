package com.example.murphy.gpstest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements View.OnClickListener {
    Button restart;
    Gps localGps = new Gps();
    double lat, lng, speed, bearing, groundX, groundY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        restart = (Button)findViewById(R.id.restart);
        restart.setOnClickListener(this);
        final LocationManager locationManager;
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(svcName);

        final String provider = LocationManager.GPS_PROVIDER;
        int t = 1000; //ms
        float distance = 0.5f; //m

        Intent intent = getIntent();
        final String suffix = intent.getStringExtra("filename");

        updateWithNewLocation(suffix, locationManager.getLastKnownLocation(provider));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationListener myLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                updateWithNewLocation(suffix, locationManager.getLastKnownLocation(provider));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(provider, 0, 0, myLocationListener);
        /*while (true){
            locationManager.requestLocationUpdates(provider, 0, 0, myLocationListener);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

    }

    private void updateWithNewLocation(String suffix, Location location){
        TextView myLocationText;
        myLocationText = (TextView)findViewById(R.id.myLocationText);

        String latLongString = "No location found";
        String latLongFile = null;
        if (location != null){
            Toast.makeText(MainActivity.this, "出现GPS信号", Toast.LENGTH_SHORT).show();
            lat = location.getLatitude();
            lng = location.getLongitude();
            speed = location.getSpeed();
            bearing = location.getBearing();
            localGps.setLatitude(lat);
            localGps.setLongitude(lng);
            localGps.geodeticToCartesian();
            groundX = localGps.getX();
            groundY = localGps.getY();

            //long timeUTC = location.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
            String time = sdf.format(new java.util.Date());
            latLongString =  "\ntime:" + time+ "Lat:" + lat + "\nLong:" + lng +
                    "\ngroundX:" + groundX + "\ngroundY:" + groundY + "\nSpeed:" + speed;
            latLongFile = time + " " + lat + " "+ lng + " "+ groundX + " " + groundY +
                    " " + speed + "\n";
        }else {
            Toast.makeText(MainActivity.this, "当前无GPS信号", Toast.LENGTH_SHORT).show();
        }

        myLocationText.setText("Current Position is:\n" + latLongString);
        writeFile("gpsTest" + suffix + ".txt", latLongFile);
    }

    private void writeFile(String fileName, String str) {
        try {
              Toast.makeText(MainActivity.this, "写入数据", Toast.LENGTH_SHORT).show();
              FileOutputStream fout = openFileOutput(fileName, MODE_APPEND);
              String path = Environment.getDataDirectory()+"";
              fout.write(str.getBytes());
              fout.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void writeSDCard(String fileName, String str) {
        try {
            // if the SDcard exists 判断是否存在SD卡
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                // get the directory of the SDcard 获取SD卡的目录
                File sdDire = Environment.getExternalStorageDirectory();
                FileOutputStream outFileStream = new FileOutputStream(
                        sdDire.getCanonicalPath() + "/" + fileName, true);
                outFileStream.write(str.getBytes());
                outFileStream.close();
                Toast.makeText(MainActivity.this, "写入一条数据", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this, "请插入SD卡", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.restart:
                this.finish();
                break;
        }
    }
}
