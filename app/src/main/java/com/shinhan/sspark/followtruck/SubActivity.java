package com.shinhan.sspark.followtruck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SubActivity extends AppCompatActivity {

    EditText uid, upw;
    Button login, signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        //  id초기화
        uid    = (EditText)findViewById(R.id.uid);
        upw    = (EditText)findViewById(R.id.upw);
        login  = (Button)findViewById(R.id.login);
        signup = (Button)findViewById(R.id.signup);

        // 로그인 버튼이벤트 활성화
        login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(SubActivity.this, ComSelActivity.class);
                // 페이지 넘어가기
                startActivity(intent);
                // 앱종료
                finish();
            }
        });

        // 회원가입 버튼이벤트 활성화
        signup.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(SubActivity.this, ComSelActivity.class);
                // 페이지 넘어가기
                startActivity(intent);
                // 앱종료
                finish();
            }
        });

    }
}
