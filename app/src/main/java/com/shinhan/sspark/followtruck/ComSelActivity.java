package com.shinhan.sspark.followtruck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ComSelActivity extends AppCompatActivity {

    Button bizsel, cussel;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com_sel);

        bizsel  = (Button)findViewById(R.id.bizsel);
        cussel  = (Button)findViewById(R.id.cussel);

        Intent intent = getIntent(); // 이 액티비티를 부른 인텐트를 받는다.
        username      = intent.getStringExtra("TRX_G"); // 로그인인지 회원가입인지 넘겨온다

        // 영업장입니다 버튼이벤트 활성화
        bizsel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = null;
                if(username.equals("LOGIN")){
                    intent = new Intent(ComSelActivity.this, BizManageActivity.class); // 영업점초기화면으로 이동
                }
                else
                if(username.equals("SIGNUP")){
                    intent = new Intent(ComSelActivity.this, ComSignUpActivity.class); // 영업점회원가입화면으로 이동
                    intent.putExtra("USER_KEY","BIZ");
                }
                else
                {
                    finish();
                    return ;
                }
                // 페이지 넘어가기
                startActivity(intent);
                finish();
            }
        });

        // 고객입니다 버튼이벤트 활성화
        cussel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = null;
                if(username.equals("LOGIN")){
                    intent = new Intent(ComSelActivity.this, CusMainActivity.class); // 고객초기화면으로 이동
                }
                else
                if(username.equals("SIGNUP")){
                    intent = new Intent(ComSelActivity.this, ComSignUpActivity.class); // 고객회원가입화면으로 이동
                    intent.putExtra("USER_KEY","CUS");
                }
                else
                {
                    finish();
                    return ;
                }

                // 페이지 넘어가기
                startActivity(intent);
                finish();
            }
        });
    }
}
