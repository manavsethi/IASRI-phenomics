package com.example.iasri;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;


public class SizeActivity extends Activity {
    private static String SOAP_ACTION1 = "http://pack1/convertStringtoImage";
    private static String NAMESPACE = "http://pack1/";
    private static String METHOD_NAME1 = "convertStringtoImage";
    private static String URL = "http://192.168.1.105:8080/webservice/test?wsdl";
    TextView textView;
    EditText sizeText;
    Button submitbtn;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    ProgressDialog prgDialog;
    Bitmap bitmap;
    String Area,encodedString;
    private String cropPath;
    public String resp;
    private int value;
    private String fname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_size);
        Intent intent=getIntent();
        cropPath=intent.getStringExtra("cropPath");
        value=intent.getIntExtra("value", value);
        fname=intent.getStringExtra("fname");
        textView=(TextView)findViewById(R.id.textView);
        textView.setText(Html.fromHtml("Enter size in cm<sup>2</sup>"));
        sizeText=(EditText)findViewById(R.id.editText);
        submitbtn=(Button)findViewById(R.id.buttonSubmit);
        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False
        prgDialog.setCancelable(false);

    }

    public void next(View view){
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("AOR", Float.parseFloat(sizeText.getText().toString()));
        editor.commit();
        Area=sizeText.getText().toString();
        if (cropPath != null && !cropPath.isEmpty()) {
            //prgDialog.setMessage("Converting Image to Binary Data");
            prgDialog.setMessage("Uploading data, please wait...");
            prgDialog.show();
            // Convert image to String using Base64
            encodeImagetoString();


            // When Image is not selected from Gallery
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "You must select image from gallery before you try to upload",
                    Toast.LENGTH_LONG).show();

        }

    }
    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            };

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                cropPath=cropPath.substring(7);
                Log.d("crop",cropPath);

                bitmap = BitmapFactory.decodeFile(cropPath,
                        options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, 0);
                //Initialize soap request + add parameters

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
                request.addProperty("encodedImageStr",encodedString);
                request.addProperty("fileName", fname);

                String AOR= String.valueOf(Area);
                Log.d("Aor",AOR);
                request.addProperty("AOR",AOR);
                request.addProperty("val",value);


                //Declare the version of the SOAP request
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                //this is the actual part that will call the webservice
                try {
                    androidHttpTransport.call(SOAP_ACTION1, envelope);
                } catch (Exception e)  {
                    e.printStackTrace();
                }
                try {


                    // Get the SoapResult from the envelope body.
                    SoapObject result = (SoapObject)envelope.bodyIn;

                    if(result != null)
                    {
                        //Get the first property and change the label text
                        resp=result.getProperty(0).toString();
                        Log.d("rsult", resp);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {

                prgDialog.hide();
                launchActivity(true);
            }
        }.execute(null, null, null);
    }

    private void launchActivity(boolean b) {
        value++;
        Intent resultAct = new Intent(SizeActivity.this, MainActivity.class);
        resultAct.putExtra("value", value);
        startActivity(resultAct);
        finish();

    }



}
