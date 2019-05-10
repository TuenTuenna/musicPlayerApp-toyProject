package com.example.jeffjeong.soundcrowd.Etc;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeffjeong.soundcrowd.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class CaptureImageActivity extends AppCompatActivity {

    private EditText editJsonPassword;
    private EditText editJsonPasswordCheck;

    private TextView loadJsonId;
    private TextView loadJsonPassword;

    //image 관련
    private CircleImageView profileCapturedImage;
    private CircleImageView profileImageCheck;

    static final int CAPTURE_IMAGE_REQUEST = 1;
    File photoFile = null;
    private String mCurrentPhotoPath;
    private Uri photoURI = null;
    static final String SHARED_PREF = "";

    private final static int PICK_IMAGE = 100;
    static final int PICK_FROM_GALLERY = 22;
    boolean isGalleryOpen = false;
    boolean isFirst = true;


    private Button mEditButton;
    private Button mEditCheckButton;
    private Button mPicSetButton;
    JSONObject userJson;
    String userString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);

        editJsonPassword = (EditText) findViewById(R.id.edit_json_password);
        editJsonPasswordCheck = (EditText) findViewById(R.id.edit_json_password_check);

        loadJsonId = (TextView) findViewById(R.id.load_json_id);
        loadJsonPassword = (TextView) findViewById(R.id.json_password_check);

        profileCapturedImage = (CircleImageView) findViewById(R.id.user_edit_profile_pic);

        profileImageCheck = (CircleImageView) findViewById(R.id.user_edit_profile_pic_check);

        mEditButton = (Button) findViewById(R.id.btn_profile_edit_finish);
        mEditCheckButton = (Button) findViewById(R.id.btn_profile_edit_check);
        mPicSetButton = (Button) findViewById(R.id.btn_profile_pic_edit);

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        mEditCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        mPicSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picChooseDialog();
            }
        });

        loadData();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        Glide.with(this)
//                .asBitmap()
//                .load(photoURI.toString())
//                .into(profileCapturedImage);
//        Glide.with(this)
//                .asBitmap()
//                .load(photoURI.toString())
//                .into(profileImageCheck);
        profileCapturedImage.setImageURI(photoURI);
        profileImageCheck.setImageURI(photoURI);
//        saveData();

    }

    private void saveData() {
        SharedPreferences userShared = getSharedPreferences("userSharedName", MODE_PRIVATE);

        userString = userShared.getString("userSharedKey", "");


        String passwordRegistered = editJsonPassword.getText().toString();
        String passwordJustTyped = editJsonPasswordCheck.getText().toString();

        if (passwordRegistered.equals(passwordJustTyped)) {
            try {
                userJson = new JSONObject(userString);
                userJson.put("PASSWORD", editJsonPassword.getText().toString());
                userJson.put("PHOTO_PATH", photoURI.toString());
            } catch (JSONException je) {
                je.printStackTrace();
            }


            SharedPreferences.Editor editor = userShared.edit();
            editor.putString("userSharedKey", userJson.toString());
            editor.apply();
            Toast.makeText(this, "프로필 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "비밀번호: "+editJsonPassword.getText().toString(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "파일경로: "+photoURI.toString(), Toast.LENGTH_SHORT).show();
            editJsonPassword.setText("");
            editJsonPasswordCheck.setText("");



        } else {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadData() {


        SharedPreferences userShared = getSharedPreferences("userSharedName", MODE_PRIVATE);
        userString = userShared.getString("userSharedKey", "");
        try {
            userJson = new JSONObject(userString);

            String userId = userJson.getString("ID");
            String userPassword = userJson.getString("PASSWORD");
            String photoPath = userJson.getString("PHOTO_PATH");
            photoURI = Uri.parse(photoPath);
            loadJsonId.setText(userId);
            loadJsonPassword.setText(userPassword);

            if (photoPath == null) {
                Toast.makeText(this, "이미지가 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
                profileImageCheck.setImageURI(Uri.parse(photoPath));
                profileCapturedImage.setImageURI(Uri.parse(photoPath));
            }
//            Toast.makeText(this, userJson.toString(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void picChooseDialog() {

        final CharSequence[] options = {"사진찍기", "내 갤러리"};
        //show options to user
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("선택해주세요.")
                .setCancelable(true)
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //사진찍기 선택
                        if (which == 0) {
                            Toast.makeText(builder.getContext(), "사진찍기 선택!", Toast.LENGTH_SHORT).show();
                            captureImage();

                        } // 내 갤러리 선택
                        else if (which == 1) {
                            Toast.makeText(builder.getContext(), "내 갤러리사진 선택!", Toast.LENGTH_SHORT).show();
                            pickPhotoFromGallery();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);


            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                try {

                    photoFile = createImageFile();
//                    displayMessage(getBaseContext(), photoFile.getAbsolutePath());
                    Toast.makeText(this, "사진경로: " + photoFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(this,
                                "com.example.jeffjeong.soundcrowd.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                    }
                } catch (Exception ex) {
                    // Error occurred while creating the File
//                    displayMessage(getBaseContext(), ex.getMessage().toString());
                }

            }

        }
        //Save imagePath
        SharedPreferences MyPhotoPath = this.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = MyPhotoPath.edit();
        editor.putString("myPhotoPath", photoURI.toString());
        editor.apply();


    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();


        return image;
    }

    private void pickPhotoFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_FROM_GALLERY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Bundle extras = data.getExtras();
//        Bitmap imageBitmap = (Bitmap) extras.get("data");
//        profileCapturedImage.setImageBitmap(imageBitmap);
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            profileCapturedImage.setImageBitmap(myBitmap);
        } else if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            photoURI = data.getData();
            if (photoURI != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                    profileCapturedImage.setImageBitmap(bitmap);
                    mCurrentPhotoPath = photoURI.toString();
                    //Save imagePath
                    SharedPreferences MyPhotoPath = this.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = MyPhotoPath.edit();
                    editor.putString("myPhotoPath", mCurrentPhotoPath);
                    editor.apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureImage();

            }
        }
    }

}
