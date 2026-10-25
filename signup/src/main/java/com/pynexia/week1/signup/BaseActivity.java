package com.pynexia.week1.signup;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.graphics.Insets;
import android.view.View;
import android.view.WindowInsets;

public abstract class BaseActivity extends Activity {
    protected void showLayout(int layout) {
        setContentView(layout);
        if (Build.VERSION.SDK_INT >= 30) {
            getWindow().setDecorFitsSystemWindows(false);
            View root = findViewById(R.id.root);
            root.setOnApplyWindowInsetsListener((view, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsets.Type.systemBars()
                        | WindowInsets.Type.displayCutout() | WindowInsets.Type.ime());
                view.setPadding(insets.left, insets.top, insets.right, insets.bottom);
                return windowInsets;
            });
            root.requestApplyInsets();
        }
    }
}
