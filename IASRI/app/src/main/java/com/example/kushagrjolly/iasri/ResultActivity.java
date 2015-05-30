package com.example.kushagrjolly.iasri;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ResultActivity extends Activity {
    Intent i;
    String[] ans;
    TextView tv3,tv4,tv5;
    private SharedPreferences sharedPreferences;
    private String MyPREFERENCES="MyPrefs";
    private double Area;
    Button btn;
    int choice;
    Intent intent;
    double finalAns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        tv3=(TextView)findViewById(R.id.textView3);
        tv4=(TextView)findViewById(R.id.textView4);
        tv5=(TextView)findViewById(R.id.textView5);
        btn=(Button)findViewById(R.id.button);

        sharedPreferences=getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Area=sharedPreferences.getFloat("AOR", (float) Area);

        i=getIntent();
//        Log.d("LOG",i.getStringExtra("result"));
        ans=i.getStringExtra("result").split("/");
        choice= Integer.parseInt(ans[0]);
        Log.d("Choice", String.valueOf(choice));

        if(ans[0].contains("1"))
        {
            tv3.setText("Area of Reference ="+Area+"");
            Log.d("TAG", ans[0] + " " + ans[1]);
            tv4.setText("Counting of 0 pixel=" + ans[1]);
            tv5.setText("Estimated 1 pixel area="+ans[2]);
        }
        else if(ans[0].contains("2")){
            tv3.setText("Counting of 0 pixel="+ans[1]);
            tv4.setText("1 pixel area="+ans[2]);
            tv5.setText("Estimated leafarea0="+ans[3]);
        }else if (ans[0].contains("3")){
            tv3.setText("Counting of 0 pixel="+ans[1]);
            tv4.setText("1 pixel area="+ans[2]);
            tv5.setText("Estimated leafarea180="+ans[3]);
            finalAns= Double.parseDouble((ans[4]));
            Log.d("final", String.valueOf(finalAns));
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice++;
                Log.d("choice1", String.valueOf(choice));
                if (choice < 4)
                    intent = new Intent(ResultActivity.this, MainActivity.class);
                else {
                    intent = new Intent(ResultActivity.this, FinalAns.class);
                    intent.putExtra("finalValue", finalAns);
                    startActivity(intent);
                }
                intent.putExtra("value", choice);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
