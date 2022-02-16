package com.example.cse227_2021_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.core.Constants;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
/*
Choose Button: We will tap this button to choose an image from the gallery.
EditText Enter Name: In this EditText we will put the label for the chosen image.
ImageView: The middle area (you cann’t see it in the layout because it is blank
ImageView) contains an ImageView where we will display the chosen image.
Upload Button: Tapping this button will upload the selected image to Firebase Storage.
TextView Uploads: Tapping this TextView will open another activity where we will
display the uploaded images with labels.

 */
//for detials: https://www.simplifiedcoding.net/firebase-storage-example/
//or
//go to Tools--> Firebase--> RealtimeDatabase--> Connect Firebase --> then add Realtime database
//and add -> addfirebase storage in ur app to your app(add dependancy ) and in storage firebase-> rules->allow read write

//URL: URL stands for Uniform Resource Locator. A URL is nothing more than the address of a given unique resource on the Web.
//for details: https://firebase.google.com/docs/storage/android/upload-files

public class P3Storage extends AppCompatActivity implements View.OnClickListener{
    //constant to track image chooser intent
    private static final int PICK_IMAGE_REQUEST = 234;
    //view objects
    private Button buttonChoose;
    private Button buttonUpload;
    private EditText editTextName;
    private TextView textViewShow;
    private ImageView imageView;

    //uri to store file
    private Uri filePath;

    //firebase objects
    private StorageReference storageReference;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p3_storage);
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        imageView = (ImageView) findViewById(R.id.imageView);
        editTextName = (EditText) findViewById(R.id.editText);
        textViewShow = (TextView) findViewById(R.id.textViewShow);

        // Create a storage reference from our app
        storageReference = FirebaseStorage.getInstance().getReference();
        // mDatabase = FirebaseDatabase.getInstance().getReference(SyncStateContract.Constants.DATABASE_PATH_UPLOADS);

        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        //    textViewShow.setOnClickListener(this);
    }
        @Override
    public void onClick(View view) {
        if (view == buttonChoose) {
                   showFileChooser();
        } else if (view == buttonUpload) {
                  uploadFile();
        } else if (view == textViewShow) {
        }
    }
    //Select Image from Gallery with Intents in android
    private void showFileChooser() {
        Intent intent = new Intent();
       //public Intent setType (String type): if type null then all types accepted and Returns the same Intent object, for chaining multiple calls into a single statement. This value cannot be null.
       //Set an explicit MIME(Multipurpose Internet Mail Extensions) data type.
        // to check different types of MIME:  https://stackoverflow.com/questions/13065838/what-are-the-possible-intent-types-for-intent-settypetype
        intent.setType("image/*");

//An ACTION_GET_CONTENT could allow the user to create the data as it runs (for example taking a picture or recording a sound),
// let them browse over the web and download the desired data, etc.
        intent.setAction(Intent.ACTION_GET_CONTENT);
//you should wrap the GET_CONTENT intent with a chooser (through createChooser(Intent, CharSequence)), which will give the
// proper interface for the user to pick how to send your data and allow you to specify a prompt indicating what they are doing.
// You will usually specify a broad MIME type (such as image/* or */*), resulting in a broad range of content types the
// user can select from.
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    //Now once the selection(through intent in showFileChooser method) has been made, we’ll show up the image in the Activity/Fragment user interface,
    // using an ImageView.For this, we’ll have to override onActivityResult():

    //Notice that in the call to startActivityForResult we provided an int value in the form of PICK_IMAGE_REQUEST -
    // this tells the system what return-code to use when the invoked Activity completes, so that we can respond correctly.

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            //to display in the imageview
            try {
                //MediaStore.Images.Media is a class which has method getContentResolver()
                //This method was deprecated in API level 29. loading of images should be performed through
                // ImageDecoder#createSource(ContentResolver, Uri),
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // it will return as the extension of the selected file by taking Uri object.
    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    // this method upload the selected file to Firebase
    private void uploadFile() {
        //checking if file is available
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            //getting the storage reference and store the extension of file also
          //Returns a new instance of StorageReference pointing to a child location of the current reference.
           //Create a reference to P3Constants.STORAGE_PATH_UPLOADS
            StorageReference sRef = storageReference.child(P3Constants.STORAGE_PATH_UPLOADS +
                    System.currentTimeMillis() + "." + getFileExtension(filePath));// created the child in upload folder + timeinmilliseconds+ extension (i.e my file name)

            //upload local files on the device, such as photos and videos from the camera, with the putFile() method.
            // putFile() takes a File and returns an UploadTask which you can use to manage and monitor the status of the upload.
            //adding the file to reference
            sRef.putFile(filePath)
                    // Register observers to listen for when the download is done or if it fails
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot1) {
                     // taskSnapshot1.getMetadata() contains file metadata such as size, content-type, etc


                            //dismissing the progress dialog
                            progressDialog.dismiss();

                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                            //creating the upload object to store uploaded image details
                            P3Upload upload = new P3Upload(editTextName.getText().toString().trim(),
                                    taskSnapshot1.getMetadata().getReference().getDownloadUrl().toString());
//.getMetadata():return the metadata for the object. After uploading, this will return the resulting final Metadata which will include the upload URL.
//After uploading a file, you can get a URL to download the file by calling the getDownloadUrl() method on the StorageReference

                            //adding an upload to firebase database
                            String uploadId = mDatabase.push().getKey();
                            mDatabase.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            //display an error if no file is selected
        }
    }
}

