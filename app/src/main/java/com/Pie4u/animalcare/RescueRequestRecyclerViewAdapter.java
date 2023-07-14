package com.Pie4u.animalcare;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RescueRequestRecyclerViewAdapter extends RecyclerView.Adapter<RescueRequestRecyclerViewAdapter.RescueViewHolder> {

    private ArrayList<AnimalHelpCase> rescueArrayList;
    private AnimalImageUtil animalImageUtil = new AnimalImageUtil();
    private FirebaseUser firebaseUser;

    public RescueRequestRecyclerViewAdapter(ArrayList<AnimalHelpCase> rescueArrayList){
        this.rescueArrayList = rescueArrayList;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    public OnItemClickListener itemClickListener;

    public interface  OnItemClickListener{
        void onItemClick(int position, View view, View itemView);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public RescueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_rescue_requests,parent,false);

        return new RescueViewHolder(view,itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RescueViewHolder holder, int position) {

        AnimalHelpCase currentRescueCard = rescueArrayList.get(position);
        String animalType = currentRescueCard.getAnimalType();
        int animalImageResourceId = animalImageUtil.getAnimalImageResourceId(animalType);
        if(animalImageResourceId!=0) {
            holder.imageViewAnimalType.setImageResource(animalImageResourceId);
        }
        holder.textViewAnimalLocationLandmark.setText(animalType +" needs your help");
        Log.d("aa","<<--------------");
        if(currentRescueCard.getRescuerUid()!=null && currentRescueCard.getRescuerUid().equals(firebaseUser.getUid())) {
            holder.textViewCurrentTask.setVisibility(View.VISIBLE);
            Log.d("isCompleted","-> "+currentRescueCard.isCompleted());
            if (currentRescueCard.isCompleted() ){
                holder.textViewCurrentTask.setText("You completed this rescue task");
                holder.textViewCurrentTask.setBackgroundColor(holder.itemView.getResources().getColor(R.color.colorPrimary));
            }else{
                holder.textViewCurrentTask.setText("Ongoing rescue task");
                holder.textViewCurrentTask.setBackgroundColor(holder.itemView.getResources().getColor(R.color.holoBlueDark));
            }
        }else{
            holder.textViewCurrentTask.setVisibility(View.GONE);
        }

        if(currentRescueCard.isAccepted()){
            Log.d("aa","here");
            holder.itemView.findViewById(R.id.linearLayoutRescueBtn).setVisibility(View.GONE);
        }else{
            Log.d("aa","ZZZZ");
            holder.itemView.findViewById(R.id.linearLayoutRescueBtn).setVisibility(View.VISIBLE);
        }

        holder.textViewRescueStatus.setText(currentRescueCard.getRescueStatus());
        if(currentRescueCard.getTimestamp()!=null) {
            Date date = currentRescueCard.getTimestamp().toDate();

            String time = new SimpleDateFormat("h:mm a").format(date);
            String rescueDate = new SimpleDateFormat("dd-MMM-yyyy").format(date);

            Log.d("DATETIME", time +" - - " + rescueDate);
            Log.d("ISACCE","-> "+currentRescueCard.isAccepted());
            Log.d("aa","--------------");
            holder.textViewRescueTime.setText(time);
            holder.textViewRescueDate.setText(rescueDate);

        }

    }


    public static class RescueViewHolder extends RecyclerView.ViewHolder{

        private TextView textViewAnimalLocationLandmark, textViewRescueStatus;
        private TextView textViewRescueTime, textViewRescueDate, textViewCurrentTask;
        private Button btnAcceptRescue;
        private ImageView imageViewAnimalType;

        public RescueViewHolder(@NonNull final View itemView, final OnItemClickListener itemClickListener) {
            super(itemView);

            textViewAnimalLocationLandmark = itemView.findViewById(R.id.textViewAnimalLocationLandmark);
            textViewRescueStatus = itemView.findViewById(R.id.textViewRescueStatus);
            btnAcceptRescue = itemView.findViewById(R.id.btnAcceptRescue);
            imageViewAnimalType = itemView.findViewById(R.id.imageViewAnimalType);
            textViewRescueTime = itemView.findViewById(R.id.textViewRescueTime);
            textViewRescueDate = itemView.findViewById(R.id.textViewRescueDate);
            textViewCurrentTask = itemView.findViewById(R.id.textViewCurrentTask);

            btnAcceptRescue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(itemClickListener!=null && position!=RecyclerView.NO_POSITION){
                        itemClickListener.onItemClick(position, btnAcceptRescue, itemView);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(itemClickListener!=null && position!=RecyclerView.NO_POSITION){
                        itemClickListener.onItemClick(position, itemView, itemView);
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return rescueArrayList.size();
    }
}
