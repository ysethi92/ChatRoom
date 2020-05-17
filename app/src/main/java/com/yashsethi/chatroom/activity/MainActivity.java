package com.yashsethi.chatroom.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.yashsethi.chatroom.R;
import com.yashsethi.chatroom.adapter.FireBaseRecyclerAdapter;
import com.yashsethi.chatroom.bean.DocumentData;
import com.yashsethi.chatroom.bean.MessageContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFireBaseAuth;
    private FirebaseUser mFireBaseUser;
    FirebaseFirestore mFirebasefirestore = null;


    private RecyclerView mRecyclerView;
    RecyclerView.Adapter adapter;
    Button send;
    EditText sendMessageText;

    DocumentData documentData;
    MessageContainer messageContainer;

    ArrayList<MessageContainer> messageContainerArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        sendMessageText = findViewById(R.id.send_message_text);

        send = findViewById(R.id.send);
        mRecyclerView = findViewById(R.id.recycler_view);

        mFireBaseAuth = FirebaseAuth.getInstance();
        mFireBaseUser = mFireBaseAuth.getCurrentUser();

        if(mFireBaseUser == null) {
            // Sign in Flow
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
        else {
            mFirebasefirestore = FirebaseFirestore.getInstance();
            firebaseListener();
        }


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFireBaseUser != null)
                    sendMessage();
            }
        });
    }

    private void firebaseListener()
    {
        CollectionReference collectionReference = mFirebasefirestore.collection("messages");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null)
                    Log.w(TAG, "Listen failed.", e);

//                String source = queryDocumentSnapshots != null && queryDocumentSnapshots.getMetadata().hasPendingWrites()
//                        ? "Local" : "Server";

                //if(!queryDocumentSnapshots.isEmpty())
                getData();
            }
        });
    }
    private void getData() {

        documentData = new DocumentData();
        documentData.setMessage(messageContainerArrayList);
        mFirebasefirestore.collection("messages").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty())
                    Log.d(TAG, "onSuccess: LIST EMPTY");
                else
                {
                    List<DocumentSnapshot> docList = queryDocumentSnapshots.getDocuments();
                    documentData.clearData();
                    for (DocumentSnapshot documentSnapshot : docList) {
                        messageContainer = new MessageContainer();
                        messageContainer.setMessage(documentSnapshot.get("message").toString());
                        messageContainer.setName(documentSnapshot.get("name").toString());
                        messageContainer.setTimeStamp(documentSnapshot.get("timeStamp").toString());
                        messageContainer.setImage(documentSnapshot.get("image").toString());
                        documentData.addMessage(messageContainer);

                    }
                    documentData.sortMessages();
                    adapter = new FireBaseRecyclerAdapter(MainActivity.this, documentData.getMessage());
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
                    mRecyclerView.setAdapter(adapter);
                }
            }
        });
    }

    private void sendMessage() {
        Map<String, String> user = new HashMap<>();
        long ts = System.currentTimeMillis();
        user.put("message", sendMessageText.getText().toString());
        user.put("name", mFireBaseUser.getDisplayName());
        user.put("timeStamp", Long.toString(ts));
        user.put("image", mFireBaseUser.getPhotoUrl().toString());
        sendMessageText.setText(null);
        mFirebasefirestore.collection("messages")
            .add(user)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    getData();
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding document", e);
                }
            });
    }

    private void updateList(FirebaseFirestore mFirebasefirestore) {
        mFirebasefirestore.collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
//                            if(!messages.contains(dc.getDocument().getId()))
//                                messages.add(dc.getDocument().get("message").toString());

                            Log.d("TAG", "New Msg: " + dc.getDocument().toObject(Message.class));
                            break;
//                        case MODIFIED:
//                            Log.d("TAG", "Modified Msg: " + dc.getDocument().toObject(Message.class));
//                            break;
//                        case REMOVED:
//                            Log.d("TAG", "Removed Msg: " + dc.getDocument().toObject(Message.class));
//                            break;
                    }
                }
//                adapter = new FireBaseRecyclerAdapter(MainActivity.this, messages, names, "sad");
//                mRecyclerView.setAdapter(adapter);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.side_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign_out) {
            FirebaseAuth.getInstance().signOut();
            GoogleSignIn.getClient(this,
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                    .signOut();
            //Toast.makeText(this, mFireBaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SignInActivity.class));
            //finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
