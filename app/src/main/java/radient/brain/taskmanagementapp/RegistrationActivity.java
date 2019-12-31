package radient.brain.taskmanagementapp;

import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

public class RegistrationActivity extends AppCompatActivity {

    private EditText email;
    private EditText pass;
    private Button btnReg;
    private TextView login_txt;

    private FirebaseAuth mAuth;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth =FirebaseAuth.getInstance();

        email=(EditText) findViewById(R.id.email_reg);
        pass = findViewById(R.id.password_reg);
        btnReg = findViewById(R.id.reg_btn);
        login_txt = findViewById(R.id.login_txt);
        mDialog = new ProgressDialog(this);

        login_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mEmail = email.getText().toString().trim();
                String mPass = pass.getText().toString().trim();

                if(isNetworkAvailable()) {
                        if (mPass.length() <= 6) {
                            Toast.makeText(getApplicationContext(), "Atleast 6 characters Password Required", Toast.LENGTH_LONG).show();
                        }

                        if (TextUtils.isEmpty(mEmail)) {
                            email.setError("Required Field...");
                            return;
                        }

                        if (TextUtils.isEmpty(mPass)) {
                            pass.setError("Required Field...");
                            return;
                        }

                        mDialog.setMessage("Processing...");
                        mDialog.show();

                        mAuth.createUserWithEmailAndPassword(mEmail, mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_LONG).show();

                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                                    mDialog.dismiss();

                                } else {
                                    Toast.makeText(getApplicationContext(), "Problem occurred...", Toast.LENGTH_LONG).show();
                                    mDialog.dismiss();
                                }
                            }
                        });

                }
                else {
                    Toast.makeText(RegistrationActivity.this, "Internet Required for Registration, Please Check Your connection and try again...", Toast.LENGTH_LONG).show();
                }
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
