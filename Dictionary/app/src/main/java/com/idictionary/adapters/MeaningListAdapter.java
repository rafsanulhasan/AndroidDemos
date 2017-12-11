package com.idictionary.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.idictionary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rafsan on 11-Dec-17.
 */

public class MeaningListAdapter extends ArrayAdapter {
    private final Activity _context;
    private final List<String> _meaningList;
    public MeaningListAdapter(@NonNull Activity context, List<String> meaningList) {
        super(context, R.layout.dic_list_item, meaningList);
        if (meaningList.isEmpty()) {
            meaningList = new ArrayList<>();
            meaningList.add("no result");
        }
        this._context = context;
        this._meaningList = meaningList;
    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater =_context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.dic_list_item, null,true);

        //this code gets references to objects in the listview_row.xml file
        TextView meaningTextField = (TextView) rowView.findViewById(R.id.txtMeaning);

        //this code sets the values of the objects to values from the arrays
        meaningTextField.setText(_meaningList.get(position));

        return rowView;

    };
}
