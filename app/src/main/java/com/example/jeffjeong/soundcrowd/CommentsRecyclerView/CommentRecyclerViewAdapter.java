package com.example.jeffjeong.soundcrowd.CommentsRecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeffjeong.soundcrowd.CommentsRecyclerView.CommentsActivity;
import com.example.jeffjeong.soundcrowd.ProfileActivity;
import com.example.jeffjeong.soundcrowd.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {
    private OnItemClickListener mListener;
    private static final String TAG = "CommentRecyclerViewAdapter";

    //vars
//    private ArrayList<String> mUserNames = CommentsActivity.userNames;
//    private ArrayList<String> mComments = CommentsActivity.comments;
//    private ArrayList<String> mCurrentPlayTimes = CommentsActivity.currentPlayTimes;
//    private ArrayList<String> mCommentCurrentTime = CommentsActivity.commentTime;
//    private ArrayList<String> mUserProfilePicPath = CommentsActivity.userProfilePicPath;
    private Context mContext;
//    private String UserName;


    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public CommentRecyclerViewAdapter(ArrayList<String> userNames, ArrayList<String> comments, ArrayList<String> currentPlayTimes, ArrayList<Long> commentCurrentTime, ArrayList<String> userProfilePicPath, Context context) {

        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        CircleImageView image;
        TextView name;
        TextView comment;
        TextView currentPlayTime;
        TextView commentCurrentTime;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.user_profile_pic);
            name = itemView.findViewById(R.id.comment_user_id);
            comment = itemView.findViewById(R.id.comment_text);
            currentPlayTime = itemView.findViewById(R.id.comment_current_playtime_text);
            commentCurrentTime = itemView.findViewById(R.id.comment_current_time_text);
            cardView = itemView.findViewById(R.id.commentCardView);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
//        String isUserName = CommentsActivity.userNames.get(position);
        View view;
//        if(!isUserName.equals("정의정")) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
//        } else {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_user, parent, false);
//        }

//        CommentsActivity.nominatedIndex = position;


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        CommentsActivity.nominatedIndex = position;
        Glide.with(mContext)
                .asBitmap()
                .load(CommentsActivity.userProfilePicPath.get(position))
                .into(holder.image);

        holder.name.setText(CommentsActivity.userNames.get(position));
        holder.comment.setText(CommentsActivity.comments.get(position));
        holder.currentPlayTime.setText("감상시점: "+CommentsActivity.currentPlayTimes.get(position)+" / ");
        holder.commentCurrentTime.setText("작성시간: "+calculateComementTime(CommentsActivity.commentTime.get(position)));

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, CommentsActivity.userNames.get(position) + "님의 프로필페이지로 넘어갑니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                intent.putExtra("profileName", CommentsActivity.userNames.get(position));
                mContext.startActivity(intent);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, mComments.get(position), Toast.LENGTH_SHORT).show();
                CommentsActivity.textUserTyped.setText("@" + CommentsActivity.userNames.get(position));
//                CommentsActivity.nominatedIndex = position;
            }


        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
//                Toast.makeText(v.getContext(), "롱 클릭을 시전하였다.", Toast.LENGTH_SHORT).show();

                final CharSequence[] options = {"수정", "삭제"};
                //show options to user
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("선택해주세요")
                        .setCancelable(true)
                        .setItems(options, new DialogInterface.OnClickListener() {
                            //수정 버튼 클릭시 발동
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    //수정하기 메소드
//                                    Toast.makeText(v.getContext(), "롱 클릭을 시전하였다.",Toast.LENGTH_SHORT).show();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    View view = LayoutInflater.from(mContext)
                                            .inflate(R.layout.edit_box, null, false);
                                    builder.setView(view);
                                    final Button ButtonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
                                    final EditText editComment = (EditText) view.findViewById(R.id.edittext_dialog_id);

                                    editComment.setText(CommentsActivity.userNames.get(holder.getAdapterPosition()));

                                    final AlertDialog dialog1 = builder.create();
                                    ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String editedComment = editComment.getText().toString();
                                            CommentsActivity.comments.set(holder.getAdapterPosition(), editedComment);
                                            notifyItemChanged(holder.getAdapterPosition());
                                            dialog1.dismiss();

                                        }
                                    });
                                    dialog1.show();


                                } else if (which == 1) {
                                    //삭제하기 메소드
//                                    Toast.makeText(v.getContext(), "롱 클릭을 시전하였다.",Toast.LENGTH_SHORT).show();
                                    CommentsActivity.userNames.remove(holder.getAdapterPosition());
                                    CommentsActivity.comments.remove(holder.getAdapterPosition());
                                    CommentsActivity.currentPlayTimes.remove(holder.getAdapterPosition());
                                    CommentsActivity.commentTime.remove(holder.getAdapterPosition());
                                    CommentsActivity.userProfilePicPath.remove(holder.getAdapterPosition());
                                    notifyItemRemoved(holder.getAdapterPosition());
                                    notifyItemRangeChanged(holder.getAdapterPosition(), CommentsActivity.userNames.size());
                                    notifyDataSetChanged();

                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();


                return true;
            }


        });

    }


    private String calculateComementTime(long writtenTime) {
        int second = 60;
        int minute = 60;
        int hour = 24;
        int day = 30;
        int month = 12;
        //now - writtenTime
        long now = System.currentTimeMillis();
        long timeCalculated = (now - writtenTime) / 1000;
//        Date date = new Date(timeCalculated);
        String calculatedTIme = "";
        if (timeCalculated < second) {
            if (timeCalculated < 1) {
                calculatedTIme = "방금 전";//방금전
            } else {
                calculatedTIme = timeCalculated + "초 전";//방금전
            }
        } else if ((timeCalculated /= second) < minute) {
            calculatedTIme = timeCalculated + "분 전";
        } else if ((timeCalculated /= minute) < hour) {
            calculatedTIme = timeCalculated + "시간 전";
        } else if ((timeCalculated /= hour) < day) {
            calculatedTIme = timeCalculated + "일 전";
        } else if ((timeCalculated /= day) < month) {
            calculatedTIme = timeCalculated + "달 전";
        } else {
            calculatedTIme = timeCalculated + "년 전";
        }
        return calculatedTIme;
    }


    @Override
    public int getItemCount() {
        return CommentsActivity.userNames.size();
    }


}
