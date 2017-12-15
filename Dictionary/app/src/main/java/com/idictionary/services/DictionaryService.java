package com.idictionary.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.conn.HttpHostConnectException;


public class DictionaryService {
    private final String _baseUrl = "https://wordsapiv1.p.mashape.com";
    private final Context _context;
    private StringBuilder _sb;
    private AsyncHttpClient _httpClient;
    private RequestParams _params;
    private List<String> _result;
    private ConnectivityManager _connectivity;

    public DictionaryService(Context context) {
        _context = context;
        _connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        _sb = new StringBuilder(_baseUrl);
        _httpClient = new AsyncHttpClient();
        _httpClient.setConnectTimeout(500);
        _httpClient.setResponseTimeout(100);
        _httpClient.setTimeout(100);
        _httpClient.setMaxRetriesAndTimeout(5, 100);
        _params = new RequestParams();
        _result = new ArrayList<>();
        this.setRequestHeaders();
    }

    public void GetDefinition(@NonNull String word, JsonHttpResponseHandler jsonResponseHandler) throws HttpHostConnectException {
        if (!isNetworkOnline()) {
            throw new HttpHostConnectException(new IOException("No Internet Connection"), null);
        }

        String url = this.populateUrl(word, "definitions");
        _httpClient.get(url, null, jsonResponseHandler);
    }

    public void GetSynonym(@NonNull String word, JsonHttpResponseHandler jsonResponseHandler) throws HttpHostConnectException {
        if (!isNetworkOnline()) {
            throw new HttpHostConnectException(new IOException("No Internet Connection"), null);
        }

        String url = this.populateUrl(word, "synonyms");
        _httpClient.get(url, null, jsonResponseHandler);
    }

    public void GetAntonym(@NonNull String word, JsonHttpResponseHandler jsonResponseHandler) throws HttpHostConnectException {
        if (!isNetworkOnline()) {
            throw new HttpHostConnectException(new IOException("No Internet Connection"), null);
        }

        String url = this.populateUrl(word, "antonyms");
        _httpClient.get(url, null, jsonResponseHandler);
    }

    public void GetExample(@NonNull String word, JsonHttpResponseHandler jsonResponseHandler) throws HttpHostConnectException {
        if (!isNetworkOnline()) {
            throw new HttpHostConnectException(new IOException("No Internet Connection"), null);
        }

        String url = this.populateUrl(word, "examples");
        _httpClient.get(url, null, jsonResponseHandler);
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
