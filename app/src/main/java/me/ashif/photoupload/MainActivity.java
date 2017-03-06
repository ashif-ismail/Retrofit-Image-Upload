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

        if (resultCode == RESULT_OK)
        switch (requestCode) {
            case 007:
                 Uri mUri;
                if (data == null) {
                    return;
                } else
                    mUri = data.getData();
                File imageFile = new File(getPath(mUri));
                postImageToServer(imageFile);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void postImageToServer(File image) {

        mProgress.setMessage("Uploading Please wait...");
        mProgress.show();

        RequestBody photo = RequestBody.create(MediaType.parse("application/image"), image);
        RequestBody body = new MultipartBody.Builder()
//                .type(MultipartBody.FORM)
                .addFormDataPart("photo", image.getName(), photo)
                .build();


        Call<ResponseBody> call = mApiService.uploadPhoto(body);
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

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(projection[0]);
        String filePath = cursor.getString(columnIndex);
        Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
        Log.d("tag", "getPath: " + cursor.getString(column_index));
        String path = cursor.getString(column_index);
        cursor.close();
        return path;

    }
}
