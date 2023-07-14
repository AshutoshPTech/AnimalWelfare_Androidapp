package com.Pie4u.animalcare;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class MyCustomPagerAdapter extends PagerAdapter {
    Context context;
    private Bitmap animalBitmap;
    ImageView imageView;

    ArrayList<Bitmap> images;
    LayoutInflater layoutInflater;


    public MyCustomPagerAdapter(Context context, ArrayList<Bitmap> images) {
        this.context = context;
        this.images = images;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.size();
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
        imageView.setImageBitmap(images.get(position));



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