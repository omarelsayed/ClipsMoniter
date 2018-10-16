package com.example.omar.cs193a.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.example.omar.cs193a.Clip;

import java.util.ArrayList;
import java.util.Collections;

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

    public boolean addClip(Clip clip) {
        if (!findClip(clip)) {
            ContentValues values = new ContentValues();
            values.put(scheme.COL_CONTENT, clip.getContent());
            values.put(scheme.COL_DATE, clip.getDate());
            getWritableDatabase().insert(scheme.TABLE_CLIPS, null, values);
            return true;
        }

        return false;

    }

    public boolean findClip(Clip clip) {
        String clipContent = clip.getContent();
        // Lower Result Set
        //Cursor cursor = getReadableDatabase().rawQuery("select *" + " from " + scheme.TABLE_CLIPS + " where lower(" + scheme.COL_CONTENT + ")=?", new String[]{clipContent.toLowerCase()});

        // Don't Lower Result Set
        Cursor cursor = getReadableDatabase().rawQuery("select *" + " from " + scheme.TABLE_CLIPS + " where " + scheme.COL_CONTENT + "=?", new String[]{clipContent.toLowerCase()});
        return cursor.moveToFirst();
    }

    public ArrayList<Clip> getClips() {
        ArrayList<Clip> clips = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("select * from " + scheme.TABLE_CLIPS, null);
        while (cursor.moveToNext()) {
            clips.add(new Clip(cursor.getString(cursor.getColumnIndex(scheme.COL_CONTENT)), cursor.getString(cursor.getColumnIndex(scheme.COL_DATE))));
        }

        Collections.reverse(clips);
        return clips;
    }


    public boolean removeClip(Clip clip) {
        if (findClip(clip)) {
            getWritableDatabase().delete(scheme.TABLE_CLIPS, scheme.QUERY_FUNC_LCASE + scheme.COL_CONTENT + ")", new String[]{clip.getContent().toLowerCase()});
            return true;
        }

        return false;

    }

    public class scheme {
        public static final String DB_NAME = "clipDB";
        public static final int DB_VER = 1;

        public static final String TABLE_CLIPS = "clips";

        public static final String COL_ID = "_id";
        public static final String COL_CONTENT = "clip_content";
        public static final String COL_DATE = "clip_date";

        public static final String QUERY_CREATE = "create table "
                + TABLE_CLIPS
                + " ("
                + COL_ID + " integer primary key AUTOINCREMENT,"
                + COL_CONTENT + " text,"
                + COL_DATE + " text"
                + ")";

        public static final String QUERY_FUNC_LCASE = "lower`(";

    }
}
