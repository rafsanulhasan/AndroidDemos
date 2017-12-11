package com.idictionary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Dictionary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_dictionary);
        final String word = getIntent().getExtras().get("word").toString();
        final TextView txtSearch = findViewById(R.id.txtSearch);
        txtSearch.setText(word);
    }
}
