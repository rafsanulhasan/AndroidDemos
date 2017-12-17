package com.idictionary.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;

import com.idictionary.utils.JsonCacher;
import com.idictionary.utils.MyJsonHttpResponseHandler;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.conn.HttpHostConnectException;

public class DictionaryService {
    private static DictionaryService _svc;
    private final String _baseUrl = "https://wordsapiv1.p.mashape.com";
    private final Context _context;
    JsonHttpResponseHandler _currentResponseHandler;
    String _word;
    PersistentCookieStore _cookieStrore;
    JsonCacher _jsonCacher;
    String _defJson;
    String _synJson;
    String _antJson;
    String _exampleJson;
    String _fileName;
    private StringBuilder _sb;
    private AsyncHttpClient _httpClient;
    private ConnectivityManager _connectivity;

    private DictionaryService(Context context, String word) {
        _word = word;
        _context = context;
        _connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        _sb = new StringBuilder(_baseUrl);
        _httpClient = new AsyncHttpClient();
        _httpClient.setConnectTimeout(500);
        _httpClient.setResponseTimeout(100);
        _httpClient.setTimeout(100);
        _httpClient.setMaxRetriesAndTimeout(5, 100);
        _cookieStrore = new PersistentCookieStore(_context);
        _httpClient.setCookieStore(_cookieStrore);
        _jsonCacher = new JsonCacher(_context);
        this.setRequestHeaders();
    }

    public static DictionaryService getInstance(Context context, String word) {
        return _svc == null ? new DictionaryService(context, word) : _svc;
    }

    public void GetDefinition(final JsonHttpResponseHandler jsonResponseHandler) throws HttpHostConnectException, JSONException {
        _fileName = "def." + _word + ".json";
        _defJson = _jsonCacher.readJsonFileData(_fileName);
        if (_defJson != null) {

            jsonResponseHandler.onSuccess(200, null, new JSONObject(_defJson));
            jsonResponseHandler.onFinish();
        } else {
            if (!this.isNetworkOnline())
                throw new HttpHostConnectException(new IOException("No internet connection"), null);
            _currentResponseHandler = new MyJsonHttpResponseHandler(_context, jsonResponseHandler, _fileName);
            String url = this.populateUrl(_word, "definitions");
            _httpClient.get(url, null, _currentResponseHandler);
        }
    }

    public void GetSynonym(JsonHttpResponseHandler jsonResponseHandler) throws HttpHostConnectException, JSONException {
        _fileName = "syn." + _word + ".json";
        _synJson = _jsonCacher.readJsonFileData(_fileName);
        if (_synJson != null) {
            jsonResponseHandler.onSuccess(200, null, new JSONObject(_synJson));
            jsonResponseHandler.onFinish();
        } else {
            if (!isNetworkOnline())
                throw new HttpHostConnectException(new IOException("No Internet Connection"), null);
            _currentResponseHandler = new MyJsonHttpResponseHandler(_context, jsonResponseHandler, _fileName);
            String url = this.populateUrl(_word, "synonyms");
            _httpClient.get(url, null, _currentResponseHandler);
        }
    }

    public void GetAntonym(JsonHttpResponseHandler jsonResponseHandler) throws HttpHostConnectException, JSONException {
        _fileName = "ant." + _word + ".json";
        _antJson = _jsonCacher.readJsonFileData(_fileName);
        if (_antJson != null) {
            jsonResponseHandler.onSuccess(200, null, new JSONObject(_antJson));
            jsonResponseHandler.onFinish();
        } else {
            if (!isNetworkOnline())
                throw new HttpHostConnectException(new IOException("No Internet Connection"), null);
            _currentResponseHandler = new MyJsonHttpResponseHandler(_context, jsonResponseHandler, _fileName);
            String url = this.populateUrl(_word, "antonyms");
            _httpClient.get(url, null, _currentResponseHandler);
        }
    }

    public void GetExample(JsonHttpResponseHandler jsonResponseHandler) throws HttpHostConnectException, JSONException {
        _fileName = "ex." + _word + ".json";
        _exampleJson = _jsonCacher.readJsonFileData(_fileName);
        if (_exampleJson != null) {
            jsonResponseHandler.onSuccess(200, null, new JSONObject(_exampleJson));
            jsonResponseHandler.onFinish();
        } else {
            if (!isNetworkOnline())
                throw new HttpHostConnectException(new IOException("No Internet Connection"), null);
            _currentResponseHandler = new MyJsonHttpResponseHandler(_context, jsonResponseHandler, _fileName);
            String url = this.populateUrl(_word, "examples");
            _httpClient.get(url, null, _currentResponseHandler);
        }
    }

    private StringBuilder append(@NonNull String value) {
        _sb.append("/").append(value);
        return _sb;
    }

    private String populateUrl(String word, String operation) {
        if (_sb.toString().equals(_baseUrl)) {
            this.append("words");
        }
        this.append(word);
        this.append(operation);
        return _sb.toString();
    }

    private void setRequestHeaders() {
        _httpClient.addHeader("X-Mashape-Key", "kVHOeIBNG5mshNUEh1WvWsQeGp1bp1UEVgtjsnFhd5lLiSnBgx");
        _httpClient.addHeader("Accept", "application/json");
    }

    private boolean isNetworkOnline() {
        if (_connectivity == null) return false;
        else if (Build.VERSION.SDK_INT >= 21) {
            Network[] info = _connectivity.getAllNetworks();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i] != null && _connectivity.getNetworkInfo(info[i]).isConnected()) {
                        return true;
                    }
                }
            }
        } else {
            NetworkInfo[] info = _connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i] != null && info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
            final NetworkInfo activeNetwork = _connectivity.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                return true;
            }
        }

        return false;

    }
}
