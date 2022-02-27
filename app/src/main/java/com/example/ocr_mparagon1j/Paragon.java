package com.example.ocr_mparagon1j;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;


public class Paragon extends AppCompatActivity {
    DatabaseReference databaseReference;
    ListView listView;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> arrayListDate = new ArrayList<>();
    public ArrayList<ParagonData> arrayParagonData = new ArrayList<>();
    public ArrayList<ParagonData> arrayParagonDataCopy = new ArrayList<>();

    ArrayAdapter<String> arrayAdapter, arrayAdapter2;
    Button btnBack, buttonOK;
    EditText theFilter, fromDate, toDate;
    ParagonData paragonData = new ParagonData();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    boolean flag = false;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("tag", "success, I'm in oncreate paragon");

        setContentView(R.layout.activity_paragon);
        btnBack = (Button) findViewById(R.id.btnback);
        databaseReference = FirebaseDatabase.getInstance().getReference("pictures");
        listView = (ListView) findViewById(R.id.listviewtxt);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
        theFilter = (EditText) findViewById(R.id.searchFilter);
        fromDate = (EditText) findViewById(R.id.fromDate);
        toDate = (EditText) findViewById(R.id.toDate);
        buttonOK = (Button) findViewById(R.id.buttonOK);



        Log.d("data", "success I'm in: oncreate paragon");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Paragon.this, MainActivity.class);
                startActivity(intent);
            }
        });
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayListDate.clear();
                arrayParagonDataCopy.clear();
                flag = true;
                String sTestFrom = fromDate.getText().toString();
                String sTestTo = toDate.getText().toString();
                Date arrDate = null, fD = null, tD = null;
                /*Date date1 = format.parse(date1);
                Date date2 = format.parse(date2);

                if (date1.compareTo(date2) <= 0) {
                    System.out.println("earlier");
                }*/
                Log.d("tag","data filter: " + sTestFrom + "   " + sTestTo);

                //DateFormat sdf = new SimpleDateFormat(format);
                format.setLenient(false);
                try {
                    format.parse(sTestFrom);
                    format.parse(sTestTo);
                } catch (Exception error1) {
                    Toast.makeText(Paragon.this, "Wprowadź poprawny format daty yyyy-MM-dd", Toast.LENGTH_SHORT).show();
                    error1.printStackTrace();
                    arrayListDate = (ArrayList<String>)arrayList.clone();
                    arrayParagonDataCopy = (ArrayList<ParagonData>)arrayParagonData.clone();
                    arrayAdapter = new ArrayAdapter<>(Paragon.this, android.R.layout.simple_list_item_1, arrayListDate);
                    listView.setAdapter(arrayAdapter);
                }
                //if( sTestFrom.matches("\\\\d{4}-\\\\d{2}-\\\\d{2}") && sTestTo.matches("\\\\d{4}-\\\\d{2}-\\\\d{2}")) {
                    try {
                        fD = format.parse(sTestFrom);
                        tD = format.parse(sTestTo);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return;
                    }
                    for (int i = 0; i < arrayList.size(); i++) {
                        try {
                            arrDate = format.parse(arrayList.get(i).substring(0,11));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        assert arrDate != null;
                        if(arrDate.compareTo(fD) >0 && arrDate.compareTo(tD) <0) {
                            arrayListDate.add(arrayList.get(i));
                            arrayParagonDataCopy.add(arrayParagonData.get(i));
                        }
                    }
                    Log.d("tag", "arrayParagonData.size():" + arrayParagonData.size());
                    arrayAdapter = new ArrayAdapter<>(Paragon.this, android.R.layout.simple_list_item_1, arrayListDate);
                    listView.setAdapter(arrayAdapter);
            //    }
            //    else {
            //        Toast.makeText(Paragon.this, "Wprowadź poprawny format daty yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            //    }
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("pictures");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("tag", "The : " + snapshot);
                Uri uri2;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String key = ds.getKey();
                    String imgName = ds.child("imageName").getValue(String.class);
                    imgName = imgName.replace("<b>", " ");
                    Log.d("tag", "The string imgName: " + imgName);

                    String imgNameCopy = imgName;
                    Log.d("tag", "The string imgNameCopy: " + imgNameCopy);

                    imgNameCopy = imgNameCopy.substring(0, Math.min(imgNameCopy.length(), 20)) + "...";
                    String imgUrl = ds.child("imageURL").getValue(String.class);
                    String imgDate = imgUrl.substring(imgUrl.length() - 22, imgUrl.length() - 14);
                    imgDate = imgDate.substring(0,4)+"-"+imgDate.substring(4,6)+"-"+imgDate.substring(6);
                    Log.d("tag", "The key: " + key + "\nthe imgName: " + imgName + "\nimgUrl: " + imgUrl);
                    arrayList.add(imgDate + "\n" + imgNameCopy);
                    arrayAdapter.notifyDataSetChanged();

                    ParagonData paragonData2 =new ParagonData(imgDate,imgName,imgUrl,key);

                    Log.d("tag", "The uri: " + imgUrl.substring(35));

                    Log.d("tag", "The paragonData for: " + paragonData2.data);
                    Log.d("tag", "The paragonData for: " + paragonData2.name);
                    Log.d("tag", "The paragonData for: " + paragonData2.imageurl);

                    arrayParagonData.add(paragonData2);
                }

                Log.d("tag", "The read : " + snapshot.getValue());

                Log.d("tag","The array for: " + arrayParagonData.size());

                for (int i = 0; i < arrayParagonData.size(); i++) {
                    Log.d("tag","The array for: " + i);
                    Log.d("tag", "The array for: " + arrayParagonData.get(i).data);
                    Log.d("tag", "The array for: " + arrayParagonData.get(i).name);
                    Log.d("tag", "The array for: " + arrayParagonData.get(i).imageurl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("firebase", "onCancelled" + error);
            }
        });


        theFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                (Paragon.this).arrayAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Paragon.this, DetailsActivity2.class);

                Log.d("tag Intent position", String.valueOf(position));

                if (!flag) {
                    intent.putExtra("data", arrayParagonData.get(position).data);
                    intent.putExtra("name", arrayParagonData.get(position).name);
                    intent.putExtra("url", arrayParagonData.get(position).imageurl);
                    intent.putExtra("key", arrayParagonData.get(position).key);
                    intent.putExtra("key2", position);
                    Log.d("tag Intent data  ", arrayParagonData.get(position).data);
                    Log.d("tag Intent name  ", arrayParagonData.get(position).name);
                    Log.d("tag Intent imageurl  ", arrayParagonData.get(position).imageurl);
                    Log.d("tag Intent key  ", arrayParagonData.get(position).key);
                }
                else {
                    intent.putExtra("data", arrayParagonDataCopy.get(position).data);
                    intent.putExtra("name", arrayParagonDataCopy.get(position).name);
                    intent.putExtra("url", arrayParagonDataCopy.get(position).imageurl);
                    intent.putExtra("key", arrayParagonDataCopy.get(position).key);
                    intent.putExtra("key2", position);
                    Log.d("tag Intent position", String.valueOf(position));
                    Log.d("tag Intent data  ", arrayParagonDataCopy.get(position).data);
                    Log.d("tag Intent name  ", arrayParagonDataCopy.get(position).name);
                    Log.d("tag Intent imageurl  ", arrayParagonDataCopy.get(position).imageurl);
                    Log.d("tag Intent key  ", arrayParagonDataCopy.get(position).key);
                }

                startActivity(intent);
            }
        });
    }
}