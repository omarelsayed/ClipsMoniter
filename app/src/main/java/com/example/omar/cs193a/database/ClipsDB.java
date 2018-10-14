package com.example.omar.cs193a.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;

public class ClipsDB extends SQLiteOpenHelper {

    public ClipsDB(@Nullable Context context) {
        super(context, scheme.DB_NAME, null, scheme.DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(scheme.QUERY_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addClip(String clip) {
        if (!findClip(clip)) {
            ContentValues values = new ContentValues();
            values.put(scheme.COL_CONTENT, clip);
            getWritableDatabase().insert(scheme.TABLE_CLIPS, null, values);
            return true;
        }

        return false;

    }

    public boolean findClip(String clip) {
        Cursor cursor = getReadableDatabase().rawQuery("select *" + " from " + scheme.TABLE_CLIPS + " where " + scheme.COL_CONTENT + "=?", new String[]{clip.toLowerCase()});
        return cursor.moveToFirst();
    }

    public ArrayList<String> getClips() {
        ArrayList<String> clips = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("select * from " + scheme.TABLE_CLIPS, null);
        while (cursor.moveToNext()) {
            clips.add(cursor.getString(cursor.getColumnIndex(scheme.COL_CONTENT)));
        }

        return clips;
    }


    public boolean removeClip(String clip) {
        if (findClip(clip)) {
            getWritableDatabase().delete(scheme.TABLE_CLIPS, scheme.QUERY_FUNC_LCASE + scheme.COL_CONTENT + ")", new String[]{clip.toLowerCase()});
            return true;
        }

        return false;

    }

    public class scheme {
        public static final String DB_NAME = "clipDB";
        public static final int DB_VER = 1;

        public static final String TABLE_CLIPS = "clips";

        public static final String COL_CONTENT = "clip_content";
        public static final String COL_ID = "_id";

        public static final String QUERY_CREATE = "create table "
                + TABLE_CLIPS
                + " ("
                + COL_ID + " integer primary key,"
                + COL_CONTENT + " text"
                + ")";

        public static final String QUERY_FUNC_LCASE = "lower`(";

    }
}
