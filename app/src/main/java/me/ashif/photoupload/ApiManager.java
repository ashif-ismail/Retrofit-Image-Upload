package me.ashif.photoupload;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by Ashif on 10/1/17,January,2017
 * TechJini Solutions
 * Banglore,India
 */

public class ApiManager {
    public static final String BASE_URL = "http://www.gtruckways.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {


        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .build();
        }
        return retrofit;
    }
}