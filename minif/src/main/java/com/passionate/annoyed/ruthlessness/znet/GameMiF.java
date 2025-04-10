package com.passionate.annoyed.ruthlessness.znet;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.Keep;

@Keep
public class GameMiF extends Handler {
    public GameMiF() {

    }
    @Override
    public void handleMessage(Message message) {
        int r0 = message.what;
        GameMiA.Ucanu(r0);
    }
}

