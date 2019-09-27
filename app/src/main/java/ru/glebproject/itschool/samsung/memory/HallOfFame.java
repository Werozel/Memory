package ru.glebproject.itschool.samsung.memory;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.logging.LogRecord;

public class HallOfFame extends AppCompatActivity {

    private static DBManager manager;

    TextView classicalTV;
    TextView arcadeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall_of_fame);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        classicalTV = (TextView) this.findViewById(R.id.classical);
        arcadeTV = (TextView) this.findViewById(R.id.arcade);

        classicalTV.setText("");
        arcadeTV.setText("");

        manager = DBManager.getInstance(this);

        publish();
    }

    void publish(){
        Log.d("Delete", "in publish");

        ArrayList<ResultClassical> resultClassical = manager.getClassicalResults();
        ArrayList<ResultArcade> resultArcade = manager.getArcadeResults();

        Log.d("Delete", "got results");

        String classicalStr = "";
        String arcadeSTR = "";



        for (ResultClassical res : resultClassical) {
            Log.d("Delete", "in classical for");
            int minutes, seconds;
            String minStr = "", secStr = "";

            minutes = res.time / 60;
            seconds = res.time % 60;

            minStr += minutes;
            if(seconds < 10){
                secStr += "0" + seconds;
            } else {
                secStr += seconds;
            }

            Log.d("Delete", "got minutes and seconds");

            if(seconds != -1) {
                classicalStr += res.name + "   -   " + minStr + ":" + secStr + "\n";
            }

            Log.d("Delete", "classical text was created");
        }
        for (ResultArcade res : resultArcade) {
            Log.d("Delete", "in arcade for");
            arcadeSTR += res.name + "   -   " + res.games + "\n";
            Log.d("Delete", "arcade text was created");
        }

        classicalTV.setText(classicalStr);
        arcadeTV.setText(arcadeSTR);

        Log.d("Delete", "text was setted");

    }

    public void onClickDelete(View v){

        Log.d("Delete", "in OnClickDelete");
        final ImageButton button = (ImageButton) v;
        Log.d("Delete", "got the button");
        button.setClickable(false);
        button.setImageResource(R.drawable.delete_pressed);
        Log.d("Delete", "pressed = " + button.isClickable());

        manager.deleteAllResults();
        Log.d("Delete", "Results Must Be Deleted");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setImageResource(R.drawable.delete);
                button.setClickable(true);
            }
        }, 70);

        Log.d("Delete", "pressed = " + button.isClickable());

        publish();

        Log.d("Delete", "published");
    }
}
