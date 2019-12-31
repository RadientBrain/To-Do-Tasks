package radient.brain.taskmanagementapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

import radient.brain.taskmanagementapp.Model.Data;
import radient.brain.taskmanagementapp.Model.PostAdapter;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private PostAdapter adapter;

    private ShimmerFrameLayout mShimmerViewContainer;


    //Firebase
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    //Recycler
    private RecyclerView recyclerView;
    static boolean calledAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uId = mUser.getUid();
        if (!calledAlready)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote").child(uId);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    recyclerView.setVisibility(View.VISIBLE);

                    mShimmerViewContainer.stopShimmer();
                    mShimmerViewContainer.setVisibility(View.GONE);
                }
                else{
                    recyclerView.setVisibility(View.GONE);
                    mShimmerViewContainer.startShimmer();
                    mShimmerViewContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.keepSynced(true);


        //Recycler
        recyclerView = findViewById(R.id.recycler);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //  linearLayoutManager.setReverseLayout(true);
        //  linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        floatingActionButton = findViewById(R.id.fab_btn);

        toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Task App");


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);
                LayoutInflater layoutInflater = LayoutInflater.from(HomeActivity.this);

                View myview = layoutInflater.inflate(R.layout.custominputfield, null);
                myDialog.setView(myview);
                final AlertDialog dialog = myDialog.create();

                final EditText title = myview.findViewById(R.id.edt_title);
                final EditText note = myview.findViewById(R.id.edt_note);

                Button btn_Cancel = myview.findViewById(R.id.btn_cancel);
                btn_Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                        onCancel(dialog);
                    }

                    private void onCancel(DialogInterface dialog) {
                        Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                });




                Button btn_Save = myview.findViewById(R.id.btn_save);
                btn_Save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String mTitle = title.getText().toString().trim();
                        String mNote = note.getText().toString().trim();

                        if (TextUtils.isEmpty(mTitle)) {
                            title.setError("Required Field...");
                            return;
                        }
                        if (TextUtils.isEmpty(mNote)) {
                            note.setError("Required Field...");
                            return;
                        }

                        String id = mDatabase.push().getKey();
                        String date = DateFormat.getDateInstance().format(new Date());

                        Data data = new Data(mTitle, mNote, date, id);
                        mDatabase.child(id).setValue(data);
                        Toast toast = Toast.makeText(getApplicationContext(), "Task Saved...", Toast.LENGTH_SHORT);
                        toast.show();
                        toast.setGravity(Gravity.BOTTOM, 0, 80);
                        dialog.dismiss();

                    }
                });

                dialog.show();
            }
        });


            FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                    .setQuery(FirebaseDatabase.getInstance().getReference().child("TaskNote").child(uId), Data.class)
                    .build();

            adapter = new PostAdapter(options, this);
            recyclerView.setAdapter(adapter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmer();
    }

    @Override
    protected void onPause() {
        mShimmerViewContainer.stopShimmer();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
