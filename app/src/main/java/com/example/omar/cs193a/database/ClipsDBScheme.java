package com.example.omar.cs193a.database;

public class ClipsDBScheme {
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
