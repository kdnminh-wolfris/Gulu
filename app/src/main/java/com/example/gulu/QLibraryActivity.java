package com.example.gulu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class QLibraryActivity extends AppCompatActivity {

    public static QDatabase database;
    private ArrayList<QHistoryItem> historyItems;
    private RecyclerView mRecyclerView;
    private QHistoryAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_fragment);

        mRecyclerView = findViewById(R.id.recyclerView);
        historyItems = new ArrayList<>();

        mAdapter = new QHistoryAdapter(this, historyItems);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = new QDatabase(this, "QLibrary.sqlite", null, 1);
        database.QueryData("CREATE TABLE IF NOT EXISTS History(Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Content VARCHAR(5000), Image BLOB)");

        //Get Data
        Cursor cursor = database.GetData("SELECT * FROM History");
        historyItems.clear();
        while (cursor.moveToNext()){
            historyItems.add(new QHistoryItem(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getBlob(2)
            ));
        }
        mAdapter.notifyDataSetChanged();
    }

    public void DialogDelete(final int id){
        AlertDialog.Builder dialogDel = new AlertDialog.Builder(this);
        dialogDel.setMessage("Do you want to delete this translation?");

        dialogDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                database.QueryData("DELETE FROM History WHERE Id = '"+ id +"' ");
                Toast.makeText(QLibraryActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                Cursor cursor = database.GetData("SELECT * FROM History");
                historyItems.clear();
                while (cursor.moveToNext()){
                    historyItems.add(new QHistoryItem(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getBlob(2)
                    ));
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        dialogDel.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialogDel.show();
    }
}