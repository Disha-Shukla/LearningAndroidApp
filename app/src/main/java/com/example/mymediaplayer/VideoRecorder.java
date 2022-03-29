package com.example.mymediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.hardware.Camera.CameraInfo;

import java.io.IOException;

public class VideoRecorder extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private Button capture, switchCamera;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recorder);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;
        initialize();
        //chooseCamera();
        prepareMediaRecorder();
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (recording) {
                    // stop recording and release camera
                    mediaRecorder.stop(); // stop the recording
                    releaseMediaRecorder(); // release the MediaRecorder object
                    Toast.makeText(VideoRecorder.this, "Video captured!", Toast.LENGTH_LONG).show();
                    recording = false;
                } else {
                    if (!prepareMediaRecorder()) {
                        Toast.makeText(VideoRecorder.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    // work on UiThread for better performance
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // If there are stories, add them to the table

                            try {
                                mediaRecorder.start();
                            } catch (final Exception ex) {
                                // Log.i("---","Exception in thread");
                            }
                        }
                    });

                    recording = true;
                }
            }
        });




    }

    private void initialize() {

        capture = findViewById(R.id.button_capture);
        switchCamera = findViewById(R.id.button_ChangeCamera);

    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera()==1) {
                // release the old camera instance
                // switch camera, from the front and the back and vice versa

                releaseCamera();
                chooseCamera();
            } else {
                Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            Log.v("DS",""+cameraId);
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview = new CameraPreview(VideoRecorder.this,mCamera);
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview = new CameraPreview(VideoRecorder.this,mCamera);
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
        releaseCamera();
    }
    private boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    boolean recording = false;
    View.OnClickListener captrureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*if (recording) {
                // stop recording and release camera
                mediaRecorder.stop(); // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                Toast.makeText(VideoRecorder.this, "Video captured!", Toast.LENGTH_LONG).show();
                recording = false;
            } else {
                if (!prepareMediaRecorder()) {
                    Toast.makeText(VideoRecorder.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                    finish();
                }
                // work on UiThread for better performance
                runOnUiThread(new Runnable() {
                    public void run() {
                        // If there are stories, add them to the table

                        try {
                            mediaRecorder.start();
                        } catch (final Exception ex) {
                            // Log.i("---","Exception in thread");
                        }
                    }
                });

                recording = true;
            }*/
        }
    };

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();
        mCamera = Camera.open(0);
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));

        mediaRecorder.setOutputFile("/sdcard/myvideo.mp4");
        mediaRecorder.setMaxDuration(600000); //set maximum duration 60 sec.
        mediaRecorder.setMaxFileSize(50000000); //set maximum file size 50M

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }


}