package com.shinhan.sspark.followtruck;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

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


public class ComSignUpActivity extends AppCompatActivity {

    EditText name, uid, upw, upwhw, hpno, snsid;
    RadioButton custom_radio, biz_radio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com_sign_up);

        Button backbutton = (Button)findViewById(R.id.backbutton);

        // 뒤로가기 버튼이벤트 활성화
        backbutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(ComSignUpActivity.this, SubActivity.class);
                // 페이지 넘어가기
                startActivity(intent);
                // 끝내기
                finish();
            }
        });
    }

    public void signup(View view) {
        //  id초기화
        name       = (EditText)findViewById(R.id.name);
        uid        = (EditText)findViewById(R.id.uid);
        upw        = (EditText)findViewById(R.id.upw);
        upwhw      = (EditText)findViewById(R.id.upwhw);
        hpno       = (EditText)findViewById(R.id.hpno);
        snsid      = (EditText)findViewById(R.id.snsid);

        custom_radio = (RadioButton)findViewById(R.id.custom_radio);
        biz_radio    = (RadioButton)findViewById(R.id.biz_radio);


        // req.body.userid, req.body.name, req.body.password, req.body.hpno, req.body.snsid, req.body.user_code
        new SignUp().execute(
                "http://172.16.2.3:52273/users",
                uid.getText().toString(),
                name.getText().toString(),
                upw.getText().toString(),
                hpno.getText().toString(),
                snsid.getText().toString(),
                "1"
                );
    }
    class SignUp extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(ComSignUpActivity.this);
        @Override
        protected String doInBackground(String... params) {
            StringBuilder output = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                JSONObject postDataParams = new JSONObject();

                // req.body.userid, req.body.name, req.body.password, req.body.hpno, req.body.snsid, req.body.user_code
                postDataParams.put("userid", params[1]);
                postDataParams.put("name", params[2]);
                postDataParams.put("password", params[3]);
                postDataParams.put("hpno", params[4]);
                postDataParams.put("snsid", params[5]);
                postDataParams.put("user_code", params[6]);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("POST");
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
            dialog.setMessage("회원등록 중...");
            dialog.show();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            try {
                JSONObject json = new JSONObject(s);
                if (json.getBoolean("result") == true) { //로그인 실패
                    Toast.makeText(ComSignUpActivity.this,
                            "회원가입에 성공하였습니다.",
                            Toast.LENGTH_SHORT).show();
                    // 끝내기
                    finish();
                } else {//로그인 성공
                    Toast.makeText(ComSignUpActivity.this,
                            "회원가입에 실패하였습니다. 입력한 내용을 확인해주세요.",
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
