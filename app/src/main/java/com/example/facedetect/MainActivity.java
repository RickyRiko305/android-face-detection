package com.example.facedetect;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;


public class MainActivity extends AppCompatActivity {


    Button faceDetect;
    Button ChangeCam;
    private int Index=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        faceDetect = (Button)findViewById(R.id.DetectFaces);

        ChangeCam = (Button)findViewById(R.id.camChange);
        ChangeCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Index == 0){
                    //javaCameraView.setCameraIndex(1);
                    Index = 1;
                    ChangeCam.setText("Front Camera");
                }
                else{
                    //javaCameraView.setCameraIndex(-1);
                    Index = 0;
                    ChangeCam.setText("Rear Camera");
                }

            }
        });

        faceDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final PopupMenu popupMenu = new PopupMenu(getApplicationContext(),faceDetect);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Intent intent = new Intent(getApplicationContext(), LiveImage.class);
                        switch(menuItem.getItemId()){
                            case R.id.fontCamera:

                                intent.putExtra("camera","1");
                                startActivity(intent);
                                return true;
                            case R.id.rearCamera:
                                intent.putExtra("camera","0");
                                startActivity(intent);
                                return  true;
                        }
                        return false;
                    }

                });
                popupMenu.show();
//                Intent intent = new Intent(getApplicationContext(), LiveImage.class);
//                if(Index == 1){
//                    intent.putExtra("camera","1");
//                }
//                else {
//                    intent.putExtra("camera","0");
//                }
//
//                startActivity(intent);
            }
        });

    }


}
