package com.shinhan.sspark.followtruck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ComSignUpActivity extends AppCompatActivity {

    EditText uid, upw, upwhw, hpno, snsid;
    Button drkbutton, backbutton;
    String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com_sign_up);

        //  id초기화
        uid        = (EditText)findViewById(R.id.uid);
        upw        = (EditText)findViewById(R.id.upw);
        upwhw      = (EditText)findViewById(R.id.upwhw);
        hpno       = (EditText)findViewById(R.id.hpno);
        snsid      = (EditText)findViewById(R.id.snsid);

        drkbutton  = (Button)findViewById(R.id.drkbutton);
        backbutton = (Button)findViewById(R.id.backbutton);

        Intent intent = getIntent(); // 이 액티비티를 부른 인텐트를 받는다.
        userKey       = intent.getStringExtra("USER_KEY"); // 로그인인지 회원가입인지 넘겨온다

        // 회원가입을 어디서 호출된 것에 따른 버튼 색깔 변화
        if (userKey.equals("BIZ")){
            drkbutton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.biz_btn_bg));
            backbutton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.biz_btn_bg));
        }
        else
        if (userKey.equals("CUS")) {
            drkbutton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.cus_btn_bg));
            backbutton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.cus_btn_bg));
        }

        // 등록 버튼이벤트 활성화
        drkbutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(ComSignUpActivity.this, SubActivity.class);
                // 페이지 넘어가기
                startActivity(intent);
                // 끝내기
                finish();
            }
        });

        // 뒤로가기 버튼이벤트 활성화
        backbutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // 뒤로가기
                finish();
            }
        });
    }
}
