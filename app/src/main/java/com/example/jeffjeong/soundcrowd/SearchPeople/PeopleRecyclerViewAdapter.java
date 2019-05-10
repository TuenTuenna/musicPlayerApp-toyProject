package com.example.jeffjeong.soundcrowd.SearchPeople;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeffjeong.soundcrowd.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PeopleRecyclerViewAdapter extends RecyclerView.Adapter<PeopleRecyclerViewAdapter.ViewHolder> {

    private static final String TAG2 = "PeopleRecycler";

    //vars
    private ArrayList<String> mPersonNames = new ArrayList<>();
    private ArrayList<String> mPersonImageUrls = new ArrayList<>();
    private ArrayList<String> mPersonCountries = new ArrayList<>();
    private Context mPeopleContext;

    public PeopleRecyclerViewAdapter(Context context, ArrayList<String> names, ArrayList<String> imageUrls,ArrayList<String> countries){
        mPeopleContext = context;
        mPersonNames = names;
        mPersonImageUrls = imageUrls;
        mPersonCountries = countries;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_people_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Log.d(TAG2, "onBindViewHolder: called.");

        Glide.with(mPeopleContext)
                .asBitmap()
                .load(mPersonImageUrls.get(position))
                .into(holder.image);

        holder.name.setText(mPersonNames.get(position));

        holder.country.setText(mPersonCountries.get(position));

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG2, "onClick: clicked on an image: " + mPersonNames.get(position));
                Toast.makeText(mPeopleContext, mPersonNames.get(position), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mPersonNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView name;
        TextView country;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.peopleImage);
            name = itemView.findViewById(R.id.personName);
            country = itemView.findViewById(R.id.personCountry);
        }
    }
}
