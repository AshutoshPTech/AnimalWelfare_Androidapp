package com.Pie4u.animalcare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterforRescueAct extends PagerAdapter {
    Context context;
    //private Bitmap animalBitmap;
    ImageView imageView;

    ArrayList<String> urls;
    LayoutInflater layoutInflater;


    public AdapterforRescueAct(Context context, ArrayList<String> urls) {
        this.context = context;
        this.urls = urls;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.item, container, false);


        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewAnimal);
        //imageView.setImageBitmap(images.get(position));
        Glide.with(context.getApplicationContext())
                .load(urls.get(position))

                .placeholder(R.drawable.loadingimages)
                .into(imageView);



        container.addView(itemView);

        //listening to image click
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "you clicked image " + (position + 1), Toast.LENGTH_LONG).show();

            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}