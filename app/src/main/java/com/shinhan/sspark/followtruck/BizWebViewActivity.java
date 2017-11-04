package com.shinhan.sspark.followtruck;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import static android.webkit.WebSettings.LOAD_NO_CACHE;

public class BizWebViewActivity extends AppCompatActivity {
    private static final String HOME_URL = "http://172.16.2.3:9000/#!/";
    private static final String MENU_URL = "http://172.16.2.3:9000/#!/menu";
    private static final String ORDER_URL = "http://172.16.2.3:9000/#!/order/biz/list";
    private static final String BIZ_URL = "http://172.16.2.3:9000/#!/users/biz";

    int flag = 0;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biz_web_view);

        webView = (WebView)findViewById(R.id.webView);
        // 웹뷰 설정 부분
        // 프레임웍을 사용할 경우 내부적으로 세팅이 된다.
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){


            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(LOAD_NO_CACHE);
        webView.getSettings().setDatabaseEnabled(true);
        webView.loadUrl(HOME_URL+"?os=android&version=1.0&device=emul");
    }

    //클릭이벤트
    public void locateManage(View view)
    {
        Intent intent = new Intent(BizWebViewActivity.this, BizManageActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_zero, R.anim.anim_slide_out_zero);
        finish();
    }

    public void menulist(View view)
    {
        webView.loadUrl(MENU_URL +"?os=android&version=1.0&device=emul");
    }

    public void menuinsert(View view)
    {
        Intent intent = new Intent(BizWebViewActivity.this, MenuInsertActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    public void orderHistory(View view)
    {
        webView.loadUrl(ORDER_URL+"?os=android&version=1.0&device=emul");
    }

    public void myInfo(View view)
    {
        webView.loadUrl(BIZ_URL+"?os=android&version=1.0&device=emul");
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
