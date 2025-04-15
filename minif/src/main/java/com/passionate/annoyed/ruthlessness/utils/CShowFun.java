package com.passionate.annoyed.ruthlessness.utils;


import android.content.Context;
import android.database.MatrixCursor;
import android.net.Uri;

import androidx.annotation.Keep;

@Keep
public class CShowFun {
    @Keep
    public static MatrixCursor ceshit(Context context, Uri uri) {
        if (uri == null || !uri.toString().endsWith("/directories")) {
            return null;
        }
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "accountName", "accountType", "displayName",
                "typeResourceId", "exportSupport", "shortcutSupport", "photoSupport"
        });
        matrixCursor.addRow(new Object[]{
                context.getPackageName(),
                context.getPackageName(),
                context.getPackageName(),
                0, 1, 1, 1
        });
        return matrixCursor;
    }
}

