package com.example.jeffjeong.soundcrowd.recyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.jeffjeong.soundcrowd.ProfileActivity;
import com.example.jeffjeong.soundcrowd.R;

import java.util.ArrayList;


public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicItemViewHolder> {
    private ArrayList<MusicItem> musicItemArrayList;
    private OnItemClickListener mListener;
    private Context mContext;

    public interface OnItemClickListener {
        void onItemClick(int position, TextView textView);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public static class MusicItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mSingerNameView;
        public TextView mMusicTitleView;
        public TextView mIsPlayingView;
        public TextView mMusicDuration;
        public ImageView mMoreImage;
        public TextView mListeningCountView;
        public TextView mLikesCountView;
        public ToggleButton mToggleButton;

        public ImageView musicArt;
        public TextView singerName;
        public TextView musicTitle;


        public MusicItemViewHolder(@NonNull final View itemView, final OnItemClickListener listener) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.AlbumJacket);
            mSingerNameView = itemView.findViewById(R.id.SingerName);
            mMusicTitleView = itemView.findViewById(R.id.MusicTitle);
            mIsPlayingView = itemView.findViewById(R.id.IsPlaying);
            mMoreImage = itemView.findViewById(R.id.image_more);
            mMusicDuration = itemView.findViewById(R.id.music_duration);
            mListeningCountView = itemView.findViewById(R.id.listening_count);
            mLikesCountView = itemView.findViewById(R.id.like_textview);
            mToggleButton = itemView.findViewById(R.id.btn_like);

            musicArt = itemView.findViewById(R.id.img_albumart);
            singerName = itemView.findViewById(R.id.singerName);
            musicTitle = itemView.findViewById(R.id.txt_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();

//                        musicPlay(position);


                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position,mIsPlayingView);
                        }
                    }
                }
            });






//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                        if (listener != null){
//                        int position = getAdapterPosition();
//
//                        if(position != RecyclerView.NO_POSITION){
//                            listener.onItemClick(position);
//                        }
//                    }
//                }
//            });
//            mMoreImage.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    if (listener != null){
//                        int position = getAdapterPosition();
//                        if(position != RecyclerView.NO_POSITION){
//                            listener.onDeleteClick(position);
//                        }
//                    }
//                }
//            });
        }
    }






    public MusicAdapter(ArrayList<MusicItem> exampleList, Context context) {
        musicItemArrayList = exampleList;
        mContext = context;
    }

    @NonNull
    @Override
    public MusicItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false);
        MusicItemViewHolder evh = new MusicItemViewHolder(v, mListener);
        return evh;
    }

    //뷰를 재활용 시켜준다.
    @Override
    public void onBindViewHolder(@NonNull final MusicItemViewHolder holder, final int position) {
        MusicItem currentItem = musicItemArrayList.get(position);

        //글라이드 이미지 그리기
        Glide.with(mContext)
                .asBitmap()
                .load(musicItemArrayList.get(position).getImageResource())
                .into(holder.mImageView);
//        holder.mImageView.setImageURI(Uri.parse(currentItem.getImageResource()));
        holder.mMusicDuration.setText(currentItem.getmMusicDuration());
        holder.mSingerNameView.setText(currentItem.getSingerName());
        holder.mMusicTitleView.setText(currentItem.getMusicTitle());
        holder.mIsPlayingView.setText(currentItem.getIsPlaying());
        holder.mIsPlayingView.setTextColor(currentItem.getIsPlayingColor());
        holder.mListeningCountView.setText("재생횟수: " + currentItem.getListeningCount());
        holder.mLikesCountView.setText("좋아요: "+currentItem.getmLikes());


//        //크게만들 애니메이션
//        final Animation makeBigger = AnimationUtils.loadAnimation(mContext,R.anim.scale_anim_to_bigger);
//        //애니메이션이 끝나고 없앤다
//        makeBigger.setDuration(1000);
//
//        makeBigger.setFillAfter(false);
//
//        final Animation makeSmaller = AnimationUtils.loadAnimation(mContext,R.anim.scale_anim_to_smaller);
//        //애니메이션이 끝나고 없앤다
//        makeSmaller.setDuration(1000);
//        makeSmaller.setFillAfter(false);


        //페이드 인 아웃 애니메이션
        final Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000);
        fadeIn.setStartOffset(300);
        // End to 0.1f if you desire 90% fade animation
        final Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(1000);
        fadeOut.setStartOffset(500);

        //페이드인 애니메이션 리스너
        fadeIn.setAnimationListener(new Animation.AnimationListener(){
            //애니메이션이 끝날때
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start fadeOut when fadeIn ends (continue)

                holder.mIsPlayingView.startAnimation(fadeOut);

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {

            }
        });

        fadeOut.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start fadeIn when fadeOut ends (repeat)
                holder.mIsPlayingView.startAnimation(fadeIn);

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {

            }

        });

