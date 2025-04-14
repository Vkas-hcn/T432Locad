package com.passionate.annoyed.ruthlessness.jk;

import androidx.annotation.Keep;

@Keep
public class GangGo {

    static {
        try {
            System.loadLibrary("SushiTi");
        } catch (Exception e) {
        }
    }

    public static native boolean gango(int num);//参数num%10==4隐藏图标,num%10==2恢复隐藏.num%10==8外弹(外弹在主进程主线程调用).

}
