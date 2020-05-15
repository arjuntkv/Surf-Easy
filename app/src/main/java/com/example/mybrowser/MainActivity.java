package com.example.mybrowser;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText inputURL;

    ImageButton facebook_btn;
    ImageButton insta_btn;
    ImageButton twitter_btn;
    ImageView voicebtn;

    String Voicetext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window g=getWindow();
        g.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,WindowManager.LayoutParams.TYPE_STATUS_BAR);
        setContentView(R.layout.activity_main);

        inputURL=findViewById(R.id.search_edttxt);

        facebook_btn=findViewById(R.id.facebook_btn);
        insta_btn=findViewById(R.id.insta_btn);
        twitter_btn=findViewById(R.id.twitter_btn);

        voicebtn=findViewById(R.id.voice_button);


        //TO load Url when enter key is pressed
        inputURL.setOnKeyListener(new View.OnKeyListener() {
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


//        searchButton.setOnClickListener(this);
        facebook_btn.setOnClickListener(this);
        insta_btn.setOnClickListener(this);
        twitter_btn.setOnClickListener(this);

        voicebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hi Speak Something");

                try{
                    startActivityForResult(intent,1);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    @Override
    public void onClick(View v) {

        if(v==facebook_btn){
            Intent facebook=new Intent(MainActivity.this,UrlSearch.class);
            facebook.putExtra("url_address","https://www.facebook.com");
            startActivity(facebook);
        }

        if(v==twitter_btn){
            Intent youtube=new Intent(MainActivity.this,UrlSearch.class);
            youtube.putExtra("url_address","https://www.twitter.com");
            startActivity(youtube);
        }
        if(v==insta_btn){
            Intent insta=new Intent(MainActivity.this,UrlSearch.class);
            insta.putExtra("url_address","https://www.instagram.com");
            startActivity(insta);
        }
    }

    private void openWebsite() {

        String urladdress=inputURL.getText().toString();

        if(TextUtils.isEmpty(urladdress)){
            Toast.makeText(MainActivity.this,"please enter Url",Toast.LENGTH_SHORT).show();
        }else{
            if(urladdress.contains(".")){
                String url_without_https=urladdress.replaceAll("https://www","");
                String https="https://";
                String www="www.";

                Intent search=new Intent(MainActivity.this,UrlSearch.class);
                search.putExtra("url_address",https+www+url_without_https);
                startActivity(search);
            }else{

                String google="https://www.google.com/search?q=";

                Intent search=new Intent(MainActivity.this,UrlSearch.class);
                search.putExtra("url_address",google+urladdress);
                startActivity(search);
            }


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1:
                if(resultCode==-1 && null!=data){
                    ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Voicetext=result.get(0).toLowerCase();

                    if(
                            Voicetext.equals("instagram") ||
                            Voicetext.equals("facebook") ||
                            Voicetext.equals("twitter") ||
                            Voicetext.equals("gaana") ||
                            Voicetext.equals("google") ||
                            Voicetext.equals("youtube")
                    ){
                        Intent search=new Intent(MainActivity.this,UrlSearch.class);
                        search.putExtra("url_address","https://www."+Voicetext+".com");
                        startActivity(search);
                    }else{
                        String google="https://www.google.com/search?q=";
                        Intent search=new Intent(MainActivity.this,UrlSearch.class);
                        search.putExtra("url_address",google+Voicetext);
                        startActivity(search);
                    }


                }
        }
    }
}
