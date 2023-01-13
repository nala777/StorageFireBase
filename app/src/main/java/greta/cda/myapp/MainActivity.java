package greta.cda.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private static final int PICK_FILE_REQUEST = 1;
    private Button select;
    private Button upload;
    private Button download;
    private TextView path;
    private ImageView image;

    private Uri selectedFile;
    private StorageReference storageRef;
    private StorageReference storageRefImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialisation de FireBase Storage
        storageRef = FirebaseStorage.getInstance().getReference();
        storageRefImg = storageRef.child("images/firebase.jpg");

        path = findViewById(R.id.path);
        select = findViewById(R.id.select);
        upload = findViewById(R.id.upload);
        download = findViewById(R.id.download);
        image = findViewById(R.id.image);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFile();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile();
            }
        });


    }

    private void selectFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK) {
            selectedFile = data.getData();

            path.setText(selectedFile.getPath());
            select.setText("Fichier Sélectionné");
        }
    }

    private void uploadFile(){

        if (selectedFile != null) {
            final StorageReference fileRef = storageRef.child("images/" + System.currentTimeMillis() + "." +
                    getFileExtension(selectedFile));
            //System.out.println(fileRef);
            fileRef.putFile(selectedFile)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(MainActivity.this, "Fichier Uploadé", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Fichier pas uploadé", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            Toast.makeText(MainActivity.this, "Fichier Non Sélectionné", Toast.LENGTH_SHORT).show();
        }

    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(uri));
    }


    private void downloadFile(){
        storageRefImg.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Transformer les bytes en bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                //Affecter l'image à l'imageView
                image.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(MainActivity.this, "Erreur lors du téléchargement de l'image", Toast.LENGTH_SHORT).show();
            }
        });
    }

}