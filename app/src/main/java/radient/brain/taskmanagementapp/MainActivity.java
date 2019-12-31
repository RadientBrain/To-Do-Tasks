package radient.brain.taskmanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    private TextView signup;

    private EditText email;
    private EditText pass;
    private Button btnLogin;

    private ProgressDialog mDialog;

    //Firebase Auth
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }


        mDialog= new ProgressDialog(this);
        signup=findViewById(R.id.signup_txt);

        email=findViewById(R.id.email_login);
        pass=findViewById(R.id.password_login);
        btnLogin=findViewById(R.id.login_btn);



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String mEmail = email.getText().toString().trim();
                String mPass = pass.getText().toString().trim();


                if(isNetworkAvailable()){

                        if(TextUtils.isEmpty(mEmail)){
                            email.setError("Required Field...");
                            return;
                        }
                        if (TextUtils.isEmpty((mPass))) {

                            pass.setError("Required Field...");
                            return;
                        }

                        mDialog.setMessage("Processing...");
                        mDialog.show();

                        mAuth.signInWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                    mDialog.dismiss();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"Invalid Credentials! Please Try again",Toast.LENGTH_LONG).show();
                                    mDialog.dismiss();

                                }
                            }
                        });
                }
                else {
                        Toast.makeText(MainActivity.this, "Internet Required for login, Please Check Your connection and try again...", Toast.LENGTH_LONG).show();
                }

            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegistrationActivity.class));
            }
        });

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit the app?");
        builder.setNegativeButton("No", null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }
            }
        }).show();
    }
}
