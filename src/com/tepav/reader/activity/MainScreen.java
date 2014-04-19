package com.tepav.reader.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.tepav.reader.R;

/**
 *
 * Author : kanilturgut
 * Date : 15.04.2014
 * Time : 13:47
 */
public class MainScreen extends BaseActivity {

    Context context;

    Button bHaber, bGunluk, bArastirma, bRapor, bNotlar, bBasili, bOkuma, bFav, bArchive;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        context = this;

        bHaber = (Button) findViewById(R.id.bHaber);
        bGunluk = (Button) findViewById(R.id.bGunluk);
        bArastirma = (Button) findViewById(R.id.bPublication);
        bRapor = (Button) findViewById(R.id.bRaporlar);
        bNotlar = (Button) findViewById(R.id.bNotlar);
        bBasili = (Button) findViewById(R.id.bBasiliYayin);
        bOkuma = (Button) findViewById(R.id.bReadList);
        bFav = (Button) findViewById(R.id.bFav);
        bArchive = (Button) findViewById(R.id.bArchive);

        bHaber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, NewsActivity.class));
            }
        });

        bGunluk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, BlogActivity.class));
            }
        });

        bArastirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bRapor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bNotlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bBasili.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bOkuma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}