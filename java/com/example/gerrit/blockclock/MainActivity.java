package com.example.gerrit.blockclock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Something to fix in an actual release: this date will not roll over at midnight

        Date today = Calendar.getInstance().getTime();
        int month  = today.getMonth() + 1;
        int day    = today.getDate();
        int year   = today.getYear() + 1900;

        String date = "Today: " + ((month < 10) ? "0" + month : month) + "/" + ((day < 10) ? "0" + day : day) + "/" + year;

        setTitle(date);
    }

}
