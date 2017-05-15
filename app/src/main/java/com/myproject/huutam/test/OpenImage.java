package com.myproject.huutam.test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OpenImage extends AppCompatActivity {

    TextView tvShow;
    ImageView imgShow;
    ImageButton imgBack, imgStartGame, imgOpen, imgTakePhoto;
    int CHOOSE_IMAGE = 123;
    int TAKE_PHOTO = 321;
    int level = 0;
    int FINISH_OPEN = 111;

    Bitmap bitmap;

    private String pictureImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);
        initialize();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            level = bundle.getInt("level");
        }
        tvShow.setText("Mission " + level);
        bitmap= BitmapFactory.decodeResource(this.getResources(), R.drawable.tam);
        imgShow.setImageBitmap(bitmap);

        setEvent();
    }

    private void setEvent() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMission = new Intent(OpenImage.this, MissionActivity.class);
                intentMission.putExtra("openreturn", "openreturn");
                finish();
                startActivity(intentMission);
            }
        });

        imgStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filename = "bitmap.png";
                FileOutputStream stream = null;
                try {
                    stream = openFileOutput(filename, Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    stream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent;
                if(level < 4) {
                    intent = new Intent(OpenImage.this, MainActivity.class);
                }
                else{
                    intent = new Intent(OpenImage.this, MainGameType2.class);
                }
//                intent = new Intent(OpenImage.this, MainGameType2.class);
                intent.putExtra("level", level);
                intent.putExtra("path", filename);
                finish();
                startActivity(intent);
            }
        });

        imgOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, CHOOSE_IMAGE);
            }
        });

        imgTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = timeStamp + ".jpg";
                File storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
                File file = new File(pictureImagePath);
                Uri outputFileUri = Uri.fromFile(file);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(cameraIntent, TAKE_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_IMAGE) {
                try {
                    Uri imageUri = data.getData();
                    InputStream is = getContentResolver().openInputStream(imageUri);
                    bitmap = BitmapFactory.decodeStream(is);
                    imgShow.setImageBitmap(bitmap);
                    //imgView.setImageURI(imageUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == TAKE_PHOTO) {
//                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                imgView.setImageBitmap(bitmap);
                File imgFile = new  File(pictureImagePath);
                if(imgFile.exists()) {
                    bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imgShow.setImageBitmap(bitmap);
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(imgFile);
                    mediaScanIntent.setData(contentUri);
                    this.sendBroadcast(mediaScanIntent);
                }
            }
            else if(requestCode == FINISH_OPEN){

            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initialize() {
        tvShow = (TextView) findViewById(R.id.tvMission);
        imgShow = (ImageView) findViewById(R.id.imgShow);
        imgBack = (ImageButton) findViewById(R.id.imgBackMission);
        imgStartGame = (ImageButton) findViewById(R.id.imgStartGame);
        imgOpen = (ImageButton) findViewById(R.id.imgOpenImage);
        imgTakePhoto = (ImageButton) findViewById(R.id.imgTakePhoto);
    }
}
