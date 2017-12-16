package com.idictionary;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.idictionary.adapters.AntonymListAdapter;
import com.idictionary.adapters.ExampleListAdapter;
import com.idictionary.adapters.MeaningListAdapter;
import com.idictionary.adapters.SynonymListAdapter;
import com.idictionary.services.DictionaryService;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.ConnectionPendingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;
import cz.msebera.android.httpclient.conn.HttpHostConnectException;

import static com.idictionary.R.id;
import static com.idictionary.R.layout;

public class MainActivity
        extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener, View.OnKeyListener, TextToSpeech.OnInitListener, TabHost.OnTabChangeListener {

    public static final String TAG = "MainActivity";
    private static final int REQUEST_INTERNET = 200;
    private MainActivity _mainActivity;
    private View _mainLayout;
    private View _mainContent;
    private View _dictionaryContent;
    private EditText _txtSearch;
    private EditText _txtSearchEdit;
    private TextView _lblSearchEdit;
    private ListView _defListView;
    private ListView _synListView;
    private ListView _antListView;
    private ListView _exampleListView;
    private Button _btnSearch;
    private Button _btnSearchEdit;
    private Button _btnSpeak;
    private DictionaryService _service;
    private JsonHttpResponseHandler _defHandler;
    private JsonHttpResponseHandler _synHandler;
    private JsonHttpResponseHandler _antHandler;
    private JsonHttpResponseHandler _exampleHandler;
    private List<String> _defList;
    private MeaningListAdapter _defListAdapter;
    private List<String> _synList;
    private SynonymListAdapter _synListAdapter;
    private List<String> _antList;
    private AntonymListAdapter _antListAdapter;
    private List<String> _exampleList;
    private ExampleListAdapter _exampleListAdapter;
    private TextToSpeech _tts;
    private ProgressBar _dictLoadProgress;
    private TabHost _dicResultTab;

    private void configureData(List<String> list, ArrayAdapter<String> arrayAdapter, List<String> data) {
        list.clear();
        list.addAll(data);
        arrayAdapter.notifyDataSetChanged();
    }

    private void configureData(List<String> list, ArrayAdapter<String> arrayAdapter, Exception e) {
        list.clear();
        list.add(e.getMessage());
        arrayAdapter.notifyDataSetChanged();
    }

    private void configureData(List<String> list, ArrayAdapter<String> arrayAdapter, Throwable t) {
        list.clear();
        list.add(t.getMessage());
        arrayAdapter.notifyDataSetChanged();
    }

    private void configureData(List<String> list, ArrayAdapter<String> arrayAdapter, String s) {
        list.clear();
        list.add(s);
        arrayAdapter.notifyDataSetChanged();
    }

    private void initAsyncHttpResponseHandler(final List<String> values, final ArrayAdapter<String> adapter, final ListView listView) {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                OnSuccess(statusCode, headers, response, values, adapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (throwable != null &&
                        (throwable.getCause() instanceof ConnectTimeoutException ||
                                throwable.getCause() instanceof HttpHostConnectException ||
                                throwable.getCause() instanceof ConnectionPendingException)) {
                    String message = "onFailure (other): " + throwable.getMessage();
                    configureData(values, adapter, message);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                } else
                    try {
                        String message = response.getString("message");
                        configureData(values, adapter, message);
                        //Toast.makeText(MainActivity.this, "onFailure ok: " + message, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "onfailure catch: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                String message = "onFailure (jsonarray): " + throwable.getMessage();
                configureData(values, adapter, message);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                _dictLoadProgress.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                //super.onProgress(bytesWritten, totalSize);
                Integer bytesWrittenInt = Integer.parseInt(Long.toString(bytesWritten));
                Integer totalSizeInt = Integer.parseInt(Long.toString(totalSize));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    _dictLoadProgress.setMin(bytesWrittenInt);
                    _dictLoadProgress.setMax(totalSizeInt);
                }
                _dictLoadProgress.setProgress(bytesWrittenInt);

                //String.format("Progress %d from %d (%2.0f%%)", bytesWritten, totalSize, (totalSize > 0) ? (bytesWritten * 1.0 / totalSize) * 100 : -1)l
            }

            @Override
            public void onStart() {
                super.onStart();
                _dictLoadProgress.setVisibility(View.VISIBLE);
                listView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onUserException(Throwable error) {
                String message = "userEx (" + error.getCause() + "): " + error.getMessage();
                configureData(values, adapter, message);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };
        if (adapter instanceof MeaningListAdapter)
            _defHandler = handler;
        else if (adapter instanceof SynonymListAdapter)
            _synHandler = handler;
        else if (adapter instanceof AntonymListAdapter)
            _antHandler = handler;
        else if (adapter instanceof ExampleListAdapter)
            _exampleHandler = handler;
        listView.setAdapter(adapter);
    }

    private void OnSuccess(int statusCode, Header[] headers, JSONObject response, List<String> list, ArrayAdapter<String> adapter) {
        String whatToGet = adapter instanceof SynonymListAdapter ? "synonyms" : (adapter instanceof AntonymListAdapter ? "antonyms" : "examples");
        //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
        try {
            if (adapter instanceof MeaningListAdapter) {
                try {
                    final JSONArray definitions = response.getJSONArray("definitions");
                    for (int i = 0; i < definitions.length(); i++) {
                        JSONObject def;
                        def = definitions.getJSONObject(i);
                        String d = "";
                        if (def.has("partOfSpeech") && def.getString("partOfSpeech") != null)
                            d += "(" + def.getString("partOfSpeech") + ") ";
                        if (def.has("definition") && def.getString("definition") != null)
                            d += def.getString("definition");
                        list.add(d);
                    }
                    if (list.isEmpty())
                        list.add("No " + whatToGet + " found");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        list.sort(String::compareTo);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    String message = e.getMessage();
                    configureData(_defList, _defListAdapter, message);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                }
            } else {
                try {
                    final JSONArray others = response.getJSONArray(whatToGet);
                    for (int i = 0; i < others.length(); i++) {
                        //Toast.makeText(MainActivity.this, others.getString(i), Toast.LENGTH_LONG).show();
                        list.add(others.getString(i));
                    }
                    if (list.isEmpty())
                        list.add("No " + whatToGet + " found");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        list.sort(String::compareTo);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            //configureData(list, adapter instanceof MeaningListAdapter ? ((MeaningListAdapter)adapter) : (adapter instanceof SynonymListAdapter ? ((SynonymListAdapter)adapter): (adapter instanceof AntonymListAdapter ? ((AntonymListAdapter)adapter) :((ExampleListAdapter)adapter))), e);
            configureData(list, adapter, e);
            Toast.makeText(MainActivity.this, "success: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void speakOut(String text) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            _tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        else
            _tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
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
    public void onClick(View view) {
        String searchText;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (view.getId()) {
            case id.btnSearch:
                _mainContent.setVisibility(View.INVISIBLE);
                searchText = _txtSearch.getText().toString();
                _lblSearchEdit.setText(searchText);
                _txtSearchEdit.setText(searchText);
                _dictionaryContent.setVisibility(View.VISIBLE);
                try {
                    String currentTabTag = _dicResultTab.getCurrentTabTag();
                    if (currentTabTag != null) {
                        switch (currentTabTag) {
                            case "Antonym":
                                _service = DictionaryService.getInstance(this, searchText);
                                _antList.clear();
                                _service.GetAntonym(_antHandler);
                                break;
                            case "Example":
                                _service = DictionaryService.getInstance(this, searchText);
                                _exampleList.clear();
                                _service.GetExample(_exampleHandler);
                                break;
                            case "Meaning":
                                _service = DictionaryService.getInstance(this, searchText);
                                _defList.clear();
                                _service.GetDefinition(_defHandler);
                                break;
                            case "Synonym":
                                _service = DictionaryService.getInstance(this, searchText);
                                _synList.clear();
                                _service.GetSynonym(_synHandler);
                                break;
                        }
                    }
                    _dicResultTab.setCurrentTab(_dicResultTab.getCurrentTab());
                    if (imm != null)
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (HttpHostConnectException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case id.btnSearchEdit:
                searchText = _txtSearchEdit.getText().toString();

                _txtSearchEdit.setVisibility(View.INVISIBLE);
                view.setVisibility(View.INVISIBLE);

                _btnSpeak.setVisibility(View.VISIBLE);
                _lblSearchEdit.setVisibility(View.VISIBLE);

                _lblSearchEdit.setText(searchText);
                _txtSearch.setText(searchText);
                try {
                    String currentTabTag = _dicResultTab.getCurrentTabTag();
                    if (currentTabTag != null) {
                        switch (currentTabTag) {
                            case "Meaning":
                                _service = DictionaryService.getInstance(this, searchText);
                                _defList.clear();
                                _service.GetDefinition(_defHandler);
                                break;
                            case "Synonym":
                                _service = DictionaryService.getInstance(this, searchText);
                                _synList.clear();
                                _service.GetSynonym(_synHandler);
                                break;
                            case "Antonym":
                                _service = DictionaryService.getInstance(this, searchText);
                                _antList.clear();
                                _service.GetAntonym(_antHandler);
                                break;
                            case "Example":
                                _service = DictionaryService.getInstance(this, searchText);
                                _exampleList.clear();
                                _service.GetExample(_exampleHandler);
                                break;
                        }
                    }
                    _dicResultTab.setCurrentTab(_dicResultTab.getCurrentTab());
                    if (imm != null)
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } catch (HttpHostConnectException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                break;
            case id.btnSpeak:
                this.speakOut(_lblSearchEdit.getText().toString());
                break;
            case id.lblSearchEdit:
                view.setVisibility(View.INVISIBLE);
                _txtSearchEdit.setVisibility(View.VISIBLE);
                _txtSearchEdit.onHoverChanged(true);
                _btnSearchEdit.setVisibility(View.VISIBLE);
                _btnSpeak.setVisibility(View.INVISIBLE);
                break;
        }
    }

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
        _btnSpeak = findViewById(id.btnSpeak);
        _btnSearch.setClickable(true);
        _btnSearchEdit.setClickable(true);
        _defListView = findViewById(id.defList);
        _synListView = findViewById(id.synList);
        _antListView = findViewById(id.antList);
        _exampleListView = findViewById(id.exampleList);
        _dictLoadProgress = findViewById(id.dictLoadProgress);
        _dictLoadProgress.setProgress(0);
        _dicResultTab = findViewById(id.dicResultTab);

        _tts = new TextToSpeech(this, this);

        _btnSearch.setOnClickListener(this);
        _btnSearchEdit.setOnClickListener(this);
        _btnSpeak.setOnClickListener(this);

        _lblSearchEdit.setOnClickListener(this);
        _txtSearch.setOnKeyListener(this);
        _txtSearchEdit.setOnKeyListener(this);

        _dicResultTab.setup();

        TabHost.TabSpec definitionsTab = _dicResultTab.newTabSpec("Meaning");
        definitionsTab.setContent(id.dicDefTab);
        definitionsTab.setIndicator("Meaning");
        _dicResultTab.addTab(definitionsTab);

        TabHost.TabSpec synTab = _dicResultTab.newTabSpec("Synonym");
        synTab.setContent(id.dicSynTab);
        synTab.setIndicator("Synonym");
        _dicResultTab.addTab(synTab);

        TabHost.TabSpec antTab = _dicResultTab.newTabSpec("Antonym");
        antTab.setContent(id.dicAntTab);
        antTab.setIndicator("Antonym");
        _dicResultTab.addTab(antTab);

        TabHost.TabSpec exampleTab = _dicResultTab.newTabSpec("Example");
        exampleTab.setContent(id.dicExTab);
        exampleTab.setIndicator("Example");
        _dicResultTab.addTab(exampleTab);

        FloatingActionButton fab = findViewById(id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        _defList = new ArrayList<>();
        _defListAdapter = new MeaningListAdapter(this, _defList);
        initAsyncHttpResponseHandler(_defList, _defListAdapter, _defListView);

        _synList = new ArrayList<>();
        _synListAdapter = new SynonymListAdapter(this, _synList);
        initAsyncHttpResponseHandler(_synList, _synListAdapter, _synListView);

        _antList = new ArrayList<>();
        _antListAdapter = new AntonymListAdapter(this, _antList);
        initAsyncHttpResponseHandler(_antList, _antListAdapter, _antListView);

        _exampleList = new ArrayList<>();
        _exampleListAdapter = new ExampleListAdapter(this, _exampleList);
        initAsyncHttpResponseHandler(_exampleList, _exampleListAdapter, _exampleListView);

        _dicResultTab.setOnTabChangedListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = _tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "This Language is not supported", Toast.LENGTH_LONG).show();
            } else {
                _btnSpeak.setClickable(true);
                _btnSpeak.setEnabled(true);
            }

        } else {
            Toast.makeText(this, "Initilization Failed!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        switch (view.getId()) {
            case id.txtSearch:
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    onClick(_btnSearch);
                    return true;
                }
                break;
            case id.txtSearchEdit:
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    onClick(_btnSearchEdit);
                    return true;
                }
                break;
        }

        return false;
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

    @Override
    public void onTabChanged(String s) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        String searchText = _lblSearchEdit.getText().toString();
        switch (s) {
            case "Antonym":
                try {
                    _service = DictionaryService.getInstance(this, searchText);
                    _antList.clear();
                    _service.GetAntonym(_antHandler);
                } catch (HttpHostConnectException e) {
                    String message = e.getMessage();
                    configureData(_antList, _antListAdapter, message);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    String message = e.getMessage();
                    configureData(_antList, _antListAdapter, message);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                }
                break;
            case "Example":
                try {
                    _service = DictionaryService.getInstance(this, searchText);
                    _exampleList.clear();
                    _service.GetExample(_exampleHandler);
                } catch (HttpHostConnectException e) {
                    String message = e.getMessage();
                    configureData(_synList, _synListAdapter, message);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    String message = e.getMessage();
                    configureData(_exampleList, _exampleListAdapter, message);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                }
                break;
            case "Synonym":
                try {
                    _service = DictionaryService.getInstance(this, searchText);
                    _synList.clear();
                    _service.GetSynonym(_synHandler);
                } catch (HttpHostConnectException e) {
                    String message = e.getMessage();
                    configureData(_synList, _synListAdapter, message);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    String message = e.getMessage();
                    configureData(_synList, _synListAdapter, message);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                }
                break;
            default:
                try {
                    _service = DictionaryService.getInstance(MainActivity.this, searchText);
                    _defList.clear();
                    _service.GetDefinition(_defHandler);
                } catch (JSONException e) {
                    String message = e.getMessage();
                    configureData(_defList, _defListAdapter, message);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                } catch (HttpHostConnectException e) {
                    String message = e.getMessage();
                    configureData(_defList, _defListAdapter, message);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}

