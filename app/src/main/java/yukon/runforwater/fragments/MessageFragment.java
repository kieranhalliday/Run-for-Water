package yukon.runforwater.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import yukon.runforwater.ChatMessage;
import yukon.runforwater.R;
import yukon.runforwater.User;

/**
 * Created by Kieran Halliday on 2017-11-01
 */

public class MessageFragment extends Fragment implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private DatabaseReference messagesRef, usersRef, otherMessagesRef;
    private FirebaseDatabase database;
    private FirebaseListAdapter<ChatMessage> adapter;

    private User currentUser;

    private String FBId = "", otherFBId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        String target = getArguments().getString("Current User");
        Gson gS = new Gson();
        currentUser = gS.fromJson(target, User.class);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_fragment, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        messagesRef = null;
        otherMessagesRef = null;
        usersRef = null;
        FBId = "";
        otherFBId = "";
    }

    @Override
    public void onStart() {
        super.onStart();
        getView().findViewById(R.id.fab).setOnClickListener(this);

        // Always put kenyan UUID first
        String firstUID = mAuth.getCurrentUser().getUid();
        String secondUID = currentUser.getPartnerUser();

        if (firstUID.compareTo(secondUID) > 0) {
            usersRef = database.getReference().child("Messages").child(firstUID + secondUID);
        } else {
            usersRef = database.getReference().child("Messages").child(secondUID + firstUID);
        }

        // My profile
        messagesRef = database.getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    switch (child.getKey()) {
                        case "FBProfileId":
                            FBId = String.valueOf(child.getValue());
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Other profile
        otherMessagesRef = database.getReference().child("Users").child(currentUser.getPartnerUser());
        otherMessagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    switch (child.getKey()) {
                        case "FBProfileId":
                            otherFBId = String.valueOf(child.getValue());
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        displayChatMessages();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // CLICKS
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fab:
                EditText input = getView().findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                usersRef.push().setValue(
                        new ChatMessage(input.getText().toString(), currentUser.getUsername()));

                // Clear the input
                input.setText("");
                break;
        }
    }

    // CHAT FUNCTIONALITY
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        displayChatMessages();
    }

    private void displayChatMessages() {
        ListView listOfMessages = getView().findViewById(R.id.list_of_messages);

        Query query = usersRef;//FirebaseDatabase.getInstance().getReference().child("chats");

        FirebaseListOptions<ChatMessage> options =
                new FirebaseListOptions.Builder<ChatMessage>()
                        .setQuery(query, ChatMessage.class)
                        .setLayout(android.R.layout.simple_list_item_1)
                        .build();
        adapter = new FirebaseListAdapter<ChatMessage>(options){
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = v.findViewById(R.id.message_text);
                TextView messageUser = v.findViewById(R.id.message_user);
                TextView messageTime = v.findViewById(R.id.message_time);
                ProfilePictureView profilePictureView = v.findViewById(R.id.user_image);

                messageText.setTextColor(getResources().getColor(R.color.black));
                messageUser.setTextColor(getResources().getColor(R.color.black));
                messageTime.setTextColor(getResources().getColor(R.color.black));

                TextView otherMessageText = v.findViewById(R.id.message_text_other);
                TextView otherMessageUser = v.findViewById(R.id.message_user_other);
                TextView otherMessageTime = v.findViewById(R.id.message_time_other);
                ProfilePictureView otherUserProfilePictureView = v.findViewById(R.id.other_user_image);

                otherMessageText.setTextColor(getResources().getColor(R.color.black));
                otherMessageUser.setTextColor(getResources().getColor(R.color.black));
                otherMessageTime.setTextColor(getResources().getColor(R.color.black));

                if (!FBId.equals("")) {
                    profilePictureView.setProfileId(FBId);
                } else if (!otherFBId.equals("")) {
                    //GET OTHER USER IMAGE
                    otherUserProfilePictureView.setProfileId(otherFBId);
                }

                if (model.getMessageUser().equals(currentUser.getUsername())) {

                    v.findViewById(R.id.user_message).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.other_user_message).setVisibility(View.GONE);

                    // Set their text
                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getMessageUser());

                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                            model.getMessageTime()));
                } else {

                    v.findViewById(R.id.user_message).setVisibility(View.GONE);
                    v.findViewById(R.id.other_user_message).setVisibility(View.VISIBLE);

                    // Set their text
                    otherMessageText.setText(model.getMessageText());
                    otherMessageUser.setText(model.getMessageUser());

                    // Format the date before showing it
                    otherMessageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                            model.getMessageTime()));
                }
            }
        };

        listOfMessages.setAdapter(adapter);
    }
}


