package com.example.iasri;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


public class FinalAns extends Activity {
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
}
