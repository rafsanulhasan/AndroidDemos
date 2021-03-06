package com.idictionary.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.conn.HttpHostConnectException;

public class DictionaryServiceVolley {
    private static DictionaryServiceVolley _svc;
    private final String _baseUrl = "https://wordsapiv1.p.mashape.com";
    private String _word;
    private Context _context;
    private RequestQueue _reqQueue;
    private JsonObjectRequest _reqStr;
    private StringBuilder _sb;
    private ConnectivityManager _connectivity;


    private DictionaryServiceVolley(Context context, String word) {
        _context = context;
        _sb = new StringBuilder(_baseUrl);
        _reqQueue = Volley.newRequestQueue(_context.getApplicationContext());
        _word = word;
    }

    public static synchronized DictionaryServiceVolley getInstance(Context context, String word) {
        return _svc == null ? new DictionaryServiceVolley(context, word) : _svc;
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

    public void GetDefinition(final Response.Listener<JSONObject> responseListener, final Response.ErrorListener errorListener) throws HttpHostConnectException, JSONException {

        //if (!this.isNetworkOnline())
        //throw new HttpHostConnectException(new IOException("No internet connection"), null);leName);
        String url = this.populateUrl(_word, "definitions");
        _reqStr = new MashapeRequest(Request.Method.GET, url, null, responseListener, errorListener);
        _reqQueue.add(_reqStr);
    }

    public void GetAntonym(final Response.Listener<JSONObject> responseListener, final Response.ErrorListener errorListener) throws HttpHostConnectException, JSONException {

        //if (!this.isNetworkOnline())
        //throw new HttpHostConnectException(new IOException("No internet connection"), null);
        String url = this.populateUrl(_word, "antonyms");
        _reqStr = new MashapeRequest(Request.Method.GET, url, null, responseListener, errorListener);
        _reqQueue.add(_reqStr);
    }

    public void GetSynonym(final Response.Listener<JSONObject> responseListener, final Response.ErrorListener errorListener) throws HttpHostConnectException, JSONException {

        //if (!this.isNetworkOnline())
        //throw new HttpHostConnectException(new IOException("No internet connection"), null);
        String url = this.populateUrl(_word, "synonyms");
        _reqStr = new MashapeRequest(Request.Method.GET, url, null, responseListener, errorListener);
        _reqQueue.add(_reqStr);
    }

    public void GetExample(final Response.Listener<JSONObject> responseListener, final Response.ErrorListener errorListener) throws HttpHostConnectException, JSONException {

        //if (!this.isNetworkOnline())
        //throw new HttpHostConnectException(new IOException("No internet connection"), null);
        String url = this.populateUrl(_word, "examples");
        _reqStr = new MashapeRequest(Request.Method.GET, url, null, responseListener, errorListener);
        _reqQueue.add(_reqStr);
    }

    private class MashapeRequest extends JsonObjectRequest {

        public MashapeRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(url, jsonRequest, listener, errorListener);
            setShouldCache(true);
        }

        public MashapeRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
            setShouldCache(true);
            setCacheEntry(new Cache.Entry() {
            });
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("X-Mashape-Key", "kVHOeIBNG5mshNUEh1WvWsQeGp1bp1UEVgtjsnFhd5lLiSnBgx");
            headers.put("Accept", "application/json");
            return headers;
        }

        @Override
        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
            try {
                Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                if (cacheEntry == null) {
                    cacheEntry = new Cache.Entry();
                }
                final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                long now = System.currentTimeMillis();
                final long softExpire = now + cacheHitButRefreshed;
                final long ttl = now + cacheExpired;
                cacheEntry.data = response.data;
                cacheEntry.softTtl = softExpire;
                cacheEntry.ttl = ttl;
                String headerValue;
                headerValue = response.headers.get("Date");
                if (headerValue != null) {
                    cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                }
                headerValue = response.headers.get("Last-Modified");
                if (headerValue != null) {
                    cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                }
                cacheEntry.responseHeaders = response.headers;
                final String jsonString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers));
                return Response.success(new JSONObject(jsonString), cacheEntry);
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JSONException e) {
                return Response.error(new ParseError(e));
            }
        }
    }
}

