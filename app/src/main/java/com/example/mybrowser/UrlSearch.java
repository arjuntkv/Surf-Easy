package com.example.mybrowser;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class UrlSearch extends AppCompatActivity{

    EditText urlinput;
    ImageView homebtn;
    WebView SearchWebAddress;
    String url;
    SwipeRefreshLayout swipe;
    String murl;


    ImageView back;
    ImageView forward;

    ImageView newtabbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window g=getWindow();
        g.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,WindowManager.LayoutParams.TYPE_STATUS_BAR);
        setContentView(R.layout.activity_url_search);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

         urlinput=findViewById(R.id.search_edttxt2);
        homebtn=findViewById(R.id.home_button);
        SearchWebAddress=findViewById(R.id.search_website);
        swipe=findViewById(R.id.swipe);

        back=findViewById(R.id.back_button);
        forward=findViewById(R.id.forward_button);

        newtabbtn=findViewById(R.id.newtab_button);

        //swipe icon colors scheme
        swipe.setColorSchemeColors(Color.BLUE,Color.YELLOW,Color.GREEN);

        url=getIntent().getExtras().get("url_address").toString();

        WebSettings WebSettings =SearchWebAddress.getSettings();

        WebSettings.setJavaScriptEnabled(true);

        //to display url in edittext bar
        SearchWebAddress.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipe.setRefreshing(false);

                murl =view.getUrl();
                if(murl.contains("https://www."))
                {
                    String url_without_https=murl.replaceAll("https://www.","");
                    urlinput.setText(url_without_https);
                }
                else{
                    String url_without_https=murl.replaceAll("https://","");
                    urlinput.setText(url_without_https);
                }

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });


        //to select the entire text in editext when the editext is clicked
        urlinput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urlinput.setSelectAllOnFocus(true);
                urlinput.selectAll();
            }
        });

        //to open a new Activity with google.com as loadurl
        newtabbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebook=new Intent(UrlSearch.this,UrlSearch.class);
                facebook.putExtra("url_address","https://www.google.com");
                startActivity(facebook);
            }
        });



        SearchWebAddress.setWebChromeClient(new MyChrome());

        SearchWebAddress.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        SearchWebAddress.getSettings().setAppCacheEnabled(true);
        SearchWebAddress.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings.setDomStorageEnabled(true);
        WebSettings.setUseWideViewPort(true);
        WebSettings.setSavePassword(true);
        WebSettings.setSaveFormData(true);
        WebSettings.setEnableSmoothTransition(true);

        //to save instance when something changes(orientation)
        if(savedInstanceState==null){
            SearchWebAddress.post(new Runnable() {
                @Override
                public void run() {
                    loadWebsite();
                }
            });
        }


        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               SearchWebAddress.reload();
            }
        });

        //enter key is pressed to load url
        urlinput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    openWebsite();
                    return true;
                }
                return false;
            }
        });

        //Home button to forward to home page
        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SearchWebAddress.canGoBack())
                SearchWebAddress.goBack();
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SearchWebAddress.canGoForward())
                SearchWebAddress.goForward();
            }
        });

        //for downloading files
        SearchWebAddress.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DownloadManager.Request myRequest=new DownloadManager.Request(Uri.parse(url));
                myRequest.allowScanningByMediaScanner();
                myRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                DownloadManager mymanager=(DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                mymanager.enqueue(myRequest);

                Toast.makeText(UrlSearch.this,"File is Downloading...",Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        SearchWebAddress.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        SearchWebAddress.restoreState(savedInstanceState);
    }

    //used to load website from Home page
    private void loadWebsite() {
        ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            SearchWebAddress.loadUrl(url);
            //urlinput.setText(url);
        } else {
            SearchWebAddress.setVisibility(View.GONE);
        }
    }


    //this is the main method to open website
    private void openWebsite() {

        String urladdress=urlinput.getText().toString();
        swipe.setRefreshing(true);


        if(TextUtils.isEmpty(urladdress)){
            Toast.makeText(UrlSearch.this,"please enter Url",Toast.LENGTH_SHORT).show();
        }else{
            if(urladdress.contains("https://")){
                SearchWebAddress.loadUrl(urladdress);
            }
            else if(urladdress.contains("www")){
                String urladd="https://"+urladdress;
                SearchWebAddress.loadUrl(urladd);
            }
            else if(urladdress.contains(".")){
                String urladd="https://www."+urladdress;
                SearchWebAddress.loadUrl(urladd);
            }
            else{
                String google="https://www.google.com/search?q=";
                SearchWebAddress.loadUrl(google+urladdress);
            }

        }


    }



    //on the event of pressing bak button
    @Override
    public void onBackPressed(){
        urlinput.setSelectAllOnFocus(true);
        swipe.setRefreshing(false);
        if(SearchWebAddress.canGoBack()){
            swipe.setRefreshing(false);
            SearchWebAddress.goBack();
        }else{
            swipe.setRefreshing(false);
            super.onBackPressed();
        }
    }



    //to handle page instance on changing orientation
    public void onPageFinished(WebView view,String url){
        swipe.setRefreshing(false);
    }

    private class MyChrome extends WebChromeClient{
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846);
        }
    }
}
