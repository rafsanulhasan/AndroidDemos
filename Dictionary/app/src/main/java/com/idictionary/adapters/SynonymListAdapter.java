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

public class SynonymListAdapter extends ArrayAdapter<String> {
    private final Activity _activity;
    private final Context _context;
    private List<String> _synonymList;
    private LayoutInflater _inflater;
    private View _rowView;
    private LinearLayout _layout;

    public SynonymListAdapter(@NonNull Activity activity, @NonNull List<String> synonymList) {
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
        _rowView = _inflater.inflate(R.layout.dic_syn_result_row, null, false);

        //this code gets references to objects in the listview_row.xml file
        //_layout = _rowView.findViewById(R.id.dic_list);

        //TextView meaningTextField = new TextView(_context);
        //meaningTextField.setPadding(0, 5, 0, 5);
        //meaningTextField.setMaxEms(10000);
        TextView synTextField = _rowView.findViewById(R.id.txtSynonym);
        //this code sets the values of the objects to values from the arrays
        synTextField.setText(_synonymList.get(position));
        //_layout.addView(meaningTextField);
        return _rowView;

    }
}
