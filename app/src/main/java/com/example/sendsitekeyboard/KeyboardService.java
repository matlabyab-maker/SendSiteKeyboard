package com.example.sendsitekeyboard;

import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.TypedValue;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KeyboardService extends InputMethodService {
    private TextView sendButton;
    private boolean browserField = false;

    private static final Set<String> BROWSERS = new HashSet<>(Arrays.asList(
        "com.android.chrome", "org.mozilla.firefox", "com.sec.android.app.sbrowser",
        "com.microsoft.emmx", "com.brave.browser", "com.opera.browser",
        "com.opera.mini.native", "com.kiwibrowser.browser", "com.yandex.browser"
    ));

    @Override public View onCreateInputView() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(226, 226, 218));
        root.setPadding(dp(1), dp(1), dp(1), dp(1));

        LinearLayout top = row(root);
        String[] first = {"Past","Copy","Copy\nScreen","Select","ReDo","UnDo","8\nHistory","Tool","File","Send"};
        for (String label : first) {
            TextView b = key(label, 15, Color.rgb(250,249,241));
            if (label.equals("Send")) {
                sendButton = b;
                b.setTextColor(Color.rgb(45,55,70));
                b.setTypeface(Typeface.DEFAULT_BOLD);
                b.setOnClickListener(v -> sendFromSite());
            } else {
                b.setOnClickListener(v -> { /* Other controls are intentionally inactive in this stage. */ });
            }
            top.addView(b, new LinearLayout.LayoutParams(0, dp(48), 1));
        }

        LinearLayout second = row(root);
        for (int i=0;i<5;i++) second.addView(key("",14,Color.rgb(250,249,241)), new LinearLayout.LayoutParams(0,dp(34),1));
        TextView up = key("↑",22,Color.rgb(250,249,241)); second.addView(up,new LinearLayout.LayoutParams(dp(40),dp(34)));
        TextView down = key("↓",22,Color.rgb(250,249,241)); second.addView(down,new LinearLayout.LayoutParams(dp(40),dp(34)));
        second.addView(key("Resize",14,Color.rgb(250,249,241)),new LinearLayout.LayoutParams(0,dp(34),1));
        second.addView(key("Muse",16,Color.rgb(250,249,241)),new LinearLayout.LayoutParams(0,dp(34),1));

        LinearLayout nums = row(root);
        String[] num = {"!","۱\n@","۲\n#","۳\n$","۴\n%","۵\n^","۶\n&","۷\n*","۸\n(","۹\n)","۰","⌫"};
        for(int i=0;i<num.length;i++){
            TextView k=key(num[i],17,i==11?Color.rgb(250,218,222):Color.rgb(250,249,241));
            nums.addView(k,new LinearLayout.LayoutParams(0,dp(40),i==11?1.4f:1));
        }

        addLetterRow(root, "ض ص ث ق ف غ ع ه خ ح ج", 11);
        LinearLayout letters2=row(root);
        String[] r2={"ش","س","ی","ب","ل","ا","ت","ن","م","ک","گ","Enter"};
        for(String s:r2) {
            TextView k=key(s,20,s.equals("Enter")?Color.rgb(211,230,255):Color.rgb(250,249,241));
            letters2.addView(k,new LinearLayout.LayoutParams(0,dp(43),s.equals("Enter")?1.35f:1));
        }
        LinearLayout letters3=row(root);
        String[] r3={"Caps","ظ","ط","ز","ر","ذ","د","پ","و","چ","=","-"};
        for(String s:r3) letters3.addView(key(s,18,Color.rgb(250,249,241)),new LinearLayout.LayoutParams(0,dp(40),1));

        LinearLayout bottom=row(root);
        String[] last={"😀","&@#\n*¥✓","اعراب","🌐 FA","Space","،",".","؟","⬅","➡","↑","↓","[","]"};
        for(String s:last){
            TextView k=key(s,s.equals("Space")?15:16,Color.rgb(250,249,241));
            float weight=s.equals("Space")?2.5f:1;
            bottom.addView(k,new LinearLayout.LayoutParams(0,dp(48),weight));
        }
        refreshSendState();
        return root;
    }

    private void addLetterRow(LinearLayout root, String letters, int count) {
        LinearLayout r=row(root);
        String[] arr=letters.split(" ");
        for(String s:arr) r.addView(key(s,21,Color.rgb(250,249,241)),new LinearLayout.LayoutParams(0,dp(43),1));
        TextView back=key("⌫",19,Color.rgb(250,218,222));
        r.addView(back,new LinearLayout.LayoutParams(0,dp(43),1.35f));
    }

    private LinearLayout row(LinearLayout parent) {
        LinearLayout r=new LinearLayout(this); r.setOrientation(LinearLayout.HORIZONTAL);
        parent.addView(r,new LinearLayout.LayoutParams(-1,-2)); return r;
    }
    private TextView key(String text,int size,int color) {
        TextView t=new TextView(this); t.setText(text); t.setTextSize(TypedValue.COMPLEX_UNIT_SP,size);
        t.setTextColor(Color.rgb(40,53,69)); t.setGravity(Gravity.CENTER); t.setMaxLines(2);
        t.setBackground(bg(color)); t.setPadding(dp(1),0,dp(1),0);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,dp(42),1);
        lp.setMargins(dp(1),dp(1),dp(1),dp(1)); t.setLayoutParams(lp); return t;
    }
    private GradientDrawable bg(int c) {
        GradientDrawable d=new GradientDrawable(); d.setColor(c); d.setCornerRadius(dp(5));
        d.setStroke(dp(1),Color.rgb(210,210,202)); return d;
    }
    private int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}

    @Override public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info,restarting);
        refreshSendState();
    }
    @Override public void onWindowShown() { super.onWindowShown(); refreshSendState(); }

    private void refreshSendState() {
        EditorInfo info=getCurrentInputEditorInfo();
        int editorAction = info == null ? EditorInfo.IME_ACTION_UNSPECIFIED
                : (info.imeOptions & EditorInfo.IME_MASK_ACTION);
        // Send is available only when the focused field explicitly advertises SEND.
        // Never treat unspecified/NONE/GO/SEARCH/ENTER as a Send action.
        boolean explicitSendAction = editorAction == EditorInfo.IME_ACTION_SEND;
        browserField = info != null && BROWSERS.contains(info.packageName) && explicitSendAction;
        if(sendButton!=null) {
            sendButton.setEnabled(browserField);
            sendButton.setAlpha(browserField ? 1.0f : 0.35f);
            sendButton.setTextColor(browserField ? Color.rgb(45,55,70) : Color.GRAY);
        }
    }

    private void sendFromSite() {
        refreshSendState();
        if (!browserField) return;
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        EditorInfo info = getCurrentInputEditorInfo();
        if (info == null) return;

        int action = info.imeOptions & EditorInfo.IME_MASK_ACTION;
        // Do not synthesize Enter or fall back to another editor action.
        // Send only invokes the field's explicitly declared IME_ACTION_SEND.
        if (action == EditorInfo.IME_ACTION_SEND) {
            ic.performEditorAction(EditorInfo.IME_ACTION_SEND);
        }
    }
}
