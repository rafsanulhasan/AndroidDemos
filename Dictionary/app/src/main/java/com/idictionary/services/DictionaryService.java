package com.idictionary.services;

import android.content.Context;
import android.widget.Toast;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        this.setRequestParams();
    }

    public List<String> GetDefinition(String word) {
        _result = new ArrayList<>();
        String url = this.populateUrl(word, "definitions");
        Toast.makeText(_context, url, Toast.LENGTH_LONG).show();

        RequestHandle requestHandle = _httpClient.get(url, _params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Toast.makeText(_context, statusCode, Toast.LENGTH_LONG).show();
                try {
                    final JSONArray definitions = response.getJSONArray("definitions");
                    if (definitions.length() > 0) {
                        for (int i = 0; i < definitions.length(); i++) {
                            JSONObject def = definitions.getJSONObject(i);
                            String d = definitions.get(i).toString();
                            _result.add(d);
                        }
                    } else
                        _result.add("no result");
                    Toast.makeText(_context, response.toString(), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    Toast.makeText(_context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                catch (Exception e) {
                    Toast.makeText(_context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Toast.makeText(_context, statusCode, Toast.LENGTH_LONG).show();
            }
        });

        if (_result.isEmpty())
            _result.add("no data");

        return _result;
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

    private void setRequestParams() {
        _params.put("X-Mashape-Key", "kVHOeIBNG5mshNUEh1WvWsQeGp1bp1UEVgtjsnFhd5lLiSnBgx");
        _params.put("Accept", "application/json");
    }
}
