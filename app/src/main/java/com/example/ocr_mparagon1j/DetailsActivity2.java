package com.example.ocr_mparagon1j;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class DetailsActivity2 extends AppCompatActivity {
    Button btnBack3, btnDelete3;
    TextView textDetails;
    ImageView img;
    Bitmap bitmap;
    String date, name, imgUrl, key;
    Uri uri2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details2);
        btnBack3 = (Button) findViewById(R.id.btnback3);
        btnDelete3 = (Button) findViewById(R.id.btndelete3);
        textDetails = (TextView) findViewById(R.id.textDetails);
        textDetails.setMovementMethod(new ScrollingMovementMethod());

        img = (ImageView) findViewById(R.id.imageDetails);

        btnBack3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity2.this, Paragon.class);
                startActivity(intent);
            }
        });


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            date = extras.getString("data");
            name = extras.getString("name");
            imgUrl = extras.getString("url");
            key = extras.getString("key");
            date = date + "\n" + name;
            Log.d("tag","date:" + date + "\nimguri: " + imgUrl);

            textDetails.setText(date);

            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(imgUrl.substring(35));

            final long ONE_MEGABYTE = 1024 * 1024;
            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    img.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        }

        btnDelete3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pictures").child(key);
                Log.d("tag ","database ref" +ref);
                ref.removeValue();

                // Create a reference to the file to delete
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(imgUrl.substring(35));

                // Delete the file
                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        Log.d("tag", "usuniety");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Error occurred!
                        Log.d("tag", "nieusuniety exception: " + exception);
                    }
                });

                Intent intent = new Intent(DetailsActivity2.this, Paragon.class);
                startActivity(intent);
            }
        });
    }
}
