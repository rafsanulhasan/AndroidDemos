package com.idictionary;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.idictionary.adapters.MeaningListAdapter;
import com.idictionary.services.DictionaryService;

import java.util.ArrayList;
import java.util.List;

import static com.idictionary.R.*;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private MainActivity _mainActivity;
    private View _mainLayout;
    private View _mainContent;
    private View _dictionaryContent;
    private EditText _txtSearch;
    private EditText _txtSearchEdit;
    private TextView _lblSearchEdit;
    private ListView _exList;
    private Button _btnSearch;
    private DictionaryService _service;

    public static final String TAG = "MainActivity";
    private static final int REQUEST_INTERNET = 200;
    private WebView htmlWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        _mainActivity = this;
        _mainLayout = findViewById(id.mainLayout);

        Toolbar toolbar = findViewById(id.toolbar);
        setSupportActionBar(toolbar);
        _mainContent = findViewById(id.contentMain);
        _dictionaryContent = findViewById(id.contentDictionary);
        _txtSearch = findViewById(id.txtSearch);
        _lblSearchEdit = findViewById(id.lblSearchEdit);
        _txtSearchEdit = findViewById(id.txtSearchEdit);
        _btnSearch = findViewById(id.btnSearch);
        _btnSearch.setClickable(true);
        _exList = findViewById(id.exList);

        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar){
            actionBar.hide();
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_INTERNET);
        }


        htmlWebView = (WebView)findViewById(R.id.webView);
        assert htmlWebView != null;
        WebSettings webSetting = htmlWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDisplayZoomControls(true);
        htmlWebView.setWebViewClient(new CustomWebViewClient());
        htmlWebView.loadUrl("https://inducesmile.com/blog");

        this._btnSearch.setOnClickListener((View view) -> {

            String searchText=_txtSearch.getText().toString();
            _mainContent.setVisibility(View.INVISIBLE);
            _lblSearchEdit.setText(searchText);
            _txtSearchEdit.setText(searchText);
            _dictionaryContent.setVisibility(View.VISIBLE);
            try {
                _service = new DictionaryService(MainActivity.this);
                List<String> meaningList = new ArrayList<String>();
                meaningList.add("no data");
                //meaningList=_service.GetDefinition(searchText);
                MeaningListAdapter meaningListAdapter = new MeaningListAdapter(this, meaningList);
                _exList.setAdapter(meaningListAdapter);
            }
            catch (Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        _lblSearchEdit.setOnClickListener((View view)->{
            view.setVisibility(View.INVISIBLE);
            _txtSearchEdit.setVisibility(View.VISIBLE);
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_INTERNET) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //start audio recording or whatever you planned to do
            }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.INTERNET)) {
                    //Show an explanation to the user *asynchronously*
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET);
                }else{
                    //Never ask again and handle your app without permission.
                }
            }
        }
    }
    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}

