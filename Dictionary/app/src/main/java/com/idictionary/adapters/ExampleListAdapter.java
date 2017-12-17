package com.idictionary.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idictionary.R;

import java.util.List;

public class ExampleListAdapter extends ArrayAdapter<String> implements View.OnClickListener {
    private final Activity _activity;
    private final Context _context;
    private List<String> _synonymList;
    private LayoutInflater _inflater;
    private View _rowView;
    private LinearLayout _layout;

    public ExampleListAdapter(@NonNull Activity activity, @NonNull List<String> synonymList) {
        super(activity, R.layout.content_dictionary, synonymList);
        _activity = activity;
        _context = activity;
        this._synonymList = synonymList;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        _inflater = _activity.getLayoutInflater();
        _rowView = _inflater.inflate(R.layout.dic_example_result_row, null, false);
        TextView exampleText = _rowView.findViewById(R.id.txtExample);
        exampleText.setOnClickListener(this);
        //this code sets the values of the objects to values from the arrays
        exampleText.setText(_synonymList.get(position));
        return _rowView;
    }

    @Override
    public void onClick(View view) {
        View txtSearchEdit = _activity.findViewById(R.id.txtSearchEdit);
        txtSearchEdit.getOnFocusChangeListener().onFocusChange(txtSearchEdit, false);
    }
}
