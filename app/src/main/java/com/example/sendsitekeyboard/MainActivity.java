package com.example.sendsitekeyboard;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(28, 28, 28, 28);
        root.setBackgroundColor(Color.rgb(245, 245, 239));

        TextView title = new TextView(this);
        title.setText("SendSite Keyboard");
        title.setTextSize(24);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(Color.rgb(40, 53, 69));
        title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));

        TextView instructions = new TextView(this);
        instructions.setText("برای استفاده از کیبورد، ابتدا آن را در تنظیمات دستگاه فعال کنید؛ سپس هنگام نوشتن، از فهرست تغییر کیبورد انتخابش کنید.");
        instructions.setTextSize(17);
        instructions.setTextColor(Color.rgb(45, 45, 45));
        instructions.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(-1, -2);
        ip.setMargins(0, 24, 0, 24);
        root.addView(instructions, ip);

        Button enable = new Button(this);
        enable.setText("فعال‌سازی کیبورد در تنظیمات");
        enable.setOnClickListener(v -> startActivity(new Intent("android.settings.INPUT_METHOD_SETTINGS")));
        root.addView(enable, new LinearLayout.LayoutParams(-1, -2));

        Button choose = new Button(this);
        choose.setText("انتخاب کیبورد");
        choose.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                try { imm.showInputMethodPicker(); }
                catch (Exception ignored) {
                    startActivity(new Intent("android.settings.INPUT_METHOD_SETTINGS"));
                }
            }
        });
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2);
        cp.setMargins(0, 10, 0, 0);
        root.addView(choose, cp);
        setContentView(root);
    }
}
