package com.app.youtubers.connection;

import com.app.youtubers.BuildConfig;
import com.app.youtubers.data.Constant;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestBuilder {

    private static API createAPI() {

        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(Constant.BASE_URL);
        builder.addConverterFactory(GsonConverterFactory.create());

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG ? Level.BODY : Level.NONE);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit retrofit = builder.build();
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(API.class);
    }

    protected API request = createAPI();
}
