package ru.glebproject.itschool.samsung.memory;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class DBManager {
    /*Хочу хранить две таблицы для Classical и Arcade режимов*/
    private Context context;

    static private SQLiteDatabase db;

    private static DBManager dbManager;

    public static DBManager getInstance(Context context) {
        if (dbManager == null){
            dbManager = new DBManager(context);
        }
        return dbManager;
    }

    private DBManager(Context context){
        this.context = context;
        db = context.openOrCreateDatabase("DATABASE", Context.MODE_PRIVATE, null);
        createTableIfNeedBe();
    }

    private void createTableIfNeedBe() {    //Для двух таблиц
        Log.d("Delete", "in create");
            db.execSQL("CREATE TABLE IF NOT EXISTS RESULTCLASSICAL (NAME TEXT, TIME INTEGER);");
        Log.d("Delete", "first table created");
            db.execSQL("CREATE TABLE IF NOT EXISTS RESULTARCADE (NAME TEXT, GAMES INTEGER);");
        Log.d("Delete", "second created");

    }

    ArrayList<ResultClassical> getClassicalResults() {  //Хочу получить результаты в отсортированном виде

        ArrayList<ResultClassical> data = new ArrayList<ResultClassical>();
        Cursor cursor = db.rawQuery("SELECT * FROM RESULTCLASSICAL ORDER BY TIME;", null);
        boolean hasMoreData = cursor.moveToFirst();

        while (hasMoreData) {
            String name = cursor.getString(cursor.getColumnIndex("NAME"));
            int time = Integer.parseInt(cursor.getString(cursor.getColumnIndex("TIME")));
            data.add(new ResultClassical(name, time));
            hasMoreData = cursor.moveToNext();
        }

        return data;
    }

    ArrayList<ResultArcade> getArcadeResults() {    //то же самое только для Arcade режима

        ArrayList<ResultArcade> data = new ArrayList<ResultArcade>();
        Cursor cursor = db.rawQuery("SELECT * FROM RESULTARCADE ORDER BY GAMES DESC;", null);
        boolean hasMoreData = cursor.moveToFirst();

        while (hasMoreData) {
            String name = cursor.getString(cursor.getColumnIndex("NAME"));
            int games = Integer.parseInt(cursor.getString(cursor.getColumnIndex("GAMES")));
            data.add(new ResultArcade(name, games));
            hasMoreData = cursor.moveToNext();
        }

        return data;
    }

    void addClassicalResult(String name, int turns) {    //Проверю кол-во элементов и если меньше 10 то просто вставлю новый, а если равно 10 то заменю один с худшим значением на новый
        /*Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM RESULTCLASSICAL", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);*/
        db.execSQL("INSERT INTO RESULTCLASSICAL VALUES ('" + name + "', " + turns + ");");

    }

    void addArcadeResult(String name, int games){   // то же самое для другого режима
        /*Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM RESULTARCADE", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);*/
            db.execSQL("INSERT INTO RESULTARCADE VALUES ('" + name + "', " + games + ");");
    }

    void deleteAllResults(){

        db.delete("RESULTCLASSICAL", null, null);
        db.delete("RESULTARCADE", null, null);
        createTableIfNeedBe();

    }

}
