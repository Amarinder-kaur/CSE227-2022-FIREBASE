package com.example.cse227_2021_firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
// to add firebase in ur project there are two ways: 1. manual 2. through tools->firebase
//https://www.javatpoint.com/adding-firebase-to-app
//manual: https://firebase.google.com/docs/android/setup
//through tools->firebase  : https://firebasetutorials.com/how-firebase-works-in-android/
// ur account  in firebase: https://console.firebase.google.com/u/0/  + add real time db-> add database + give read write rights true
public class P1SimpleReadWriteFirebase extends AppCompatActivity {
    DatabaseReference ref;
    ListView lv;
    ArrayList<String> al;
    ArrayAdapter<String> ad;
EditText tv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p1simplereadwritefirebase);
        tv1=findViewById(R.id.viewname);
        //        retrieve an instance of our database using getInstance() method
  //and then reference the location we have to write to.
                ref=FirebaseDatabase.getInstance().getReference();

        lv = (ListView) findViewById(R.id.lv1);
        al = new ArrayList<>();
        ad = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,al);
        lv.setAdapter(ad);

        //fixed values write in Database
        ref.child("Name").setValue("Amarider1");
        ref.child("Phone Number").setValue("1234");
        ref.child("Profession").setValue("Astt. Professor");

        Bundle bundle = getIntent().getExtras();
        String showtext = bundle.getString("Name");
        tv1.setText(showtext);

    }
    public void doRead(View view)
    {

 //To read data at a path and to listen for any changes if the data changes, we have to use the addValueEventListener()
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    String s1 = "";
                    Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                    for (DataSnapshot ds : iterable) {
                        Toast.makeText(P1SimpleReadWriteFirebase.this, "" + ds.getValue(), Toast.LENGTH_SHORT).show();
                                //ds.child("Name"), Toast.LENGTH_SHORT).show();
                        s1 = s1 + ds.getValue();
                    }
                    al.add(s1);
                }
                ad.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}