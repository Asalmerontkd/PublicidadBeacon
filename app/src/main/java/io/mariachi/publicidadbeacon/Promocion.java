package io.mariachi.publicidadbeacon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class Promocion extends AppCompatActivity {
    ImageView imagen;
    String urlimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promocion);

        imagen=(ImageView)findViewById(R.id.imgPromo);

        Intent intent = getIntent();
        urlimg = intent.getStringExtra("imgUrl");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this).load(urlimg).into(imagen);
        Toast.makeText(this, "URL IMG: "+urlimg, Toast.LENGTH_LONG).show();
    }
}
