package io.kevsoft.userrestclient;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by kevin_000 on 22.03.2018.
 */

public class HomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Displays Home Screen
        setContentView(R.layout.home);

        TextView helloTextView = findViewById(R.id.welcomeText);
        helloTextView.setText(String.format("Welcome %s", getIntent().getStringExtra("username")));
    }
}
