package com.example.murphy.gpstest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Murphy on 16/9/2.
 */
public class WelcomeActivity extends Activity implements View.OnClickListener {
    Button start;
    EditText fileSuffix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        fileSuffix = (EditText) findViewById(R.id.filename);
        start = (Button)findViewById(R.id.start);
        start.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start:
                String filename = fileSuffix.getText().toString();
                if(!TextUtils.isEmpty(filename)){
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    intent.putExtra("filename", filename);
                    startActivity(intent);
                }else {
                    Toast.makeText(WelcomeActivity.this, "请输入文件名后缀", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
