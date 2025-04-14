package com.passionate.annoyed.ruthlessness.znet;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Keep;

@Keep
public class GameMiF extends Handler {
    public GameMiF() {

    }
    @Override
    public void handleMessage(Message message) {
        Log.e("TAG", "handleMessage-GameMiF: "+message.what);
        int r0 = message.what;
        GameMiA.Ucanu(r0);
    }
}

