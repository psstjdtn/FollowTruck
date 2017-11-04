package com.shinhan.sspark.followtruck;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


public class BizManageActivity extends AppCompatActivity {
        SupportMapFragment mapFragment;
        GoogleMap map;
        int flag = 0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_biz_manage);

            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(new OnMapReadyCallback(){
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    Log.d("TAG", "Googlemap is ready");

                    map = googleMap;
                }
            });


            try {
                MapsInitializer.initialize(this);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        // 클릭이벤트
        public void locateUpdate(View view)
        {

        }

        public void webMove(View view)
        {
            Intent intent = new Intent(BizManageActivity.this, BizWebViewActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_zero, R.anim.anim_slide_out_zero);
            finish();
        }

        public void menuinsert(View view)
        {
            Intent intent = new Intent(BizManageActivity.this, MenuInsertActivity.class);
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

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            flag = 0;
        }
    };
}
