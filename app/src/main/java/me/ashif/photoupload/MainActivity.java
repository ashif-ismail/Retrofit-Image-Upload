package me.ashif.photoupload;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

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

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case PICK_IMAGE:
                    postImageToServer(data.getData());
                    break;
            }
        }
    }

    private void postImageToServer(Uri uri) {

        mProgress.setMessage("Uploading Please wait...");
        mProgress.show();

        File file = new File(uri.getPath());

        Log.d("tag", "postImageToServer: " + uri.getPath());

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);


        Call<ResponseBody> call = mApiService.uploadPhoto(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    showToast("Successfully Uploaded" + response.message());
                }
                Log.d("tag", "onResponse: " + response.code());
                mProgress.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("Failed Uploading image" + " " + t.getMessage());
                t.printStackTrace();
                mProgress.dismiss();

            }
        });

    }

    private void showToast(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

}
