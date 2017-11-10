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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class BizManageActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;

    GoogleMap map;

    BusinessInfo businessInfo;

    int flag = 0;
    int id = 0;

    double latitude = 0.0;
    double longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biz_manage);

        Intent intent = getIntent(); // 이 액티비티를 부른 인텐트를 받는다.

        Log.i("seongsoo", intent.getStringExtra("ID").toString());
        businessInfo = new BusinessInfo();
        // 사업장 정보 가져오기
        new getBusinessInfo().execute(
                    "http://172.16.2.3:52273/biz/" + intent.getStringExtra("ID").toString()
            );

        getLocation();
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

                if (businessInfo.getLatitude() == latitude && businessInfo.getLongitude() == longitude)
                {
                    ;
                }
                else
                {

                    MarkerOptions usermarkerOptions = new MarkerOptions();
                    usermarkerOptions.position(userLocate);
                    usermarkerOptions.title("나의위치");
                    usermarkerOptions.snippet("영업시작을 눌러주세요.");
                    googleMap.addMarker(usermarkerOptions); // 현재위치
                }

                LatLng businessInfoLocate = new LatLng(businessInfo.getLatitude(), businessInfo.getLongitude());

                if (businessInfo.getLatitude() == 0.0 && businessInfo.getLongitude() == 0.0)
                {

                }
                else
                {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(businessInfoLocate);
                    markerOptions.title(businessInfo.getName().toString());
                    markerOptions.snippet(businessInfo.getContext().toString());
                    googleMap.addMarker(markerOptions); // 등록 위치
                }

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocate));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            }
        });

        try {
            MapsInitializer.initialize(this);
        } catch(Exception e) {
            e.printStackTrace();
        }
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
     * GPS 권한 응답에 따른 처리
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    boolean canReadLocation = false;
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
// success!
                Location userLocation = getMyLocation();
                if( userLocation != null ) {
// 다음 데이터 //
// todo 사용자의 현재 위치 구하기
                    latitude = userLocation.getLatitude();
                    longitude = userLocation.getLongitude();
                }
                canReadLocation = true;
            } else {
// Permission was denied or request was cancelled
                canReadLocation = false;
            }
        }
    }

    // 클릭이벤트
    public void locateUpdate(View view)
    {
        // 사업장 정보 가져오기
        // [ req.body.businessid, req.body.name, req.body.context, req.body.latitude,
        // req.body.longitude, req.body.business_state,req.params.id ],
        getLocation();
        businessInfo.setLatitude(latitude);
        businessInfo.setLongitude(longitude);
        businessInfo.setBusiness_state(1);

        new businessUpdate().execute(
                "http://172.16.2.3:52273/biz/" + businessInfo.getId(),
                businessInfo.getBusinessid(),
                businessInfo.getName(),
                businessInfo.getContext(),
                Double.toString(businessInfo.getLatitude()),
                Double.toString(businessInfo.getLongitude()),
                Integer.toString(businessInfo.getBusiness_state()));
    }

    public void webMove1(View view)
    {
        Intent intent = new Intent(BizManageActivity.this, BizWebViewActivity.class);
        intent.putExtra("ID", businessInfo.getBusinessid().toString());
        intent.putExtra("WEBMOVE", "1");
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_zero, R.anim.anim_slide_out_zero);
        finish();
    }

    public void webMove2(View view)
    {
        Intent intent = new Intent(BizManageActivity.this, BizWebViewActivity.class);
        intent.putExtra("ID", businessInfo.getBusinessid().toString());
        intent.putExtra("WEBMOVE", "2");
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_zero, R.anim.anim_slide_out_zero);
        finish();
    }

    public void webMove3(View view)
    {
        Intent intent = new Intent(BizManageActivity.this, BizWebViewActivity.class);
        intent.putExtra("ID", businessInfo.getBusinessid().toString());
        intent.putExtra("WEBMOVE", "3");
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_zero, R.anim.anim_slide_out_zero);
        finish();
    }

    public void menuinsert(View view)
    {
        Intent intent = new Intent(BizManageActivity.this, MenuInsertActivity.class);
        intent.putExtra("ID", businessInfo.getBusinessid().toString());
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
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

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            flag = 0;
        }
    };


    class getBusinessInfo extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(BizManageActivity.this);
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
            dialog.dismiss();
            // Log.i("result", s);

            Log.i("seongsoo", s);

            try {
                //JSONArray jsonArray = new JSONArray(s);
                //JSONObject json = jsonArray.getJSONObject(0);

                JSONObject json = new JSONObject(s);
                businessInfo.setId(json.getInt("id"));
                businessInfo.setBusinessid(json.getString("businessid"));
                businessInfo.setName(json.getString("name"));
                businessInfo.setContext(json.getString("context"));
                businessInfo.setLatitude(json.getDouble("latitude"));
                businessInfo.setLongitude(json.getDouble("longitude"));
                businessInfo.setBusiness_state(json.getInt("business_state"));

                if(businessInfo.getId() > 0)
                {
                    UpdateMap();
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    class businessUpdate extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(BizManageActivity.this);
        @Override
        protected String doInBackground(String... params) {
            StringBuilder output = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                JSONObject postDataParams = new JSONObject();

                // [ req.body.businessid, req.body.name, req.body.context, req.body.latitude,
                // req.body.longitude, req.body.business_state ],
                postDataParams.put("businessid", params[1]);
                postDataParams.put("name", params[2]);
                postDataParams.put("context", params[3]);
                postDataParams.put("latitude", params[4]);
                postDataParams.put("longitude", params[5]);
                postDataParams.put("business_state", params[6]);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("PUT");
                    conn.setDoInput(true); conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));
                    writer.flush();
                    writer.close();
                    os.close();
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
            dialog.setMessage("위치등록 중...");
            dialog.show();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            try {
                JSONObject json = new JSONObject(s);
                if (json.getBoolean("result") == true) {//로그인 성공
                    Toast.makeText(BizManageActivity.this,
                            "위치가 등록되었습니다.",
                            Toast.LENGTH_SHORT).show();
                            UpdateMap();
                } else {//로그인 실패
                    Toast.makeText(BizManageActivity.this,
                            "위치등록에 실패하였습니다",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}