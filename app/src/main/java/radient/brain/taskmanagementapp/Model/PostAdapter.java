package radient.brain.taskmanagementapp.Model;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

import radient.brain.taskmanagementapp.R;

public class PostAdapter extends FirebaseRecyclerAdapter<Data, PostAdapter.PostViewHolder> {



    private Context mycontext;
    private FirebaseAuth newAuth;
    private int lastPosition=-1;

    public PostAdapter(@NonNull FirebaseRecyclerOptions<Data> options, Context context) {
        super(options);
        this.mycontext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull PostViewHolder holder, final int position, @NonNull final Data model) {

        holder.title.setText(model.getTitle());
        holder.note.setText(model.getNote());
        holder.date.setText(model.getDate());


        newAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = newAuth.getCurrentUser();
        final String uId = mUser.getUid();


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance()
                        .getReference().child("TaskNote").child(uId)
                        .child(getRef(position).getKey())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(mycontext, "Task Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialog = DialogPlus.newDialog(mycontext)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 0, 50, 0)
                        .setContentHolder(new ViewHolder(R.layout.updateinputfield))
                        .setExpanded(false)
                        .setCancelable(true)
                        .setOnCancelListener(new OnCancelListener() {
                            @Override
                            public void onCancel(DialogPlus dialog) {
                                Toast.makeText(mycontext, "Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create();

                View holderView = (LinearLayout) dialog.getHolderView();

                final EditText title = holderView.findViewById(R.id.edt_title_upd);
                final EditText note = holderView.findViewById(R.id.edt_note_upd);
                Button update_btn = holderView.findViewById(R.id.btn_update_upd);
                Button cancel = holderView.findViewById(R.id.btn_cancel_upd);
                title.setText(model.getTitle());
                note.setText(model.getNote());


                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Toast.makeText(mycontext, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                });

                update_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Map<String, Object> map = new HashMap<>();
                        map.put("title", title.getText().toString().trim());
                        map.put("note", note.getText().toString().trim());

                        FirebaseDatabase.getInstance()
                                .getReference().child("TaskNote").child(uId)
                                .child(getRef(position).getKey())
                                .updateChildren(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialog.dismiss();
                                        Toast.makeText(mycontext, "Task Updated Successfully", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });

                dialog.show();
            }
        });


       // setAnimation(holder.itemView,position);

    }


//    public void setAnimation(View viewToAnimate, int position){
//
//        if (position>lastPosition){
//            ScaleAnimation animation= new ScaleAnimation(0.0f,1.0f,0.0f,1.0f,
//                    Animation.RELATIVE_TO_SELF,0.5f,
//                    Animation.RELATIVE_TO_SELF,0.5f
//                    );
//            animation.setDuration(1500);
//            viewToAnimate.startAnimation(animation);
//            lastPosition=position;
//        }
//    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_data, parent, false);
        return new PostViewHolder(view);
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        TextView title, note, date;
        ImageView edit, delete;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_item);
            note = itemView.findViewById(R.id.note_item);
            date = itemView.findViewById(R.id.date_item);
            edit = itemView.findViewById(R.id.edt_btn);
            delete = itemView.findViewById(R.id.delete_btn);

        }
    }


}
