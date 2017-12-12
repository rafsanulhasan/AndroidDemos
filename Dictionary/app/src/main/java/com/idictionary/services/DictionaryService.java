package com.idictionary.services;

import android.content.Context;
import android.widget.Toast;


import com.idictionary.Dictionary;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import cz.msebera.android.httpclient.Header;

//import com.loopj.android.http.AsyncHttpClient;
//import com.loopj.android.http.JsonHttpResponseHandler;
//import com.loopj.android.http.RequestParams;

/**
 * Created by Rafsan on 11-Dec-17.
 */


public class DictionaryService {
    private final String _baseUrl = "https://wordsapiv1.p.mashape.com/words";
    private StringBuilder _sb;
    private AsyncHttpClient _httpClient;
    private RequestParams _params;
    private List<String> _result;
    private final Context _context;

    public DictionaryService(Context context) {
        _context = context;
        _sb = new StringBuilder(_baseUrl);
        _httpClient = new AsyncHttpClient();
        _params = new RequestParams();
        _result = new ArrayList<>();
        this.setRequestHeaders();
    }

    public void GetDefinition(String word, JsonHttpResponseHandler jsonResponseHandler) {

        String url = this.populateUrl(word, "definitions");

        _httpClient.get(url, null, jsonResponseHandler);
    }

    private void append(String value) {
        if (_sb.length() > 0 )
            _sb.append("/" + value);
        else
            _sb.append(value);
    }

    private String populateUrl(String word, String operation) {
        this.append(word);
        this.append(operation);
        return _sb.toString();
    }

    private void setRequestHeaders() {
        _httpClient.addHeader("X-Mashape-Key", "kVHOeIBNG5mshNUEh1WvWsQeGp1bp1UEVgtjsnFhd5lLiSnBgx");
        _httpClient.addHeader("Accept", "application/json");
    }
}
