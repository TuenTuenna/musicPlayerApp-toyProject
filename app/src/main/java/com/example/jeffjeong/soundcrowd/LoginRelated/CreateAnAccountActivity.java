package com.example.jeffjeong.soundcrowd.LoginRelated;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeffjeong.soundcrowd.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateAnAccountActivity extends AppCompatActivity {

    private static final String LOG_TAG = CreateAnAccountActivity.class.getSimpleName();
    private EditText registerJsonId;
    private EditText registerJsonPassword;

    private TextView loadJsonId;
    private TextView loadJsonPassword;

    //image 관련
    private ImageView profileCapturedImage=null;
    private ImageView profileImageCheck;

    static final int CAPTURE_IMAGE_REQUEST = 1;
    File photoFile = null;
    private String mCurrentPhotoPath;
    private Uri photoURI = null;
    static final String SHARED_PREF = "";

    private final static int PICK_IMAGE = 100;
    static final int PICK_FROM_GALLERY = 22;
    boolean isGalleryOpen = false;
    boolean isFirst = true;
    final int REQUEST_PERMISSION_CODE = 1000;

    private Button mSaveButton;
    private Button mLoadButton;
    private Button mPicSetButton;

    JSONArray userJsonArray = new JSONArray();
    String userJsonArrayString = "";
    JSONObject userJson;
    JSONObject finalUserJson = new JSONObject();
    JSONObject userJsonObj = new JSONObject();
    String finalUserJsonString;

    static String idCheck = "";
    static String passwordCheck = "";
    static String photoCheck = "";
    boolean isFirstReg = true;
    boolean isAbleToRegister = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_an_account);
        finalUserJson = new JSONObject();
        registerJsonId = (EditText) findViewById(R.id.save_json_id);
        registerJsonPassword = (EditText) findViewById(R.id.save_json_password);


        loadJsonId = (TextView) findViewById(R.id.load_json_id);
        loadJsonPassword = (TextView) findViewById(R.id.load_json_password);

        profileCapturedImage = (ImageView) findViewById(R.id.user_profile_pic);

        profileImageCheck = (ImageView) findViewById(R.id.user_profile_pic_check);


        mSaveButton = (Button) findViewById(R.id.btn_save);
        mLoadButton = (Button) findViewById(R.id.btn_load);
        mPicSetButton = (Button) findViewById(R.id.btn_profile_pic_setting);
        loadData();
        // Request Runtime permission
        if (!checkPermissionFromDevice()) {
            requestPermission();
        }


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        mLoadButton.setOnClickListener(new View.OnClickListener() {
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


    }

    @Override
    protected void onResume() {
        super.onResume();
//        profileCapturedImage.setImageURI(photoURI);
        Glide.with(this)
                .asBitmap()
                .load(photoURI)
                .into(profileCapturedImage);


    }

    private void saveData() {
        try {
            int idDuplicateCheck = 0;
            if (userJsonArray.length() > 0) {
                for (int n = 0; n < userJsonArray.length(); n++) {

                    userJsonObj = userJsonArray.getJSONObject(n);

                    if (userJsonObj.getString("ID").equals(registerJsonId.getText().toString())) {
                        idDuplicateCheck++;

                    }
                }
            }

            if (idDuplicateCheck > 0) {
                Toast.makeText(this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                isAbleToRegister = false;
            } else if (registerJsonId.getText().toString().length() == 0) {
                Toast.makeText(this, "아이디를 입력하여 주세요.", Toast.LENGTH_SHORT).show();
                isAbleToRegister = false;
            } else if (registerJsonPassword.getText().toString().length() == 0) {
                Toast.makeText(this, "비밀번호를 입력하여 주세요.", Toast.LENGTH_SHORT).show();
                isAbleToRegister = false;
            } else {
                isAbleToRegister = true;
            }


            if (isAbleToRegister == true) {
                userJson = new JSONObject();

                userJson.put("ID", registerJsonId.getText().toString());

                userJson.put("PASSWORD", registerJsonPassword.getText().toString());
                if (photoURI != null) {
                    userJson.put("PHOTO_PATH", photoURI.toString());
                }
                userJsonArray.put(userJson);


                finalUserJson.put("USERS", userJsonArray);
//                Log.d("logcheck", finalUserJson.toString());
                SharedPreferences userShared = getSharedPreferences("userShared_Name", MODE_PRIVATE);
                SharedPreferences.Editor editor = userShared.edit();
                editor.putString("userShared_Key", finalUserJson.toString());
                editor.apply();

                registerJsonId.setText("");
                registerJsonPassword.setText("");

                Toast.makeText(this, "회원가입이 완료 되었습니다.\n 가입된 회원수: " + userJsonArray.length(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this,"회원가입이 완료 되었습니다. \n 로그인 창으로 넘어갑니다.",Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//        finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    // Press Ctrl+O

    public boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }


    private void loadData() {

        SharedPreferences userShared = getSharedPreferences("userShared_Name", MODE_PRIVATE);
        finalUserJsonString = userShared.getString("userShared_Key", "");

        try {
            finalUserJson = new JSONObject(finalUserJsonString);
            userJsonArrayString = finalUserJson.getString("USERS");
            userJsonArray = new JSONArray(userJsonArrayString);

            Toast.makeText(this, "저장된 유저수: " + userJsonArray.length(), Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "내용: " + finalUserJsonString, Toast.LENGTH_SHORT).show();
            Log.d("user", finalUserJsonString);
//            JSONObject lastJsonObj = new JSONObject()

            for (int n = 0; n < userJsonArray.length(); n++) {

                JSONObject userJsonObj = userJsonArray.getJSONObject(n);

//                Toast.makeText(this, userJsonObj.toString(), Toast.LENGTH_SHORT).show();
            }
            int index = userJsonArray.length();
            JSONObject userJsonObj = userJsonArray.getJSONObject(index - 1);
            idCheck = userJsonObj.getString("ID");
            passwordCheck = userJsonObj.getString("PASSWORD");
            photoCheck = userJsonObj.getString("PHOTO_PATH");


//            profileImageCheck.setImageURI(Uri.parse(photoCheck));
            Glide.with(this)
                    .asBitmap()
                    .load(Uri.parse(photoCheck))
                    .into(profileImageCheck);
//            Toast.makeText(this, photoCheck, Toast.LENGTH_SHORT).show();
            loadJsonId.setText("ID: " + idCheck);
//            Toast.makeText(this, idCheck, Toast.LENGTH_SHORT).show();
            loadJsonPassword.setText("PASSWORD: " + passwordCheck);
//            Toast.makeText(this, passwordCheck, Toast.LENGTH_SHORT).show();


        } catch (JSONException e) {
            e.printStackTrace();
        }


//        try {
//            userJson = new JSONObject(userString);
//            String userId = userJson.getString("ID");
//            String userPassword = userJson.getString("PASSWORD");
//            String photoPath = userJson.getString("PHOTO_PATH");
//            loadJsonId.setText(userId);
//            loadJsonPassword.setText(userPassword);
//            profileImageCheck.setImageURI(Uri.parse(photoPath));
//            Toast.makeText(this, userJson.toString(), Toast.LENGTH_SHORT).show();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


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
                    Glide.with(this)
                            .asBitmap()
                            .load(bitmap)
                            .into(profileCapturedImage);
//                    profileCapturedImage.setImageBitmap(bitmap);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureImage();

            }
        }
    }


}
