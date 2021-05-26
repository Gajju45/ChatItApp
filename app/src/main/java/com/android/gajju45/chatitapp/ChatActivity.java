package com.android.gajju45.chatitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity { String ReciverImage, ReciverUID, ReciverName, SenderUID;
    CircleImageView profileImage;
    TextView reciverName;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    public static String sImage;
    public static String rImage;
    ImageView backArrwo;
    LinearLayout ll1;
    CardView sendBtn;
    EditText edtMessage;
    String senderRoom, reciverRoom;
    RecyclerView messageAdater;
    ArrayList<MessagesModel> messagesArrayList;
    MessagesAdater adater;
    ImageView btEmoji;
    LinearLayout linearLayout;

    String currentTime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        calendar=Calendar.getInstance();
        simpleDateFormat=new SimpleDateFormat("hh:mm a");

        ReciverName = getIntent().getStringExtra("name");
        ReciverImage = getIntent().getStringExtra("ReciverImage");
        ReciverUID = getIntent().getStringExtra("uid");

        messagesArrayList = new ArrayList<>();

        linearLayout=findViewById(R.id.linear_layot);
        btEmoji=findViewById(R.id.bt_emoji);
        backArrwo=findViewById(R.id.back_arrow_chat);
        profileImage = findViewById(R.id.profile_image);
        reciverName = findViewById(R.id.reciverName);
        messageAdater = findViewById(R.id.messageAdater);
        ll1=findViewById(R.id.ll1);

        messagesArrayList = new ArrayList<>();
        adater = new MessagesAdater(ChatActivity.this, messagesArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdater.setLayoutManager(linearLayoutManager);
        messageAdater.setAdapter(adater);

        sendBtn = findViewById(R.id.sendBtn);
        edtMessage = findViewById(R.id.edtMessage);

        Picasso.get().load(ReciverImage).into(profileImage);
        reciverName.setText("" + ReciverName);

        SenderUID = firebaseAuth.getUid();

        senderRoom = SenderUID + ReciverUID;
        reciverRoom = ReciverUID + SenderUID;
        //Initialize emoji popup

        EmojiPopup popup=EmojiPopup.Builder.fromRootView(
                findViewById(R.id.root_view)
        ).build(edtMessage);

        btEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.toggle();
            }
        });


        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatRefrece = database.getReference().child("chats").child(senderRoom).child("messages");


        backArrwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmojiTextView emojiTextView=(EmojiTextView) LayoutInflater
                        .from(v.getContext())
                        .inflate(R.layout.emoji_text_view,linearLayout,false);
                //set text on emoji TV
                emojiTextView.setText(edtMessage.getText().toString());
                //add view to LL
                linearLayout.addView(emojiTextView);
                edtMessage.getText().clear();

                startActivity(new Intent(ChatActivity.this, HomeActivity.class));
            }
        });


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                sImage = snapshot.child("imageUri").getValue().toString();
                rImage = ReciverImage;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        chatRefrece.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    MessagesModel messages = dataSnapshot.getValue(MessagesModel.class);
                    messagesArrayList.add(messages);
                }
                adater.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
      /*  ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this,SettingActivity.class));
            }
        });

       */

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edtMessage.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Please enter Valid Message", Toast.LENGTH_SHORT).show();
                    return;
                }


                edtMessage.setText("");
                Date date = new Date();
                currentTime=simpleDateFormat.format(calendar.getTime());

                MessagesModel messages = new MessagesModel(message, SenderUID, date.getTime(),currentTime);

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.getReference().child("chats")
                                .child(reciverRoom)
                                .child("messages")
                                .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                });

            }
        });




    }
}