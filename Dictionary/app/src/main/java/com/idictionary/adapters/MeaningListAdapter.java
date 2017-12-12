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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rafsan on 11-Dec-17.
 */

public class MeaningListAdapter extends ArrayAdapter<String> {
    private final Activity _activity;
    private final Context _context;
    private List<String> _meaningList;

    public MeaningListAdapter(@NonNull Activity activity, List<String> meaningList) {
        super(activity, R.layout.content_dictionary, meaningList);
        _activity = activity;
        _context = activity;
        if (meaningList == null) {
            meaningList = new ArrayList<>();
            meaningList.add("no result");
        }
        this._meaningList = meaningList;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = _activity.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.dic_list_item, null, false);

        //this code gets references to objects in the listview_row.xml file
        LinearLayout layout = rowView.findViewById(R.id.dic_list);
        TextView meaningTextField = new TextView(_context);
        //TextView meaningTextField = rowView.findViewById(R.id.txtMeaning);
        meaningTextField.setPadding(0, 5, 0, 5);
        //this code sets the values of the objects to values from the arrays
        meaningTextField.setText(_meaningList.get(position));
        meaningTextField.setVisibility(View.VISIBLE);
        layout.addView(meaningTextField);
        return rowView;

    }
}
