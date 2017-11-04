package com.shinhan.sspark.followtruck;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class SubActivity extends AppCompatActivity {

    EditText uid, upw;
    Button signup;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        //  id초기화
        uid    = (EditText)findViewById(R.id.uid);
        upw    = (EditText)findViewById(R.id.upw);
        signup = (Button)findViewById(R.id.signup);

        // 회원가입 버튼이벤트 활성화
        signup.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(SubActivity.this, ComSignUpActivity.class);
                // 페이지 넘어가기
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }
        });

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

    public void login(View view) {
        //  id초기화
        uid        = (EditText)findViewById(R.id.uid);
        upw        = (EditText)findViewById(R.id.upw);
        // req.body.userid, req.body.name, req.body.password, req.body.hpno, req.body.snsid, req.body.user_code
        new login().execute(
                "http://172.16.2.3:52273/users/login",
                uid.getText().toString(),
                upw.getText().toString()
        );
    }

    class login extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(SubActivity.this);
        @Override
        protected String doInBackground(String... params) {
            StringBuilder output = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                JSONObject postDataParams = new JSONObject();

                // req.body.userid, req.body.password
                postDataParams.put("userid", params[1]);
                postDataParams.put("password", params[2]);

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
            dialog.setMessage("로그인 중...");
            dialog.show();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            try {
                JSONObject json = new JSONObject(s);
                if (json.getBoolean("result") == true) {//로그인 성공
                    // 고객체크
                    ComCheck();


                } else {//로그인 실패
                    Toast.makeText(SubActivity.this,
                            "아이디가 없거나 암호가 틀렸습니다.",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void ComCheck() {
        new ComCheck().execute(
                "http://172.16.2.3:52273/users/" + uid.getText().toString()
        );
    }

    class ComCheck extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(SubActivity.this);
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Log.i("result", s);
            try {
                //JSONArray jsonArray = new JSONArray(s);
                //JSONObject json = jsonArray.getJSONObject(0);

                JSONObject json = new JSONObject(s);
                int user_code = (Integer) json.get("user_code");

                if (user_code == 1) {//고객화면
                    Intent intent = new Intent(SubActivity.this, CusMainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                    finish();
                }
                else if (user_code == 2) {//영업장화면
                    Intent intent = new Intent(SubActivity.this, BizManageActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                    finish();
                }
                else
                {
                    Toast.makeText(SubActivity.this,
                            "유효하지않은 회원정보입니다. 재가입 후 진행하세요.",
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
