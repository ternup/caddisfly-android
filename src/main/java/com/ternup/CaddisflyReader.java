package com.ternup;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.location.LocationProvider;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.NameValuePair;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Random;
import java.util.Date;
import java.text.SimpleDateFormat;

public class CaddisflyReader extends Activity {

    private static String TAG = "caddisfly-android";
    private EditText location;
    private EditText testType;
    private EditText deviceId;
    private EditText testId;
    private EditText testResult;
    private EditText testTime;

    private Button submit;
    private Button connect;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
        setContentView(R.layout.relative);
        this.location = (EditText) findViewById(R.id.location);
        this.testId = (EditText) findViewById(R.id.test_id);
        this.testType = (EditText) findViewById(R.id.test_type);
        this.testResult = (EditText) findViewById(R.id.test_result);
        this.deviceId = (EditText) findViewById(R.id.device_id);
        this.testTime = (EditText) findViewById(R.id.test_time);

        this.submit = (Button) findViewById(R.id.submit);
        this.connect = (Button) findViewById(R.id.connect);

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null)
            loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (loc != null) {
            this.location.setText(loc.getLatitude() + "," + loc.getLongitude());
        } 
        LocationListener ls = new LocationListener() {
            @Override
            public void onProviderDisabled(String provider) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onLocationChanged(Location location) {
                Log.d("caddisfly-android", "location changed");
                CaddisflyReader.this.location.setText(location.getLatitude() + "," + location.getLongitude());
            }
        };

        Log.d(TAG, "requesting loc updates");
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, ls);
        setListeners();
    }

    public void showAlert(String message) {
        Toast toast = Toast.makeText(getApplicationContext(),
            message,
            Toast.LENGTH_LONG);
    }

    private void setListeners() {
        Log.d(TAG, "setting listeners");
        this.submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "submit clicked");
              new Thread(new Runnable() {
               public void run() {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://caddisfly.herokuapp.com/post");
                try {
                    List<NameValuePair> nvp = new ArrayList<NameValuePair>(7);
                    nvp.add(new BasicNameValuePair("device_id",
                        CaddisflyReader.this.deviceId.getText().toString()));
                    nvp.add(new BasicNameValuePair("test_id",
                        CaddisflyReader.this.testId.getText().toString()));
                    nvp.add(new BasicNameValuePair("test_type",
                        CaddisflyReader.this.testType.getText().toString()));
                    nvp.add(new BasicNameValuePair("test_time",
                        CaddisflyReader.this.testTime.getText().toString()));
                    nvp.add(new BasicNameValuePair("test_result",
                        CaddisflyReader.this.testResult.getText().toString()));
                    String location = CaddisflyReader.this.location.getText().toString();
                    String[] latlon = location.split(",");

                    nvp.add(new BasicNameValuePair("latitude", latlon[0]));
                    nvp.add(new BasicNameValuePair("longitude", latlon[1]));
                    
                    httpPost.setEntity(new UrlEncodedFormEntity(nvp));

                    final HttpResponse response = httpClient.execute(httpPost);
                    
                    Log.d(TAG, "post response: " + response.toString());
                    CaddisflyReader.this.runOnUiThread(new Runnable() {
                        public void run() {
                            CaddisflyReader.this.showAlert(response.toString());
                        }
                    });

                } catch (ClientProtocolException e) {
                    Log.e(TAG, "client protocol error: " + e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, "io exception: " + e.getMessage());
                }
            } //run()
            }).start(); //new Thread
            }
        });
        Log.d(TAG, "setting onclick listeners");
        this.connect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "connect clicked");
                Random rnd = new Random();
                String[] testTypes = {"fluoride", "arsenic", "nitrate", "turbidity"};
                String[] testResults = {"ok", "good", "poor", "bad", "needs testing"};
                CaddisflyReader.this.deviceId.setText(String.valueOf(rnd.nextInt(9999)));
                CaddisflyReader.this.testId.setText(String.valueOf(rnd.nextInt(9999)));
                CaddisflyReader.this.testType.setText(testTypes[rnd.nextInt(testTypes.length)]);
                CaddisflyReader.this.testResult.setText(testResults[rnd.nextInt(testResults.length)]);
                SimpleDateFormat ts = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                CaddisflyReader.this.testTime.setText(ts.format(new Date()));
            }
        });
    }
}

