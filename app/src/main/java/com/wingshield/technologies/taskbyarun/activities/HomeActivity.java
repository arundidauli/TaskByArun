package com.wingshield.technologies.taskbyarun.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wingshield.technologies.taskbyarun.R;
import com.wingshield.technologies.taskbyarun.adapter.DataAdapter;
import com.wingshield.technologies.taskbyarun.model.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Arun Kumar on 15/07/2021.
 */
public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private TextView txtEmpty;
    RecyclerView rv_product;
    ProgressBar progressBar;
    private StorageReference storageRef, imageRef;
    DataAdapter dataAdapter;
    private List<Data> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        progressBar=findViewById(R.id.progressBar);
        dataList=new ArrayList<>();
        dataAdapter=new DataAdapter(this,dataList);
        rv_product=findViewById(R.id.rv_product);
        txtEmpty=findViewById(R.id.txtEmpty);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_product.setLayoutManager(mLayoutManager);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);


        findViewById(R.id.fab).setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        Intent intent=getIntent();
        if (intent.getStringExtra("uri")!=null){
            progressBar.setVisibility(View.VISIBLE);
            String fileName=intent.getStringExtra("filename");
            Uri uri=Uri.parse(intent.getStringExtra("uri"));
            getImageUrl(uri,fileName);
        }else {
            getDataFromFirebase();
        }





    }


    /**
     * get image uri from firebase Storage
     */
    private void getImageUrl(Uri file,String name) {
        imageRef = storageRef.child(file.getLastPathSegment());// Create Upload Task
        UploadTask uploadTask = imageRef.putFile(file);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressBar.setVisibility(View.GONE);
                       // Glide.with(context).load(uri.toString()).thumbnail(0.1f).into(binding.categoryImage);
                        Log.e(TAG, uri.toString());
                        uploadImage(uri.toString(),name);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                //progressDialog.setMessage(Util.Please_wait + "( " + progress + "% )");
            }
        });


    }


    /**
     * save data to firebase
     * I have add static entity
     * it's time taking task
     */
    private void uploadImage(String uri,String filename) {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        String id=databaseReference.child("data").push().getKey();
        Data data=new Data();

        data.setId(id);
        data.setFileName("IMG_"+filename);
        data.setFile(uri);
        data.setFiletype("A4");
        data.setNoOfPages("2");
        data.setDateTime(String.valueOf(System.currentTimeMillis()));

        assert id != null;
        databaseReference.child("data").child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull  Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful())
                 Toast.makeText(HomeActivity.this, "Image Saved successfully", Toast.LENGTH_SHORT).show();
                getDataFromFirebase();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG,e.getMessage());
                Toast.makeText(HomeActivity.this, "Image Saved successfully", Toast.LENGTH_SHORT).show();

            }
        });


    }

    /**
     * get data from firebase
     */
    private void getDataFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        reference.child("data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dataList.clear();
                progressBar.setVisibility(View.GONE);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e(TAG, Objects.requireNonNull(snapshot.getValue(Data.class)).toString());
                    if (dataSnapshot.exists()){

                        Data data = snapshot.getValue(Data.class);
                        dataList.add(data);
                    }else {
                        txtEmpty.setVisibility(View.VISIBLE);
                    }

                }
                rv_product.setAdapter(dataAdapter);

                if (dataList.isEmpty()){
                    txtEmpty.setVisibility(View.VISIBLE);
                }else txtEmpty.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG,databaseError.getMessage()+databaseError.getDetails());

            }
        });


    }
}