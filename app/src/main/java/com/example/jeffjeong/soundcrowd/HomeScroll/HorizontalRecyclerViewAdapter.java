package com.example.jeffjeong.soundcrowd.HomeScroll;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeffjeong.soundcrowd.R;

import java.util.ArrayList;

public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    //vars
    private ArrayList<String> mTitle = new ArrayList<>();
    private ArrayList<String> mAlbumImageUrls = new ArrayList<>();
    private Context mContext;

    public HorizontalRecyclerViewAdapter(ArrayList<String> mTitle, ArrayList<String> mAlbumImageUrls, Context context) {
        this.mTitle = mTitle;
        this.mAlbumImageUrls = mAlbumImageUrls;
        this.mContext = context;
    }

    @NonNull
    @Override
    public HorizontalRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_scroll_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        Glide.with(mContext)
                .asBitmap()
                .load(mAlbumImageUrls.get(position))
                .into(holder.albumImage);

        holder.title.setText(mTitle.get(position));


        holder.albumImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on an image: " + mTitle.get(position));
                Toast.makeText(mContext, mTitle.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount(){
        return mAlbumImageUrls.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView albumImage;
        TextView title;

        public ViewHolder(View itemView){
            super(itemView);
            albumImage = itemView.findViewById(R.id.itemImage);
            title = itemView.findViewById(R.id.itemTitle);
        }

    }
}
