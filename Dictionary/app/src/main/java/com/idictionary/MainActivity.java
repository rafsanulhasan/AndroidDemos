package com.idictionary;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.idictionary.adapters.MeaningListAdapter;
import com.idictionary.services.DictionaryService;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.idictionary.R.id;
import static com.idictionary.R.layout;

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

        this._btnSearch.setOnClickListener((View view) -> {

            String searchText=_txtSearch.getText().toString();
            _mainContent.setVisibility(View.INVISIBLE);
            _lblSearchEdit.setText(searchText);
            _txtSearchEdit.setText(searchText);
            _dictionaryContent.setVisibility(View.VISIBLE);
            try {
                _service = new DictionaryService(MainActivity.this);
                final List<String> meaningList = new ArrayList<>();
                MeaningListAdapter meaningListAdapter = new MeaningListAdapter(MainActivity.this, meaningList);
                _exList.setAdapter(meaningListAdapter);
                //meaningList.add("no data");
                JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
                        try {
                            final JSONArray definitions = response.getJSONArray("definitions");
                            LinearLayout layout = findViewById(id.mList);
                            for (int i = 0; i < definitions.length(); i++) {
                                JSONObject def = definitions.getJSONObject(i);
                                String d = def.getString("definition");
                                meaningList.add(d);

                                TextView tv = new TextView(MainActivity.this);
                                //tv.setPadding(0, 5,0,5);
                                //tv.setText(d);
                                layout.addView(tv);
                            }

                            //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                        Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                    }
                };
                _service.GetDefinition(searchText, handler);
            }
            catch (Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        _lblSearchEdit.setOnClickListener((View view)->{
            view.setVisibility(View.INVISIBLE);
            _txtSearchEdit.setVisibility(View.VISIBLE);
        });

        FloatingActionButton fab = findViewById(id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
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

    @Override
    public boolean onNavigateUp() {
        //return super.onNavigateUp();
        if (_dictionaryContent.getVisibility() == View.VISIBLE)
        {
            _dictionaryContent.setVisibility(View.INVISIBLE);
            _mainContent.setVisibility(View.VISIBLE);
        }
        return true;
    }
}

