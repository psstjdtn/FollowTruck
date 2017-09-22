package com.shinhan.sspark.followtruck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class BizButtonActivity extends AppCompatActivity {

    Button bizdrk, bizmng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biz_button);

        //  id초기화
        bizdrk    = (Button)findViewById(R.id.bizdrk);
        bizmng    = (Button)findViewById(R.id.bizmng);

        // 로그인 버튼이벤트 활성화
        bizdrk.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(BizButtonActivity.this, ComSignUpActivity.class); // 영업점회원가입(등록)화면으로 이동
                intent.putExtra("USER_KEY","BIZ");
                // 페이지 넘어가기
                startActivity(intent);
            }
        });

        // 로그인 버튼이벤트 활성화
        bizmng.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(BizButtonActivity.this, BizManageActivity.class); // 영업점관리화면으로 이동
                // 페이지 넘어가기
                startActivity(intent);
            }
        });

    }
}
