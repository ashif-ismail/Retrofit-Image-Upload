package me.ashif.photoupload;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE = 007;
    private ApiService mApiService;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgress = new ProgressDialog(this);
        Button button = (Button) findViewById(R.id.button_pick);
        mApiService = ApiManager.getClient().create(ApiService.class);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_pick:
                getImageFromGallery();
                break;
        }
    }

    private void getImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                File mFile = new File(CommonUtils.getRealPathFromURI(this, data.getData()));
                postImageToServer(Uri.fromFile(mFile));
    }

    private void postImageToServer(Uri uri) {

        mProgress.setMessage("Uploading Please wait...");
        mProgress.show();

        MediaType MEDIA_TYPE = MediaType.parse("image/*");
        RequestBody body = null;
        File file = new File(uri.getPath());
        if (file.exists()) {
            try {
                body = RequestBody.create(MEDIA_TYPE, new File(CommonUtils.compressImage(file.getPath())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            callapi(file,body);
        }
    }

    private void callapi(File file, RequestBody body) {
        Call<ResponseBody> call = mApiService.uploadPhoto(MultipartBody.Part.createFormData("sample", file.getName(), body));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                showToast("Successfully Uploaded");
                mProgress.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("Failed Uploading image" + " " + t.getMessage());
                mProgress.dismiss();

            }
        });
    }

    private void showToast(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

}
