package com.example.gulu;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class QHistoryAdapter extends RecyclerView.Adapter<QHistoryAdapter.ViewHolder> {

    private QLibraryActivity context;
    private List<QHistoryItem> historyItemList;
    private View view;

    private MediaPlayer clickSound;

    public QHistoryAdapter(QLibraryActivity context, List<QHistoryItem> historyItemList){
        this.context = context;
        this.historyItemList = historyItemList;
    }

    @NonNull
    @Override
    public QHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QHistoryAdapter.ViewHolder holder, int position) {
        QHistoryItem item = historyItemList.get(position);

        clickSound = MediaPlayer.create(context, R.raw.button_click);

        //transform byte[] bitmap
        byte[] image = item.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        holder.historyImageView.setImageBitmap(bitmap);
        loadDecodedImage(holder.historyBtn, R.id.historyBtn, R.drawable.delete, 30, 30);
        holder.historyTextView.setText(item.getString());
        holder.historyTextView.setMovementMethod(new ScrollingMovementMethod());
        holder.historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSound.start();
                context.DialogDelete(item.getId());
            }
        });

        //convert bitmap to uri
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);

        holder.historyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTranslate = new Intent(context, TranslateActivity.class);
                intentTranslate.putExtra("text", item.getString());
                intentTranslate.putExtra("image", Uri.parse(path).toString());
                context.startActivity(intentTranslate);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyItemList.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{

        TextView historyTextView;
        ImageView historyBtn;
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

    private void loadDecodedImage(@NonNull View view, int imageViewId, int imageId, int width, int height) {
        ImageView imageView = view.findViewById(imageViewId);
        imageView.setImageBitmap(decodeSampleBitmapFromResource(view.getResources(), imageId, width, height));
    }

    private Bitmap decodeSampleBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
