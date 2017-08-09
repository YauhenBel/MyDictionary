package com.example.genia.mydictionary;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class words extends AppCompatActivity {
    EditText ed_eng, ed_rus;
    Dialog dialog;
    String s_eng, s_rus;
    String RELEASE = "release";
    String release;
    SharedPreferences sPref;
    LinearLayout l_layout;
    LinearLayout view;
    final int DIALOG = 1;
    DBHelper dbHelper;
    SQLiteDatabase db;
    ArrayList<text_list> words_tabl = new ArrayList<text_list>();
    words_adapter adapter;
    ListView lv_words;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);
        //dialog = new Dialog(this);
        sPref = getSharedPreferences("release", MODE_PRIVATE);
        release = sPref.getString(RELEASE, "");
        //Toast.makeText(words.this, "Version OC " + release, Toast.LENGTH_LONG).show();


        l_layout = (LinearLayout) findViewById(R.id.ll_out);

        set_up_list();




    }



    public void btn_eng(View view) {
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout_words, menu);
        return super.onCreateOptionsMenu(menu);

    }



    @Override
    protected Dialog onCreateDialog(int id) {

        if (id == DIALOG)
        {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            // создаем view из dialog.xml
            view = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.panel_add_words, null);
            // устанавливаем ее, как содержимое тела диалога
            adb.setView(view);

            ed_eng = (EditText) view.findViewById(R.id.ed_eng_word);
            ed_rus = (EditText) view.findViewById(R.id.ed_rus_word);
            dialog = adb.create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Log.i("dialog", "Show");
                }
            });

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    Log.i("dialog", "Dismiss");
                }
            });
            return  dialog;
        }
        return super.onCreateDialog(id);

    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.to_main:
                Intent intent = new Intent(this, Main2Activity.class);
                startActivity(intent);
                break;
            case R.id.add_words:
                showDialog(DIALOG);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void add_new_word(View view) {
        s_eng = ed_eng.getText().toString();
        s_rus = ed_rus.getText().toString();

        Log.i("Class_wods", "Check input words: Eng word: "
                + s_eng + "Rus word: " + s_rus);

        dbHelper = new DBHelper(this);
        //object dbHelper class

        //connect to db
        db = dbHelper.getWritableDatabase();

        Cursor c = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        Log.d("words", "Number of records in the database: " + c.getCount());

        Log.d("words", "Put new data in DB...");
                ContentValues cv = new ContentValues();
                //write in table new data

                cv.put(DBHelper.LANGUAGE, "Eng");
                cv.put(DBHelper.WORD, s_eng);
                cv.put(DBHelper.TRANSLATE, s_rus);
                db.insert(DBHelper.TABLE_NAME, null, cv);
                Log.d("words", "New data is was put in database.");

        set_up_list();
        Log.d("words", "Data  from DB ended");


        c.close();
        dbHelper.close();

        dialog.dismiss();


    }

    void set_up_list() {
        dbHelper = new DBHelper(this);
        //object dbHelper class

        //connect to db
        db = dbHelper.getWritableDatabase();

        Cursor c = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);

        if (c.getCount() != 0) {
            if (c.moveToFirst()) {
                int idIndex = c.getColumnIndex(DBHelper.KEY_ID);
                int langIndex = c.getColumnIndex(DBHelper.LANGUAGE);
                int wordIndex = c.getColumnIndex(DBHelper.WORD);
                int translateIndex = c.getColumnIndex(DBHelper.TRANSLATE);
                Log.d("words", "Data output from DB: ");
                words_tabl.clear();
                do {
                    words_tabl.add(new text_list(c.getString(wordIndex), c.getString(translateIndex)));
                    Log.d("words", "ID = " + c.getInt(idIndex) + ", Language = " + c.getString(langIndex)
                            + ", Word = " + c.getString(wordIndex) + ", Translate = " + c.getString(translateIndex));
                    Log.d("words", "____________________________________________________________________________________________");
                } while (c.moveToNext());

            }
            adapter = new words_adapter(this, words_tabl);
            Log.i("words", "Crate adapter");
            lv_words = (ListView) findViewById(R.id.word_list);
            lv_words.setAdapter(adapter);
            Log.i("t_table", "Set up a list");
            registerForContextMenu(lv_words);
        }
        else
        {
            Toast.makeText(words.this, "Список слов пуст", Toast.LENGTH_LONG).show();
        }

    }


}
