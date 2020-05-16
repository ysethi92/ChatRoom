package com.yashsethi.chatroom;

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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.yashsethi.chatroom.adapter.FireBaseRecyclerAdapter;

import static android.content.ContentValues.TAG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFireBaseAuth;
    private FirebaseUser mFireBaseUser;
    private String mUsername, mPhotoUrl;
    private RecyclerView mRecyclerView;
    RecyclerView.Adapter adapter;
    EditText messageText;
    ArrayList<String> messages = new ArrayList<>();
    FirebaseFirestore mFirebasefirestore = null;
    Button send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        messageText = findViewById(R.id.message);
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
            //Toast.makeText(this, mFireBaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            mUsername = mFireBaseUser.getDisplayName();
            mPhotoUrl = mFireBaseUser.getPhotoUrl().toString();
            //initialize firestore and add user to db.

            mFirebasefirestore = FirebaseFirestore.getInstance();
            getData();
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFireBaseUser != null)
                    sendMessage();
            }
        });
//        mRecyclerView = findViewById(R.id.recycler_view);
//        adapter = new FireBaseRecyclerAdapter(this, messages, mFireBaseUser, "sad");
//        mRecyclerView.setAdapter(adapter);


    }

    private void getData() {
        mFirebasefirestore.collection("messages").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty())
                    Log.d(TAG, "onSuccess: LIST EMPTY");
                else
                {
                    List<DocumentSnapshot> docList = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot documentSnapshot : docList)
                        messages.add(documentSnapshot.get("message").toString());

                    adapter = new FireBaseRecyclerAdapter(MainActivity.this, messages, mFireBaseUser, "sad");
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
                    mRecyclerView.setAdapter(adapter);
                }
            }
        });


//        mFirebasefirestore.collection("messages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful())
//                {
//                    List<DocumentSnapshot> docList = task.getResult().getDocuments();
//                    for(DocumentSnapshot doc : docList)
//                    {
//                        messages.add((String) doc.get("message"));
//                    }
//                }
//            }
//        });
    }

    private void sendMessage() {
        Map<String, String> user = new HashMap<>();
        user.put("name", mUsername);
        user.put("message", messageText.getText().toString());

        mFirebasefirestore.collection("messages")
            .add(user)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    updateList(mFirebasefirestore);
                    //messages.add(messageText.getText().toString());
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
                            if(!messages.contains(dc.getDocument().getId()))
                                messages.add(dc.getDocument().get("message").toString());

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
                adapter = new FireBaseRecyclerAdapter(MainActivity.this, messages, mFireBaseUser, "sad");
                mRecyclerView.setAdapter(adapter);
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
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
