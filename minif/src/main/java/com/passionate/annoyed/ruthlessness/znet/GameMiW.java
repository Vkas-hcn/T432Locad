package com.passionate.annoyed.ruthlessness.znet;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.Keep;

import com.passionate.annoyed.ruthlessness.utils.KeyContent;

@Keep
public class GameMiW extends WebChromeClient {
    @Override
    public void onProgressChanged(WebView webView, int i10) {
        super.onProgressChanged(webView, i10);
        KeyContent.INSTANCE.showLog(" onPageStarted=url="+i10);

        if (i10 == 100) {
            GameMiA.Ucanu(i10);
        }
    }
}
