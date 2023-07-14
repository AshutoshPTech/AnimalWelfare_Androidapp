package com.Pie4u.animalcare;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class view_ads extends AppCompatActivity {

    ImageView prodimg,call,what;
    TextView prodname,proddesc,permob;
    TextView prodprice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ads);

        prodimg = findViewById(R.id.prodimg);
        prodname=findViewById(R.id.prodname);
        proddesc=findViewById(R.id.proddesc);
        permob=findViewById(R.id.permob);
        prodprice=findViewById(R.id.prodprice);
        what=findViewById(R.id.what);

        permob = findViewById(R.id.permob);
        call = findViewById(R.id.call);


        Picasso.get().load(getIntent().getStringExtra("prodimg"))
        .placeholder(R.drawable.ic_launcher_foreground)
                .into(prodimg);

        prodname.setText(getIntent().getStringExtra("prodname"));
        proddesc.setText(getIntent().getStringExtra("proddesc"));
        permob.setText(getIntent().getStringExtra("permob"));
        prodprice.setText(getIntent().getStringExtra("prodprice"));

        what.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String number = permob.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" +number));
                startActivity(intent);
            }
        });


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num= permob.getText().toString();
                String text="Hello Is This Pet Available?";

                boolean installed = isAppInstalled("com.whatsapp");

                if (installed)
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+91 +num+"&text="+ text));
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(view_ads.this, "Whatsapp is not installed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

            }

    private boolean isAppInstalled(String s) {
        PackageManager packageManager = getPackageManager();
        boolean is_installed;

        try {
            packageManager.getPackageInfo(s, PackageManager.GET_ACTIVITIES);
            is_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            is_installed = false;
            e.printStackTrace();
        }
        return is_installed;
    }
    }
