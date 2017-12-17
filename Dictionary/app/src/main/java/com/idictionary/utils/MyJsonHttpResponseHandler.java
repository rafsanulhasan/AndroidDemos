package com.idictionary.utils;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MyJsonHttpResponseHandler extends JsonHttpResponseHandler {
    private JsonHttpResponseHandler _responseHandler;
    private JsonCacher _cacher;
    private String _fileName;
    private String _json;

    protected MyJsonHttpResponseHandler() {
        super();
    }

    protected MyJsonHttpResponseHandler(String encoding) {
        super(encoding);
    }

    protected MyJsonHttpResponseHandler(boolean useRFC5179CompatibilityMode) {
        super(useRFC5179CompatibilityMode);
    }

    protected MyJsonHttpResponseHandler(String encoding, boolean useRFC5179CompatibilityMode) {
        super(encoding, useRFC5179CompatibilityMode);
    }

    public MyJsonHttpResponseHandler(Context context, JsonHttpResponseHandler responseHandler, String fileName) {
        this();
        _fileName = fileName;
        _responseHandler = responseHandler;
        _cacher = new JsonCacher(context);
        _json = _cacher.readJsonFileData(_fileName);
    }

    public MyJsonHttpResponseHandler(Context context, JsonHttpResponseHandler responseHandler, String fileName, String encoding) {
        super(encoding);
        _fileName = fileName;
        _responseHandler = responseHandler;
        _cacher = new JsonCacher(context);
        _json = _cacher.readJsonFileData(_fileName);
    }

    public MyJsonHttpResponseHandler(Context context, JsonHttpResponseHandler responseHandler, String fileName, boolean useRFC5179CompatibilityMode) {
        this(useRFC5179CompatibilityMode);
        _fileName = fileName;
        _responseHandler = responseHandler;
        _cacher = new JsonCacher(context);
        _json = _cacher.readJsonFileData(_fileName);
    }

    public MyJsonHttpResponseHandler(Context context, JsonHttpResponseHandler responseHandler, String fileName, String encoding, boolean useRFC5179CompatibilityMode) {
        this(encoding, useRFC5179CompatibilityMode);
        _fileName = fileName;
        _responseHandler = responseHandler;
        _cacher = new JsonCacher(context);
        _json = _cacher.readJsonFileData(_fileName);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        _responseHandler.onFailure(statusCode, headers, responseString, throwable);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        _responseHandler.onFailure(statusCode, headers, throwable, errorResponse);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        _responseHandler.onFailure(statusCode, headers, throwable, errorResponse);
    }

    @Override
    public void onFinish() {
        _responseHandler.onFinish();
    }

    @Override
    public void onProgress(long bytesWritten, long totalSize) {
        _responseHandler.onProgress(bytesWritten, totalSize);
    }

    @Override
    public void onStart() {
        if (_json != null)
            try {
                _responseHandler.onSuccess(200, null, new JSONObject(_json));
                _responseHandler.onFinish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        else
            _responseHandler.onStart();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        if (_json == null) {
            _cacher.createJsonFileData(_fileName, responseString);
            _json = _cacher.readJsonFileData(_fileName);
        }
        _responseHandler.onSuccess(statusCode, headers, responseString);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        if (_json == null) {
            _cacher.createJsonFileData(_fileName, response.toString());
            _json = _cacher.readJsonFileData(_fileName);
        }
        _responseHandler.onSuccess(statusCode, headers, response);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        if (_json == null) {
            _cacher.createJsonFileData(_fileName, response.toString());
            _json = _cacher.readJsonFileData(_fileName);
        }
        _responseHandler.onSuccess(statusCode, headers, response);
    }
}
