package ru.glebproject.itschool.samsung.memory;

import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import org.w3c.dom.Text;

public class ArcadeActivity extends AppCompatActivity {
    private int pic[];
    ImageButton button1;
    ImageButton button2;
    int b1_id;
    int b2_id;
    private int buttons[];

    int count;

    static TextView win;
    static TextView games_arcade;
    static TextView time_arcade;

    private MyAsyncTask async;

    DBManager manager;

    String name;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arcade);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        pic = random(12);
        //print(pic, 12);
        button1 = null;
        button2 = null;
        buttons = buttons_checker_arcade();
        games_arcade = (TextView)this.findViewById(R.id.turns_value_arcade);
        time_arcade = (TextView)this.findViewById(R.id.time_value_arcade);
        win = (TextView) this.findViewById(R.id.win_arcade);
        async = new MyAsyncTask();
        async.execute();
        count = 0;
        name = getIntent().getStringExtra("Name");

        //------------------------------------База данных

        manager = DBManager.getInstance(this);



    }

    @Override
    protected void onPause() {
        super.onPause();
        restart_arcade(new ImageButton(this));
        async.cancel(true);
        async = new MyAsyncTask();
    }

    private int[] random(int n){
        int b[] = new int[n];
        for(int i = 0; i < n; i++){
            int r = (int)(Math.random() * (n / 2 + 1));
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
            if(a[i] == x) c++;
        }
        if(c == 2) b = true;
        return b;
    }

    private int[] buttons_checker_arcade(){
        int b[] = new int[12];

        b[0] = ((ImageButton)(this.findViewById(R.id.arcade_pic0))).getId();
        b[1] = ((ImageButton)(this.findViewById(R.id.arcade_pic1))).getId();
        b[2] = ((ImageButton)(this.findViewById(R.id.arcade_pic2))).getId();
        b[3] = ((ImageButton)(this.findViewById(R.id.arcade_pic3))).getId();
        b[4] = ((ImageButton)(this.findViewById(R.id.arcade_pic4))).getId();
        b[5] = ((ImageButton)(this.findViewById(R.id.arcade_pic5))).getId();
        b[6] = ((ImageButton)(this.findViewById(R.id.arcade_pic6))).getId();
        b[7] = ((ImageButton)(this.findViewById(R.id.arcade_pic7))).getId();
        b[8] = ((ImageButton)(this.findViewById(R.id.arcade_pic8))).getId();
        b[9] = ((ImageButton)(this.findViewById(R.id.arcade_pic9))).getId();
        b[10] = ((ImageButton)(this.findViewById(R.id.arcade_pic10))).getId();
        b[11] = ((ImageButton)(this.findViewById(R.id.arcade_pic11))).getId();

        return b;
    }

    public void onClick_arcade(View v) {
        if (button1 == null) {
            button1 = (ImageButton) v;
            int id1 = button1.getId();
            b1_id = searchButtons(id1);
            //Toast
            if(b1_id != -1) openButton(button1, pic[b1_id]);
                else Toast.makeText(ArcadeActivity.this, "Can't find button", Toast.LENGTH_LONG);
        } else {
            button2 = (ImageButton) v;
            int id2 = button2.getId();
            b2_id = searchButtons(id2);
            //Toast
            if(b2_id != -1) openButton(button2, pic[b2_id]);
                else Toast.makeText(ArcadeActivity.this, "Can't find button", Toast.LENGTH_LONG);

            if (pic[b1_id] == pic[b2_id]) {
                //Toast.makeText(ArcadeActivity.this, "Right", Toast.LENGTH_SHORT).show();

                button1.setClickable(false);
                button2.setClickable(false);

                button1 = null;
                button2 = null;

                pic[b1_id] = -1;
                pic[b2_id] = -1;

                //print(pic, 12);
            } else {
                //Toast.makeText(ArcadeActivity.this, "Wrong", Toast.LENGTH_SHORT).show();

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

            if (win()) {
                count++;
                games_arcade.setText("" + count);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        newField();
                    }
                }, 200);


            }

        }
    }

    private void newField(){
        pic = random(12);
        button1 = null;
        button2 = null;
        buttons = buttons_checker_arcade();
        for(int i = 0; i < 12; i++){
            ImageButton button = (ImageButton)this.findViewById(buttons[i]);
            button.setImageResource(R.drawable.oblozhka);
            button.setClickable(true);
        }
        TextView win = (TextView)this.findViewById(R.id.win_arcade);
        win.setText("");
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
        switch(n){

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

    private int searchButtons(int id){
        for(int i = 0; i < 12; i++){
            if(buttons[i] == id){
                return i;
            }
        }
        return -1;
    }

    private boolean win(){
        for(int i = 0; i < 12; i++){
            if(pic[i] != -1){
                return false;
            }
        }
        return true;
    }

    public void restart_arcade(View v){

        pic = random(12);
        //print(pic, 12);
        button1 = null;
        button2 = null;
        buttons = buttons_checker_arcade();
        for(int i = 0; i < 12; i++){
            ImageButton button = (ImageButton)this.findViewById(buttons[i]);
            button.setImageResource(R.drawable.oblozhka);
            button.setClickable(true);
        }
        count = 0;
        games_arcade.setText("" + count);
        TextView win = (TextView)this.findViewById(R.id.win_arcade);
        win.setText("");

        async.cancel(true);
        async = new MyAsyncTask();
        async.execute();
    }

    private class MyAsyncTask extends AsyncTask<Void, Integer, Void>{

        @Override
        protected void onPreExecute() {
            time_arcade.setText("02:00");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //закончить игру
            Toast.makeText(ArcadeActivity.this, "Game Over", Toast.LENGTH_LONG).show();
            setButtonsNotClickable();
            //Save results
            manager.addArcadeResult(name, count);
            super.onPostExecute(aVoid);
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int minutes = values[0] / 60;
            int seconds = values[0] % 60;
            String m;
            String s;
            String time;
            m = "0" + minutes;
            if(seconds < 10) s = "0" + seconds;
            else s = "" + seconds;
            time = m + ":" + s;
            time_arcade.setText(time);
        }

        @Override
        protected Void doInBackground(Void... params) {
            int time = 120;

            while(time > 0){
                if(isCancelled()){
                    return null;
                }
                time--;
                publishProgress(time);
                SystemClock.sleep(1000);
            }
            return null;
        }
    }
}

















