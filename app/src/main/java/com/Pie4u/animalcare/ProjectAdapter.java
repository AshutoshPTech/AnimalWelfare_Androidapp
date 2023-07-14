package com.Pie4u.animalcare;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder>{

    ArrayList<ProductModel>list;
    Context context;

    public ProjectAdapter(ArrayList<ProductModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final ProductModel model=list.get(position);

        Picasso.get().load(model.getImgView()).placeholder(R.drawable.ic_launcher_background).into(holder.productImageView);

        holder.nameTextView.setText(model.getEd1());
        holder.descriptionTextView.setText(model.getEd2());
        holder.pprice.setText(model.getEd3());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, view_ads.class);
                i.putExtra("prodimg",model.getImgView());
                i.putExtra("prodname",model.getEd1());
                i.putExtra("proddesc",model.getEd2());
                i.putExtra("prodprice",model.getEd3());
                i.putExtra("permob",model.getEd4());
                i.setData(Uri.parse("permob"));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView productImageView;
        TextView nameTextView,descriptionTextView,pprice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImageView=itemView.findViewById(R.id.productImageView);
            nameTextView=itemView.findViewById(R.id.nameTextView);
            descriptionTextView=itemView.findViewById(R.id.descriptionTextView);
            pprice=itemView.findViewById(R.id.pprice);


        }
    }
}
