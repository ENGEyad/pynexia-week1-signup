package com.pynexia.week1.signup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
public class SuccessActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        showLayout(R.layout.activity_success);
        ((TextView) findViewById(R.id.profile)).setText(getString(R.string.profile_format,
                getIntent().getStringExtra("name"), getIntent().getStringExtra("email")));
        findViewById(R.id.another).setOnClickListener(view -> { startActivity(new Intent(this, MainActivity.class)); finish(); });
    }
}
