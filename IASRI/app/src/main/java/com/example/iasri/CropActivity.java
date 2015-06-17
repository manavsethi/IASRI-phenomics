package com.example.iasri;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.edmodo.cropper.CropImageView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;


public class CropActivity extends Activity {
    private static String SOAP_ACTION1 = "http://pack1/convertStringtoImage";
    private static String NAMESPACE = "http://pack1/";
    private static String METHOD_NAME1 = "convertStringtoImage";
    private static String URL = "http://192.168.1.105:8080/webservice/test?wsdl";
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    ProgressDialog prgDialog;
    String encodedString;
    String  fileName;
    Bitmap bitmap;
    public String resp;
    double Area;
    Intent resultAct;

    private static final int DEFAULT_ASPECT_RATIO_VALUES = 5;
    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";

    // Instance variables
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;
    File file;
    Bitmap croppedImage;
    private static final String TAG = MainActivity.class.getSimpleName();

    private String imgPath = null;
    long totalSize = 0;
    CropImageView cropImageView;
    Button cropButton,uploadButton;
    private Uri picUri;
    int x,y;
    int value;
    String fname;
    private String cropPath=null;

    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_crop);
        // Initialize components of the app
        cropImageView = (CropImageView) findViewById(R.id.mCrop);

        cropButton = (Button) findViewById(R.id.btnCrop);
        uploadButton=(Button)findViewById(R.id.btnUpload);



        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        // Receiving the data from previous activity
        Intent i = getIntent();

        // image or video path that is captured in previous activity
        imgPath = i.getStringExtra("filePath");
        String fileNameSegments[] = imgPath.split("/");
        fileName = fileNameSegments[fileNameSegments.length - 1];
        value=i.getIntExtra("Value",value);
        if((value==2)||(value==3)||(value==4)) {
            sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
            Area = sharedPreferences.getFloat("AOR", (float) Area);
            Log.d("AOR", String.valueOf(Area));
        }
        // boolean flag to identify the media type, image or video
        boolean isImage = i.getBooleanExtra("isImage", true);
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);
        if (imgPath != null) {
            // Displaying the image or video on the screen
            previewMedia(isImage);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }

        cropButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cropimage();
                save();


            }
        });


    }
    public void launchsizeactivity(View v) {

        if (value==1) {
            resultAct = new Intent(CropActivity.this, SizeActivity.class);
            resultAct.putExtra("cropPath", cropPath);
            resultAct.putExtra("fname", fname);
            resultAct.putExtra("value", value);
            startActivity(resultAct);
            finish();

        } else {

            // When Image is selected from Gallery
            if (imgPath != null && !imgPath.isEmpty()) {
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

    }

    private void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {
            Log.d("choice", String.valueOf(value));
            };

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                Log.d("crop",cropPath);
                Log.d("IMAGE",imgPath);
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
                request.addProperty("AOR", AOR);
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
//                        Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                value++;
                prgDialog.hide();
                launch();

            }
        }.execute(null, null, null);
    }

    private void launch() {
        if (value < 4) {

                    Intent intent;
                    intent = new Intent(CropActivity.this, MainActivity.class);
                    intent.putExtra("value", value);
                    startActivity(intent);
                    finish();

        }
        else {

                    Intent intent;
                    intent = new Intent(CropActivity.this, FinalAns.class);
                    intent.putExtra("finalValue", resp);
                    startActivity(intent);
            finish();


        }
    }


    private void previewMedia(boolean isImage) {
        if (isImage) {
            cropImageView.setVisibility(View.VISIBLE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 2;


            final Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);

            cropImageView.setImageBitmap(bitmap);
        }
    }

    private void cropimage() {
        croppedImage = cropImageView.getCroppedImage();
        x=(croppedImage.getHeight()*100)/49;
        y=(croppedImage.getWidth()*100)/49;
        ImageView croppedImageView = (ImageView) findViewById(R.id.croppedImageView);
        croppedImageView.setImageBitmap(croppedImage);
    }


    private void save() {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/IASRI/cropped_images/");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        fname = "Image-" + n + ".jpg";
        file = new File(myDir, fname);
        picUri = Uri.fromFile(file);
        cropPath= String.valueOf(picUri);
        Log.d("cropPath",cropPath);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            Bitmap bmp=Bitmap.createScaledBitmap(croppedImage,y,x,false);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
