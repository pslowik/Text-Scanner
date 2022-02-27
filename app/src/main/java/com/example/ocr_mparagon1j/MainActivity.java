package com.example.ocr_mparagon1j;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView img;
    private TextView textview;
    private Button snapBtn, detectBtn, addBtn, browseBtn;

    private Bitmap imageBitmap;

    StorageReference storageRef, mStorageRef;
    FirebaseDatabase database;
    DatabaseReference databaseRef;
    ProgressDialog progressDialog ;

    String currentPhotoPath;
    public Uri imguri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance("https://textscanner-5f31e-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseRef = (DatabaseReference) database.getReference("pictures");

        img = (ImageView) findViewById(R.id.image);
        textview = (TextView) findViewById(R.id.text);
        textview.setMovementMethod(new ScrollingMovementMethod());
        snapBtn = (Button) findViewById(R.id.snapbtn);
        detectBtn = (Button) findViewById(R.id.detectbtn);
        addBtn = (Button) findViewById(R.id.addbtn);
        browseBtn = (Button) findViewById(R.id.browsebtn);

        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling a method to
                // detect a text .
                if (img.getDrawable() != null)
                        detectTxt();
                else
                    Toast.makeText(getApplicationContext(), "Scan before detecting", Toast.LENGTH_LONG).show();
            }
        });
        snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling a method to capture our image.
                dispatchTakePictureIntent();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sTest = textview.getText().toString();
                if( !sTest.matches("") )
                    addImage();
                else
                    Toast.makeText(getApplicationContext(), "Scan before adding", Toast.LENGTH_LONG).show();
            }
        });
        browseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Paragon.class);
                startActivity(intent);
               //setContentView(R.layout.activity_paragon2);
            }

        });

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        // in the method we are displaying an intent to capture our image.
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // on below line we are calling a start activity
        // for result method to get the image captured.
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);


   /*         // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // calling on activity result method.
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // on below line we are getting
            // data from our bundles. .
//1
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");      //plik zdjecia


            // below line is to set the
            // image bitmap to our image.
            img.setImageBitmap(imageBitmap);            //zdjecie wyswietlone

            imguri = getImageUri(getApplicationContext(), imageBitmap);

/*
         //  if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                img.setImageURI(Uri.fromFile(f));
                Log.d("imguri path", Uri.fromFile(f).toString());
                imguri = Uri.fromFile(f);
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imguri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("imguri path", "error try catch ");

                }


                upload(f.getName(), imguri);
                     // Bundle extras = data.getExtras();
                      //imageBitmap = (Bitmap) extras.get("data");      //plik zdjecia

            }
*/

        }
    }

    private void addImage() {
        File f = new File(String.valueOf(imguri));
        upload(f.getName(), imguri);


    }

    private void upload(String name, Uri contentUri){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        name = "JPEG_" + timeStamp + "_" + name;

        final StorageReference image = mStorageRef.child("pictures/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "success, img url is:" + uri.toString());

                        String TempImageName = textview.getText().toString().trim();
                        Log.d("tag", "TempImageName is:" + TempImageName);

                        //  progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
                        @SuppressWarnings("VisibleForTests")
                        UploadInfo imageUploadInfo = new UploadInfo(TempImageName, image.toString());//contentUri.toString());//taskSnapshot.getUploadSessionUri().toString());
                        String ImageUploadId = databaseRef.push().getKey();
                        Log.d("tag", "TempImageName is:" + TempImageName);
                        Log.d("tag", "image upload is:" + image.toString());

                        databaseRef.child(ImageUploadId).setValue(imageUploadInfo);

                    }
                });
               // Toast.makeText(MainActivity.this, "upload complete", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "upload fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void detectTxt() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();

        assert image != null;
        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                // calling a method to process
                // our text after extracting.
                Log.d("tag", "success, img url is:" + firebaseVisionText);

                processTxt(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Fail to detect the text from image..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processTxt(FirebaseVisionText text) {
        List<FirebaseVisionText.Block> blocks = text.getBlocks();
        if (blocks.size() == 0) {

            Toast.makeText(MainActivity.this, "No Text ", Toast.LENGTH_LONG).show();
            return;
        }
        String txt ="";
        for (FirebaseVisionText.Block block : text.getBlocks()) {
            txt = txt + block.getText() + "<b>";
            Log.d("przetworzony text", txt);

            textview.setText(txt.replace("<b>", "\n"));      //txt odczytany
        }

    }
    private void addImage2() {
        StorageReference Ref = mStorageRef.child(System.currentTimeMillis()+"."+getExtension(imguri));

        Ref.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //progress.dismiss();
                Toast.makeText(MainActivity.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();

                //Uri downloadUrl = taskSnapshot.getDownloadUrl(); /* Fetch url image */

                //Picasso.with(getBaseContext()).load(imageUrl).into(imgFirebase);
                /* use picasso to fetch url and display image */
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //progress.dismiss();
                Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            });
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

}

