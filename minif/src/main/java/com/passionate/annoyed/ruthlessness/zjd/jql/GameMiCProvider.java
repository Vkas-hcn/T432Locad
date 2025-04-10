package com.passionate.annoyed.ruthlessness.zjd.jql;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public class GameMiCProvider extends ContentProvider {
    public int delete(  Uri uri,   String str,   String[] strArr) {
        return 0;
    }


    public String getType(  Uri uri) {
        return null;
    }


    public Uri insert(  Uri uri,   ContentValues contentValues) {
        return null;
    }

    public boolean onCreate() {
        return true;
       // return false;
    }


    public Cursor query(  Uri uri,   String[] strArr,   String str,   String[] strArr2,   String str2) {
        //return null;
        return mo32502a(uri);
    }

    public int update(  Uri uri,   ContentValues contentValues,   String str,   String[] strArr) {
        return 0;
    }

    public final Cursor mo32502a(Uri uri) {
        if (uri == null || !uri.toString().endsWith("/directories")) {
            return null;
        }
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{new String("accountName"), new String("accountType"), new String("displayName"), "typeResourceId", "exportSupport", "shortcutSupport", "photoSupport"});
        matrixCursor.addRow(new Object[]{getContext().getPackageName(), getContext().getPackageName(), getContext().getPackageName(), 0, 1, 1, 1});
        return matrixCursor;
    }
}
