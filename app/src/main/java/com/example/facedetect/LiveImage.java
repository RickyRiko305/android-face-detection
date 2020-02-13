package com.example.facedetect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LiveImage extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    JavaCameraView javaCameraView;
    File caseFile;

    CascadeClassifier faceDetected;

    private Mat mRgba,mGrey;

    Button Stop;
    private int Index=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_image);

        javaCameraView = (JavaCameraView)findViewById(R.id.javaCameraView);

        String sessionId = getIntent().getStringExtra("camera");

        if(!OpenCVLoader.initDebug()){
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, baseCallback);
        }
        else{
            try {
                baseCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Stop = (Button)findViewById(R.id.stopfeed);
        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        if(sessionId.equals("1")){
            javaCameraView.setCameraIndex(1);
            Index =1;
        }
        javaCameraView.setCvCameraViewListener(this);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mGrey = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGrey.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();


        String angle = getRotation(this);

        if(angle.equals("portrait")){


            if(Index ==1){

                Mat mRgbaT = mRgba.t();
                Core.flip(mRgba.t(),mRgbaT,-1);
                MatOfRect faceDetections = new MatOfRect();

                faceDetected.detectMultiScale(mRgbaT,faceDetections);

                for (Rect rect: faceDetections.toArray()){

                    Imgproc.rectangle(mRgbaT,new Point(rect.x,rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255,0,0));
                }


                return mRgbaT;
            }
            else{
                Mat mRgbaT = mRgba.t();
                Core.flip(mRgba.t(),mRgbaT,1);
                MatOfRect faceDetections = new MatOfRect();
                faceDetected.detectMultiScale(mRgbaT,faceDetections);

                for (Rect rect: faceDetections.toArray()){
                    //Imgproc.
                    Imgproc.rectangle(mRgbaT,new Point(rect.x,rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255,0,0));
                }


                return mRgbaT;
            }
        }
        else if(angle.equals("landscape")){
            MatOfRect faceDetections = new MatOfRect();
            faceDetected.detectMultiScale(mRgba,faceDetections);

            for (Rect rect: faceDetections.toArray()){
                //Imgproc.
                Imgproc.rectangle(mRgba,new Point(rect.x,rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255,0,0));
            }

            return mRgba;
        }
        else if(angle.equals("reverse_landscape")){
            Mat mRgbaT = mRgba.t();
            Core.flip(mRgba.t(),mRgbaT,1);
            Mat mRgbaT1 = mRgbaT.t();
            Core.flip(mRgbaT.t(),mRgbaT1,1);
            MatOfRect faceDetections = new MatOfRect();
            faceDetected.detectMultiScale(mRgbaT1,faceDetections);

            for (Rect rect: faceDetections.toArray()){
                Imgproc.rectangle(mRgbaT1,new Point(rect.x,rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255,0,0));
            }

            Imgproc.resize(mRgbaT1,mRgbaT1,mRgba.size());

            return mRgbaT1;
        }
        else{

        }


        //face detect

        return mRgba;
    }

    private BaseLoaderCallback baseCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) throws IOException {

            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                {
                    InputStream inputStream = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
                    File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                    caseFile = new File(cascadeDir,"haarcascade_frontalface_alt2.xml");

                    FileOutputStream fos = new FileOutputStream(caseFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while((bytesRead = inputStream.read(buffer))!=-1){
                        fos.write(buffer,0,bytesRead);
                    }

                    inputStream.close();
                    fos.close();

                    faceDetected = new CascadeClassifier(caseFile.getAbsolutePath());

                    if(faceDetected.empty()){
                        faceDetected = null;
                    }
                    else {
                        cascadeDir.delete();
                    }

                    javaCameraView.enableView();

                }
                break;

                default:{
                    super.onManagerConnected(status);
                }

            }

        }
    };

    public String getRotation(Context context){
        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return "portrait";
            case Surface.ROTATION_90:
                return "landscape";
            case Surface.ROTATION_180:
                return "reverse_portrait";
            default:
                return "reverse_landscape";
        }
    }
}
