package com.pixel.testapplication;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.pixel.hexagonmatchsdk.HexagonMatch;


public class MainActivity extends AppCompatActivity {
    private HexagonMatch pcHttps;
    private String hm_keyType = "email";
    private String hm_tag = "5003";
    private String hm_platform = "4";
    private Integer hm_clientId = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // 'this' can be passed in as the HexagonMatch constructor's first
        // argument because this extends android.content.Context.
        // Because no protocol is specified, HTTP will be used.
        //ccHttp  = new HexagonMatch(this, CLIENT_ID);

        // Instantiating a HexagonMatch instance configured for HTTPS
        //ccHttps = new HexagonMatch(this, CLIENT_ID, Protocol.HTTPS, "maupatino@gmail.com","email");
        pcHttps = new HexagonMatch(getApplicationContext(),hm_clientId, "5001","1");

        Button boton = findViewById(R.id.enviarbtn);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText valueTxt = findViewById(R.id.plain_text_input);
                String hm_keyValue = valueTxt.getText().toString();
                TextView txtView  = findViewById(R.id.textView1);
                valueTxt.setVisibility(View.INVISIBLE);

                try {
                    pcHttps.sendData(hm_keyValue, hm_keyType);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                txtView.setVisibility(View.VISIBLE);

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            setContentView(R.layout.content_main);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
