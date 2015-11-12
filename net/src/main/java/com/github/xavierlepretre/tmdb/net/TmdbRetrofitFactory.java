package com.github.xavierlepretre.tmdb.net;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import retrofit.Converter.Factory;
import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

public class TmdbRetrofitFactory
{
    @NonNull public TmdbRetrofit create()
    {
        return new Retrofit.Builder()
                .baseUrl(TmdbConstants.CONTENT_URL)
                .client(createOkHttpClient())
                .addConverterFactory(createConverterFactory())
                .build()
                .create(TmdbRetrofit.class);
    }

    @NonNull public OkHttpClient createOkHttpClient()
    {
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new Interceptor()
        {
            @Override public Response intercept(Chain chain) throws IOException
            {
                Request request = chain.request();
                Response response = chain.proceed(request);
                return response;
            }
        });
        return client;
    }

    @NonNull public Factory createConverterFactory()
    {
        return JacksonConverterFactory.create(createMapper());
    }

    @NonNull public ObjectMapper createMapper()
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
