package com.example.gulu;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class QHistoryAdapter extends RecyclerView.Adapter<QHistoryAdapter.ViewHolder> {

    private QLibraryActivity context;
    private List<QHistoryItem> historyItemList;

    public QHistoryAdapter(QLibraryActivity context, List<QHistoryItem> historyItemList){
        this.context = context;
        this.historyItemList = historyItemList;
    }

    @NonNull
    @Override
    public QHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QHistoryAdapter.ViewHolder holder, int position) {
        QHistoryItem item = historyItemList.get(position);

        //transform byte[] bitmap
        byte[] image = item.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        holder.historyImageView.setImageBitmap(bitmap);
        holder.historyTextView.setText(item.getString());
        holder.historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //QLibraryActivity.database.QueryData("DELETE FROM History WHERE Id = '"+ item.getId() +"'");
                context.DialogDelete(item.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyItemList.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{

        TextView historyTextView;
        Button historyBtn;
        ImageView historyImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            historyTextView = itemView.findViewById(R.id.historyTextView);
            historyBtn = itemView.findViewById(R.id.historyBtn);
            historyImageView = itemView.findViewById(R.id.historyImageView);
        }
    }
    private void removeItem(int position) {
        historyItemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,historyItemList.size());
    }
}
