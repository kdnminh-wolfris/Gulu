package com.example.gulu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class QLibraryActivity extends AppCompatActivity {

    private ArrayList<QHistoryItem> historyItems;
    private RecyclerView mRecyclerView;
    private QHistoryAdapter mAdapter;
    private Button deleteAllBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_fragment);

        deleteAllBtn = findViewById(R.id.deleteAllBtn);
        deleteAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.database.QueryData("DELETE FROM History");
                Toast.makeText(QLibraryActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                UpdateData();
            }
        });

        mRecyclerView = findViewById(R.id.recyclerView);
        historyItems = new ArrayList<>();

        mAdapter = new QHistoryAdapter(this, historyItems);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        //Get Data
        UpdateData();
    }

    public void UpdateData(){
        Cursor cursor = MainActivity.database.GetData("SELECT * FROM History");
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
                MainActivity.database.QueryData("DELETE FROM History WHERE Id = '"+ id +"' ");
                Toast.makeText(QLibraryActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                UpdateData();
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