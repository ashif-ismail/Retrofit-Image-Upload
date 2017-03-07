package me.ashif.photoupload;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by asif on 27/2/17.
 */

public interface ApiService {

    @Multipart
    @POST("upload")
    Call<ResponseBody> uploadPhoto(@Part MultipartBody.Part file);
}
