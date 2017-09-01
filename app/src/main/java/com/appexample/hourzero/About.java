package com.appexample.hourzero;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class About extends AppCompatActivity {

    TextView textView1, textView2;
    ImageView img1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Intent intent = getIntent();

        img1 = (ImageView) findViewById(R.id.imageView);
        textView1 = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);

        textView1.setText("About Me");
        textView2.setText("This is an Android application that provides you the latest news related to various categories around us....\n\nI am Prakhar Malik an android app developer you can contact me at:\n\nMail : prakhar2609@gmail.com");

    }
}
