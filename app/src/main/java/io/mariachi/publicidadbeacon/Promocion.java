package io.mariachi.publicidadbeacon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class Promocion extends AppCompatActivity {
    ImageView imagen;
    String urlimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promocion);

        imagen=(ImageView)findViewById(R.id.imgPromo);

        Bundle bundle = getIntent().getExtras();
        urlimg = bundle.getString("imgUrl");

        Glide.with(this).load(urlimg).into(imagen);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
