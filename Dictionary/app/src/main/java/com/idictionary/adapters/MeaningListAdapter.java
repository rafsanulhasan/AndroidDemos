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

public class MeaningListAdapter extends ArrayAdapter<String> implements View.OnClickListener {
    private final Activity _activity;
    private final Context _context;
    private List<String> _meaningList;
    private LayoutInflater _inflater;
    private View _rowView;
    private LinearLayout _layout;

    public MeaningListAdapter(@NonNull Activity activity, @NonNull List<String> meaningList) {
        super(activity, R.layout.content_dictionary, meaningList);
        _activity = activity;
        _context = activity;
        this._meaningList = meaningList;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        _inflater = _activity.getLayoutInflater();
        _rowView = _inflater.inflate(R.layout.dic_def_result_row, null, false);
        TextView meaningText = _rowView.findViewById(R.id.txtDefinition);
        meaningText.setOnClickListener(this);
        //this code sets the values of the objects to values from the arrays
        meaningText.setText(_meaningList.get(position));
        return _rowView;

    }

    @Override
    public void onClick(View view) {
        View txtSearchEdit = _activity.findViewById(R.id.txtSearchEdit);
        txtSearchEdit.getOnFocusChangeListener().onFocusChange(txtSearchEdit, false);
    }
}
