package com.shinhan.sspark.followtruck;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class CusMainActivity extends AppCompatActivity {

    SupportMapFragment mapFragment;
    GoogleMap map;
    int flag = 0;

    private LocationManager locationManager;
    private LocationListener locationListener;

    double latitude = 0.0;
    double longitude = 0.0;

    public ArrayList<BusinessInfo> businesslist = new ArrayList<BusinessInfo>();

    TextView businessid, name, context, hpno, snsid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_main);

        //  id초기화
        businessid = (TextView)findViewById(R.id.businessid);
        name    = (TextView)findViewById(R.id.name);
        context = (TextView)findViewById(R.id.context);
        hpno    = (TextView)findViewById(R.id.hpno);
        snsid   = (TextView)findViewById(R.id.snsid);

        Intent intent = getIntent(); // 이 액티비티를 부른 인텐트를 받는다.

        Log.i("seongsoo", intent.getStringExtra("ID").toString());

        getLocation();
        UpdateMap();
    }

    private void locateUpdate() {
        getLocation();
        // 사업장 정보리스트 가져오기
        new getBusinessList().execute(
                "http://172.16.2.3:52273/biz/");
    }

    private void UpdateMap(){
        SupportMapFragment mapFragment;

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback(){
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d("TAG", "Googlemap is ready");

                map = googleMap;

                // 현재위치
                LatLng userLocate = new LatLng(latitude, longitude);

                googleMap.clear();

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocate));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));

                for(int i=0; i < businesslist.size(); i++)
                {
                    BusinessInfo businessinfo = businesslist.get(i);
                    MarkerOptions usermarkerOptions = new MarkerOptions();
                    LatLng businessLocate = new LatLng(businessinfo.getLatitude(), businessinfo.getLongitude());
                    usermarkerOptions.position(businessLocate);
                    usermarkerOptions.title(businessinfo.getName());
                    usermarkerOptions.zIndex((float)i);
                    googleMap.addMarker(usermarkerOptions); // 현재위치
                }

                // 마커 클릭했을 떄 처리 : 리스너 달기
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        int index = (int)marker.getZIndex();
                        BusinessInfo businessinfo = businesslist.get(index);
                        // TODO Auto-generated method stub


                        new ComCheck().execute(
                                "http://172.16.2.3:52273/users/" + businessinfo.getBusinessid()
                        );

                        businessid.setText(businessinfo.getBusinessid());
                        name.setText(businessinfo.getName());
                        context.setText(businessinfo.getContext());

                        return false;
                    }
                });
            }
        });

        try {
            MapsInitializer.initialize(this);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void getLocation() {
        // 사용자의 위치 수신을 위한 세팅 //
        settingGPS();

        // 사용자의 현재 위치 //
        Location userLocation = getMyLocation();

        if( userLocation != null ) {
            // TODO 위치를 처음 얻어왔을 때 하고 싶은 것
            latitude = userLocation.getLatitude();
            longitude = userLocation.getLongitude();
        }
    }

    /**
     * GPS 를 받기 위한 매니저와 리스너 설정
     */
    private void settingGPS() {
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                // TODO 위도, 경도로 하고 싶은 것
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }

    /**
     * 사용자의 위치를 수신
     */
    private Location getMyLocation() {
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 사용자 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        // 수동으로 위치 구하기
        String locationProvider = LocationManager.GPS_PROVIDER;
        currentLocation = locationManager.getLastKnownLocation(locationProvider);
        if (currentLocation != null) {
            double lng = currentLocation.getLongitude();
            double lat = currentLocation.getLatitude();
            Log.d("Main", "longtitude=" + lng + ", latitude=" + lat);
        }

        return currentLocation;
    }

    class getBusinessList extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(CusMainActivity.this);

        @Override
        protected String doInBackground(String... params) {
            StringBuilder output = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                JSONObject postDataParams = new JSONObject();

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");

                    /* GET일땐 안쓴다 */
                    /*conn.setDoInput(true); conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));
                    writer.flush();
                    writer.close();
                    os.close();*/

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String line = null;
                    while(true) {
                        line = reader.readLine();
                        if (line == null) break;
                        output.append(line);
                    }
                    reader.close();
                    conn.disconnect();
                }
            } catch (Exception e) { e.printStackTrace(); }
            return output.toString();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("로드 중...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Log.i("result", s);

            Log.i("seongsoo", s);

            try {
                JSONArray jsonArray = new JSONArray(s);

                businesslist.clear();
                for(int i=0; i<jsonArray.length(); i++)
                {
                    JSONObject json = jsonArray.getJSONObject(i);

                    BusinessInfo businessInfo = new BusinessInfo();
                    businessInfo.setId(json.getInt("id"));
                    businessInfo.setBusinessid(json.getString("businessid"));
                    businessInfo.setName(json.getString("name"));
                    businessInfo.setContext(json.getString("context"));
                    businessInfo.setLatitude(json.getDouble("latitude"));
                    businessInfo.setLongitude(json.getDouble("longitude"));
                    businessInfo.setBusiness_state(json.getInt("business_state"));

                    businesslist.add(businessInfo);
                }
                dialog.dismiss();
                UpdateMap();


            } catch (Exception e) { e.printStackTrace(); }
        }
    }


    public void updateView(View view){
        locateUpdate();
    }

    public void bizInfo(View view){
        Intent intent = new Intent(CusMainActivity.this, CusMenuActivity.class);
        intent.putExtra("ID", businessid.getText().toString());
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_zero, R.anim.anim_slide_out_zero);
    }

    @Override
    public void onBackPressed() {
        if (flag == 0){
            Toast.makeText(
                    this,
                    "한번더 뒤로가기를 누르시면 종료됩니다.",
                    Toast.LENGTH_SHORT).show();
            flag++;
            handler.sendEmptyMessageDelayed(0, 1000*2);
        }
        else {
            super.onBackPressed();
        }
    }

    class ComCheck extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(CusMainActivity.this);
        @Override
        protected String doInBackground(String... params) {
            StringBuilder output = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                JSONObject postDataParams = new JSONObject();

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");

                    /* GET일땐 안쓴다 */
                    /*conn.setDoInput(true); conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));
                    writer.flush();
                    writer.close();
                    os.close();*/

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String line = null;
                    while(true) {
                        line = reader.readLine();
                        if (line == null) break;
                        output.append(line);
                    }
                    reader.close();
                    conn.disconnect();
                }
            } catch (Exception e) { e.printStackTrace(); }
            return output.toString();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("조회 중...");
            dialog.show();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Log.i("result", s);
            try {
                //JSONArray jsonArray = new JSONArray(s);
                //JSONObject json = jsonArray.getJSONObject(0);

                JSONObject json = new JSONObject(s);

                hpno.setText(json.getString("hpno"));
                snsid.setText(json.getString("snsid"));


            } catch (Exception e) { e.printStackTrace(); }
            dialog.dismiss();
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            flag = 0;
        }
    };
}

