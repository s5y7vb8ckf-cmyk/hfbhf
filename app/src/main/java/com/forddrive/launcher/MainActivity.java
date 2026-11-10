package com.forddrive.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.net.Uri;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import android.view.WindowManager;
import android.content.pm.ActivityInfo;

public class MainActivity extends Activity {
    private WebView web;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        web = new WebView(this);
        web.setBackgroundColor(0xff07111f);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setAllowFileAccess(true);
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());
        web.addJavascriptInterface(new Bridge(), "Android");
        setContentView(web);
        web.loadUrl("file:///android_asset/index.html");
    }
    public final class Bridge {
        @JavascriptInterface public void open(String action) {
            runOnUiThread(() -> launch(action));
        }
    }
    private void launch(String action) {
        Intent i;
        switch (action) {
            case "maps":
                i = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="));
                i.setPackage("com.google.android.apps.maps");
                if (i.resolveActivity(getPackageManager()) == null) i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com"));
                break;
            case "music":
                i = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.youtube.music");
                if (i == null) i = new Intent("android.intent.action.MUSIC_PLAYER");
                if (i.resolveActivity(getPackageManager()) == null) i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://music.youtube.com"));
                break;
            case "phone": i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:")); break;
            case "bluetooth": i = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS); break;
            case "androidsettings": i = new Intent(Settings.ACTION_SETTINGS); break;
            case "apps": i = new Intent(Settings.ACTION_APPLICATION_SETTINGS); break;
            default: return;
        }
        try { startActivity(i); } catch (Exception ex) {
            try { startActivity(new Intent(Settings.ACTION_SETTINGS)); } catch (Exception ignored) { }
        }
    }
    @Override public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack(); else super.onBackPressed();
    }
}
