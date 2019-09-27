package ru.glebproject.itschool.samsung.memory;

import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class ClassicalActivity extends AppCompatActivity {
    static int pic[];
    ImageButton button1;
    ImageButton button2;
    int b1_id;
    int b2_id;
    private int buttons[];
    int turn = 0;

    static String name;


    static String timeValue;
    static TextView turns;
    static TextView time;
    static TextView win;

    static int finalTime = -1;

    static DBManager manager;


    private MyAsyncTask async;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classical);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        pic = random(12);
        //print(pic, 12);
        button1 = null;
        button2 = null;
        buttons = buttons_checker();
        turns = (TextView)this.findViewById(R.id.turns_value);
        time = (TextView)this.findViewById(R.id.time_value);
        win = (TextView)this.findViewById(R.id.win);
        name = getIntent().getStringExtra("Name");
        async = new MyAsyncTask();
        async.execute();

        //----------------------------------------------База данных

        manager = DBManager.getInstance(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        restart(new ImageButton(this));
        async.cancel(true);
        async = new MyAsyncTask();
    }

    public void restart(View v){
        Log.d("Async", "in restart");
        pic = random(12);
        Log.d("Async", "pic was done");
        //print(pic, 12);
        button1 = null;
        button2 = null;
        Log.d("Async", "buttons were setted to null");
        buttons = buttons_checker();
        Log.d("Async", "byttons were checked");
        for(int i = 0; i < 12; i++){
            ImageButton button = (ImageButton)this.findViewById(buttons[i]);
            button.setImageResource(R.drawable.oblozhka);
            button.setClickable(true);
        }
        Log.d("Async", "buttons were prepared");
        turn = 0;
        turns.setText("" + turn);
        Log.d("Async", "turn = " + turn);
        win.setText("");

        async.cancel(true);
        async = new MyAsyncTask();
        async.execute();
        Log.d("Async", "async was executed");
    }

    public void onClick(View v) {
        if(button1 == null){
            button1 = (ImageButton) v;
            button1.setClickable(false);
            int id1 = button1.getId();
            b1_id = searchButtons(id1);
            //Toast.makeText(ClassicalActivity.this, "" + pic[b1_id], Toast.LENGTH_SHORT).show();
            if(b1_id != -1) openButton(button1, pic[b1_id]);
                else Toast.makeText(ClassicalActivity.this, "Can't find button", Toast.LENGTH_LONG);
        } else {
            button2 = (ImageButton) v;
            int id2 = button2.getId();
            b2_id = searchButtons(id2);
            //Toast.makeText(ClassicalActivity.this, "" + pic[b2_id], Toast.LENGTH_SHORT).show();
            if(b2_id != -1) openButton(button2, pic[b2_id]);
                else Toast.makeText(ClassicalActivity.this, "Can't find button", Toast.LENGTH_LONG);

            if(pic[b1_id] == pic[b2_id]){
                //Toast.makeText(ClassicalActivity.this, "Right", Toast.LENGTH_SHORT).show();

                button1.setClickable(false);
                button2.setClickable(false);

                button1 = null;
                button2 = null;

                pic[b1_id] = -1;
                pic[b2_id] = -1;

                //print(pic, 12);
            } else {
                //Toast.makeText(ClassicalActivity.this, "Wrong", Toast.LENGTH_SHORT).show();

                setButtonsNotClickable();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closeButton(button1);
                        closeButton(button2);
                        button1 = null;
                        button2 = null;
                        setButtonsClickable();
                    }
                }, 1000);

            }

            turn++;
            turns.setText("" + turn);
            if(win()){
                //Toast.makeText(ClassicalActivity.this, "You Win!", Toast.LENGTH_SHORT).show();
                TextView t = (TextView)this.findViewById(R.id.win);
                t.setText("You Win!");
                        int timeV = -1;
                        try {
                            timeV = async.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        if (timeV != -1) finalTime = timeV;

                //----------------------------------------------------------Добавить значение

                manager.addClassicalResult(name, finalTime);

            }
        }
    }

    private void setButtonsNotClickable(){
        for(int i = 0; i < 12; i++){
            ImageButton button = (ImageButton)this.findViewById(buttons[i]);
            button.setClickable(false);
        }
    }

    private void setButtonsClickable(){
        for(int i = 0; i < 12; i++){
            ImageButton button = (ImageButton)this.findViewById(buttons[i]);
            int c = searchButtons(buttons[i]);
            if(c != -1 && pic[c] != -1){
                button.setClickable(true);
            }
        }
    }

    private void openButton(ImageButton button, int n){
        switch (n){

            case 1: button.setImageResource(R.drawable.first);
                    return;
            case 2: button.setImageResource(R.drawable.second);
                    return;
            case 3: button.setImageResource(R.drawable.third);
                    return;
            case 4: button.setImageResource(R.drawable.fourth);
                    return;
            case 5: button.setImageResource(R.drawable.fifth);
                     return;
            case 6: button.setImageResource(R.drawable.sixth);
                     return;
            case 7: button.setImageResource(R.drawable.seventh);
                    return;
            case 8: button.setImageResource(R.drawable.eighth);
                    return;
            case 9: button.setImageResource(R.drawable.nineth);
                    return;

        }
    }

    private void closeButton(ImageButton button){
        button.setImageResource(R.drawable.oblozhka);
    }

    static boolean win(){


        for(int i = 0; i < 12; i++){
            if(pic[i] != -1){
                return false;
            }
        }

        return true;

    }

    private int searchButtons(int id){
        for(int i = 0; i < 12; i++){
            if(buttons[i] == id){
                return i;
            }
        }
        return -1;
    }

    private int[] buttons_checker(){
        int b[] = new int[12];

        b[0] = ((ImageButton)(this.findViewById(R.id.classical_pic0))).getId();
        b[1] = ((ImageButton)(this.findViewById(R.id.classical_pic1))).getId();
        b[2] = ((ImageButton)(this.findViewById(R.id.classical_pic2))).getId();
        b[3] = ((ImageButton)(this.findViewById(R.id.classical_pic3))).getId();
        b[4] = ((ImageButton)(this.findViewById(R.id.classical_pic4))).getId();
        b[5] = ((ImageButton)(this.findViewById(R.id.classical_pic5))).getId();
        b[6] = ((ImageButton)(this.findViewById(R.id.classical_pic6))).getId();
        b[7] = ((ImageButton)(this.findViewById(R.id.classical_pic7))).getId();
        b[8] = ((ImageButton)(this.findViewById(R.id.classical_pic8))).getId();
        b[9] = ((ImageButton)(this.findViewById(R.id.classical_pic9))).getId();
        b[10] = ((ImageButton)(this.findViewById(R.id.classical_pic10))).getId();
        b[11] = ((ImageButton)(this.findViewById(R.id.classical_pic11))).getId();

        return b;
    }

    private int[] random(int n){
        int b[] = new int[n];
        for(int i = 0; i < n; i++){
            int r = (int) (Math.random() * (n / 2 + 1));
            if(search(b, r, n) || r == 0){
                i--;
            } else {
                b[i] = r;
            }
        }
        return b;
    }



    private boolean search(int a[], int x, int n){
        boolean b = false;
        int c = 0;
        for(int i = 0; i < n; i++){
            if(a[i] == x)c++;
        }
        if(c == 2)b = true;
        return b;
    }

    private void print(int b[], int n){
        TextView t = (TextView) this.findViewById(R.id.win);
        String s = "";
        for(int i = 0; i < n; i++){
            s = s + (b[i] + " ");
        }
        t.setText(s);
    }


    class MyAsyncTask extends AsyncTask<Void, Integer, Integer>{

        @Override
        protected void onCancelled(Integer integer) {
            Log.d("Async", "in Cancelled");
            super.onCancelled(integer);
            //ClassicalActivity.manager.addClassicalResult(ClassicalActivity.name, integer);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Log.d("Async", "in Post");
            super.onPostExecute(integer);
            //ClassicalActivity.manager.addClassicalResult(ClassicalActivity.name, integer);
        }

        @Override
        protected void onPreExecute() {
            Log.d("Async", "in Pre");
            super.onPreExecute();
            time.setText("00:00");

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d("Async", "in UPD");
            int minutes = values[0] / 60;
            int seconds = values[0] % 60;

            Log.d("Async", "minutes = " + minutes + ", seconds = " + seconds);

            String m;
            String s;

            if(minutes < 10) m = "0" + minutes;
            else m = "" + minutes;

            if(seconds < 10) s = "0" + seconds;
            else s = "" + seconds;

            time.setText(m + ":" + s);

            Log.d("Async", "text was setted");
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Log.d("Async", "in doInBackground");
            int t = 0;
            Log.d("Async", "t = " + t);
            while(!win()){
                Log.d("Async", "in While");
                if(isCancelled()){
                    Log.d("Async", "Cancelled");
                    return t;
                }
                t++;
                Log.d("Async", "while, t = " + t);
                publishProgress(t);
                Log.d("Async", "published");
                SystemClock.sleep(1000);
                Log.d("Async", "after Sleep");
            }
            Log.d("Async", "after While");
            return t;
        }
    }

}



