package com.example.vod;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateVideoMutation;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.io.File;

import javax.annotation.Nonnull;

import type.CreateVideoInput;

public class UploadVideoActivity extends AppCompatActivity {
    private static final String TAG = UploadVideoActivity.class.getSimpleName();
    private static int RESULT_LOAD_VIDEO = 1;
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        Button btnUpload = findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                uploadWithTransferUtility(videoPath);
            }
        });

        Button btnAddVideo = findViewById(R.id.btn_add_video);
        btnAddVideo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                chooseVideo();
            }
        });
    }

    public void chooseVideo() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_VIDEO);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_VIDEO && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String vidPath = cursor.getString(columnIndex);
            cursor.close();
            // String picturePath contains the path of selected Image
            videoPath = vidPath;
            String videoUploaded = "Video added.";
            ((TextView) findViewById(R.id.text_video_add)).setText(videoUploaded);
        }
    }

    private String getS3Key(String localPath) {
        //We have read and write ability under the public folder
        return new File(localPath).getName();
    }

    public void uploadWithTransferUtility(String localPath) {
        final String titleInput = ((EditText) findViewById(R.id.editTxt_title)).getText().toString();
        final String genreInput = ((EditText) findViewById(R.id.editText_genre)).getText().toString();
        String key = getS3Key(localPath);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.addUserMetadata("title", titleInput);
        objectMetadata.addUserMetadata("genre", genreInput);

        Log.d(TAG, "Uploading file from " + localPath + " to " + key);

        TransferObserver uploadObserver =
                ClientFactory.transferUtility().upload(
                        key,
                        new File(localPath),
                        objectMetadata);

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                    Log.d(TAG, "Upload is completed. ");
                    Toast.makeText(UploadVideoActivity.this, "Successfully uploaded!", Toast.LENGTH_SHORT).show();
                    UploadVideoActivity.this.finish();

                    // Upload is successful. Save the rest and send the mutation to server.
                    //save();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int)percentDonef;

                Log.d(TAG, "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                String progressString = "Upload progress: "+percentDone+"%";
                ((TextView) findViewById(R.id.text_upload_progress)).setText(progressString);
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
                Log.e(TAG, "Failed to upload video. ", ex);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UploadVideoActivity.this, "Failed to upload video", Toast.LENGTH_LONG).show();
                    }
                });
            }

        });
    }
}
