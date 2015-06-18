package com.example.iasri;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class FinalAns extends ActionBarActivity {
    Intent i;
    String[] finalAns;
    String x;
    TextView tv,tv1,tv2,tv3,tv4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_ans);
        tv=(TextView)findViewById(R.id.textView6);
        tv1=(TextView)findViewById(R.id.textView7);
        tv2=(TextView)findViewById(R.id.textView8);
        tv3=(TextView)findViewById(R.id.textView9);
        tv4=(TextView)findViewById(R.id.textView10);
        i=getIntent();
        x=i.getStringExtra("finalValue");
        finalAns=x.split("/");
        tv.setText("Area of Reference="+finalAns[0]);
        tv1.setText("One Pixel Area="+finalAns[1]);
        tv2.setText("Leaf Area 0="+finalAns[2]);
        tv3.setText("Leaf Area 180="+finalAns[3]);
        tv4.setText("Final leaf area="+finalAns[4]);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_procedure, menu);
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
            Intent i= new Intent(FinalAns.this,AboutUs.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