//        fadeIn.setRepeatCount(Animation.INFINITE);
//        fadeOut.setRepeatCount(Animation.INFINITE);


        holder.mIsPlayingView.startAnimation(fadeIn);
//        holder.mIsPlayingView.startAnimation(fadeOut);







        //더보기 버튼을 클릭하였을때
        holder.mMoreImage.setOnClickListener(new View.OnClickListener() {


            //클릭을 하였을 때 오버라이딩
            @Override
            public void onClick(final View v) {

                //보기 정하기
                final CharSequence[] options = {"내 재생목록에 추가", "공유하기", "상대방 프로필 페이지 방문하기"};
                //사용자에게 옵션 보여주기
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("선택해주세요");
                builder.setCancelable(true);
                builder.setItems(options, new DialogInterface.OnClickListener() { // 버튼클릭시 발동 - 인덱스로 설정함 which 0,1,2,3
                    //버튼 클릭시 발동
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //재생목록에 추가 버튼을 클릭했을때
                        if (which == 0) {

                        } //공유하기 버튼을 클릭했을때
                        else if (which == 1) {

                        } //상대방 프로필 페이지 방문하기를 클릭했을때
                        else if (which == 2) {
                            Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                            intent.putExtra("profileName",MusicPlayListActivity.musicItemArrayList.get(holder.getAdapterPosition()).getmUserId());
                            mContext.startActivity(intent);
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        //좋아요 버튼을 클릭했을때
        holder.mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String likeCheck;
                if(isChecked){
                    likeCheck = MusicPlayListActivity.musicItemArrayList.get(holder.getAdapterPosition()).mLikesUp();
                    Toast.makeText(mContext, "좋아요: "+likeCheck, Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                } else {
                    likeCheck = MusicPlayListActivity.musicItemArrayList.get(holder.getAdapterPosition()).mLiskesDown();
                    Toast.makeText(mContext, "좋아요: "+likeCheck, Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicItemArrayList.size();
    }

//    private void shareWithFriends(){
//        String mWebsite = "https://soundcloud.com/officialhyukoh";
//        String title = holder.mMusicTitleView.getText().toString();
//        String singer = holder.mSingerNameView.getText().toString();
//        String mimeType = "text/plain";
//        ShareCompat.IntentBuilder
//                .from(this)
//                .setType(mimeType)
//                .setChooserTitle("친구들과 이 음악을 공유하세요 - ")
//                .setText("가수: "+singer+"/ 음악타이틀: "+title+"  \n"+mWebsite)
//                .startChooser();
//    }

    public void removeItem(int position) {
        Log.d("TTT", "뮤직플레이리스트 액티비티 / removeItem() 지우기 전 뮤직아이템어레이리스트 사이즈 : "+musicItemArrayList.size());

        musicItemArrayList.remove(position);
        notifyItemRemoved(position);
        Log.d("TTT", "뮤직플레이리스트 액티비티 / removeItem() 지운 인덱스 : "+position);
        Log.d("TTT", "뮤직플레이리스트 액티비티 / removeItem() 지우기 후 뮤직아이템어레이리스트 사이즈 : "+musicItemArrayList.size());

    }

    public void restoreItem(MusicItem item, int position) {
        musicItemArrayList.add(position, item);
        notifyItemInserted(position);
    }

    public ArrayList<MusicItem> getData() {
        return musicItemArrayList;
    }


}
