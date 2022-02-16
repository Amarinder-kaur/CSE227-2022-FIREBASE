package com.example.cse227_2021_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class P2Login extends AppCompatActivity {
    EditText etLoginemail, etLoginpass;

    FirebaseAuth auth;
    DatabaseReference root;
    String _UID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p2_login);
        etLoginemail = (EditText) findViewById(R.id.etloginemail);
        etLoginpass = (EditText)findViewById(R.id.etloginpassword);

        //Returns an instance of this class corresponding to the default FirebaseApp instance.
        //i.e initializing firebase auth object
        auth = FirebaseAuth.getInstance();

        //getInstance(): Gets the default FirebaseDatabase instance.
        //getReference(): Gets a DatabaseReference for the database root node.
        root = FirebaseDatabase.getInstance().getReference();
    }
    public void doLogin(View view)
    {
        String email = etLoginemail.getText().toString();
        String pass = etLoginpass.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            etLoginemail.setError("Email is required");
            etLoginemail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            etLoginemail.setError("Not Valid Email Required");
            etLoginemail.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(pass))
        {
            etLoginpass.setError("Pass is required");
            etLoginpass.requestFocus();
            return;
        }
        if(pass.length() <6)
        {
            etLoginemail.setError("Minimum 6 character requird");
            etLoginpass.requestFocus();
            return;
        }
        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                 /* to display the UserUID
                    final FirebaseUser Theuser = auth.getCurrentUser();
                    if (Theuser !=null)
                         _UID = Theuser.getUid();
                    Bundle bundle = new Bundle();
                    bundle.putString("Name",_UID);*/

                    // to display the name in ur next activity
                    FirebaseUser user = auth.getCurrentUser();
                    String userName = user.getDisplayName();
                    Bundle bundle = new Bundle();
                    bundle.putString("Name",userName);

                    Toast.makeText(P2Login.this,"Login Sucessful for user"+userName, Toast.LENGTH_SHORT).show();
                //    Intent intent = new Intent(P2Login.this,P1SimpleReadWriteFirebase.class);
                    Intent intent = new Intent(P2Login.this, P2MultiuserReadWriteFirebase.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthInvalidUserException)
                    {
                        Toast.makeText(P2Login.this,"User is not register", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(P2Login.this,P2RegisterActivity.class));
                    }
                }}
        });
    }
    public void doRegister(View view)
    {
        Intent i = new Intent(this,P2RegisterActivity.class);
        startActivity(i);
    }
}
