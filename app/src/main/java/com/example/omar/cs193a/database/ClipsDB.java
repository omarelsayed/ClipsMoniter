package com.example.omar.cs193a.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.example.omar.cs193a.model.Clip;

import java.util.ArrayList;
import java.util.Collections;

public class ClipsDB extends SQLiteOpenHelper {

    public ClipsDB(@Nullable Context context) {
        super(context, ClipsDBScheme.DB_NAME, null, ClipsDBScheme.DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ClipsDBScheme.QUERY_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addClip(Clip clip) {
        if (!findClip(clip)) {
            ContentValues values = new ContentValues();
            values.put(ClipsDBScheme.COL_CONTENT, clip.getContent());
            values.put(ClipsDBScheme.COL_DATE, clip.getDate());
            getWritableDatabase().insert(ClipsDBScheme.TABLE_CLIPS, null, values);
            return true;
        }

        return false;

    }

    public boolean findClip(Clip clip) {
        String clipContent = clip.getContent();
        // Lower Result Set
        //Cursor cursor = getReadableDatabase().rawQuery("select *" + " from " + ClipsDBScheme.TABLE_CLIPS + " where lower(" + ClipsDBScheme.COL_CONTENT + ")=?", new String[]{clipContent.toLowerCase()});

        // Don't Lower Result Set
        Cursor cursor = getReadableDatabase().rawQuery("select *" + " from " + ClipsDBScheme.TABLE_CLIPS + " where " + ClipsDBScheme.COL_CONTENT + "=?", new String[]{clipContent});
        return cursor.moveToFirst();
    }

    public ArrayList<Clip> getClips() {
        ArrayList<Clip> clips = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("select * from " + ClipsDBScheme.TABLE_CLIPS, null);
        while (cursor.moveToNext()) {
            clips.add(new Clip(cursor.getString(cursor.getColumnIndex(ClipsDBScheme.COL_CONTENT)), cursor.getString(cursor.getColumnIndex(ClipsDBScheme.COL_DATE))));
        }

        Collections.reverse(clips);
        return clips;
    }


    public boolean removeClip(Clip clip) {
        if (findClip(clip)) {
            getWritableDatabase().delete(ClipsDBScheme.TABLE_CLIPS, ClipsDBScheme.COL_CONTENT+"=?" ,new String[]{clip.getContent()});
            return true;
        }

        return false;

    }

}
