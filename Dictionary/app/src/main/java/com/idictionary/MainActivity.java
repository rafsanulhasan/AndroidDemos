package com.idictionary;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.idictionary.adapters.MeaningListAdapter;
import com.idictionary.services.DictionaryService;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.ConnectionPendingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;
import cz.msebera.android.httpclient.conn.HttpHostConnectException;

import static com.idictionary.R.id;
import static com.idictionary.R.layout;

public class MainActivity
        extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener, View.OnKeyListener {

    public static final String TAG = "MainActivity";
    private static final int REQUEST_INTERNET = 200;
    private MainActivity _mainActivity;
    private View _mainLayout;
    private View _mainContent;
    private View _dictionaryContent;
    private EditText _txtSearch;
    private EditText _txtSearchEdit;
    private TextView _lblSearchEdit;
    private ListView _exList;
    private Button _btnSearch;
    private Button _btnSearchEdit;
    private DictionaryService _service;
    private JsonHttpResponseHandler _handler;
    private List<String> _meaningList;
    private MeaningListAdapter _meaningListAdapter;

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
        _btnSearchEdit = findViewById(id.btnSearchEdit);
        _btnSearch.setClickable(true);
        _btnSearchEdit.setClickable(true);
        _exList = findViewById(id.exList);

        _btnSearch.setOnClickListener(this);
        _btnSearchEdit.setOnClickListener(this);

        _lblSearchEdit.setOnClickListener(this);
        _txtSearch.setOnKeyListener(this);
        _txtSearchEdit.setOnKeyListener(this);

        FloatingActionButton fab = findViewById(id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        _meaningList = new ArrayList<>();
        _meaningListAdapter = new MeaningListAdapter(this, _meaningList);
        _handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                OnSuccess(statusCode, headers, response, _meaningList, _meaningListAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (throwable != null &&
                        (throwable.getCause() instanceof ConnectTimeoutException ||
                                throwable.getCause() instanceof HttpHostConnectException ||
                                throwable.getCause() instanceof ConnectionPendingException)) {
                    String message = "onFailure (other): " + throwable.getMessage();
                    configureData(message);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                } else
                    try {
                        String message = response.getString("message");
                        configureData(message);
                        //Toast.makeText(MainActivity.this, "onFailure ok: " + message, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "onfailure catch: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                String message = "onFailure (jsonarray): " + throwable.getMessage();
                configureData(message);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onUserException(Throwable error) {
                String message = "userEx (" + error.getCause() + "): " + error.getMessage();
                configureData(message);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };
        _exList.setAdapter(_meaningListAdapter);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_INTERNET) {
            //if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //start audio recording or whatever you planned to do
            //}
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.INTERNET)) {
                    //Show an explanation to the user *asynchronously*
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET);
                }
                //else{
                //Never ask again and handle your app without permission.
                //}
            }
        }
    }

    private void OnSuccess(int statusCode, Header[] headers, JSONObject response, List<String> meaningList, MeaningListAdapter meaningListAdapter) {
        try {
            final JSONArray definitions = response.getJSONArray("definitions");
            for (int i = 0; i < definitions.length(); i++) {
                JSONObject def = definitions.getJSONObject(i);
                String d = "";
                if (def.getString("partOfSpeech") != null)
                    d += "(" + def.getString("partOfSpeech") + ") ";
                if (def.getString("definition") != null)
                    d += def.getString("definition");
                meaningList.add(d);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                meaningList.sort(String::compareTo);
            }
            meaningListAdapter.notifyDataSetChanged();
            //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            configureData(e);
            Toast.makeText(MainActivity.this, "success: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        if (_mainContent.getVisibility() == View.VISIBLE) {
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        if (_dictionaryContent.getVisibility() == View.VISIBLE) {
            _dictionaryContent.setVisibility(View.GONE);
            _mainContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)
                && (event.getRepeatCount() == 0)) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        String searchText;
        switch (view.getId()) {
            case id.btnSearch:
                _mainContent.setVisibility(View.INVISIBLE);
                searchText = _txtSearch.getText().toString();
                _lblSearchEdit.setText(searchText);
                _txtSearchEdit.setText(searchText);
                _dictionaryContent.setVisibility(View.VISIBLE);
                try {
                    _service = new DictionaryService(this);
                    _service.GetDefinition(searchText, _handler);
                } catch (HttpHostConnectException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case id.btnSearchEdit:
                _txtSearchEdit.setVisibility(View.INVISIBLE);
                view.setVisibility(View.INVISIBLE);
                _lblSearchEdit.setVisibility(View.VISIBLE);
                searchText = _txtSearchEdit.getText().toString();
                _txtSearch.setText(searchText);
                _lblSearchEdit.setText(searchText);
                try {
                    _service = new DictionaryService(this);
                    _service.GetDefinition(searchText, _handler);
                } catch (HttpHostConnectException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                break;
            case id.lblSearchEdit:
                view.setVisibility(View.INVISIBLE);
                _txtSearchEdit.setVisibility(View.VISIBLE);
                _btnSearchEdit.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void configureData(List<String> data) {
        _meaningList = data;
        _meaningListAdapter.notifyDataSetChanged();
    }

    private void configureData(Exception e) {
        _meaningList.clear();
        _meaningList.add(e.getMessage());
        _meaningListAdapter.notifyDataSetChanged();
    }

    private void configureData(Throwable t) {
        _meaningList.clear();
        _meaningList.add(t.getMessage());
        _meaningListAdapter.notifyDataSetChanged();
    }

    private void configureData(String s) {
        _meaningList.clear();
        _meaningList.add(s);
        _meaningListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (view.getId()) {
            case id.txtSearch:
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    onClick(_btnSearch);

                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    return true;
                }
                break;
            case id.txtSearchEdit:
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    onClick(_btnSearchEdit);

                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    return true;
                }
                break;
        }

        return false;
    }
}

