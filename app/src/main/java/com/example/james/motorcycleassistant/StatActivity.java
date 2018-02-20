package com.example.james.motorcycleassistant;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;


public class StatActivity extends Activity{

    private TextView baValue ;
    private TextView cornerVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        Intent intent = getIntent();

        baValue = (TextView) findViewById(R.id.baValue);
        cornerVal = (TextView) findViewById(R.id.cornerVal);

        String baVal = intent.getExtras().get("yzValue").toString();
        String cValue = intent.getExtras().get("xValue").toString();
        baValue.setText(String.valueOf(baVal));
        cornerVal.setText(String.valueOf(cValue));


    }


}
