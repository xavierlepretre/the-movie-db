package com.github.xavierlepretre.tmdb;

import android.support.annotation.NonNull;

import java.io.IOException;

import retrofit.Response;

public class TmdbRetrofitException extends IOException
{
    @NonNull  private final Response response;

    public TmdbRetrofitException(@NonNull Response response)
    {
        super(response.message());
        this.response = response;
    }

    @NonNull public Response getResponse()
    {
        return response;
    }

    @Override public String toString()
    {
        String errorBody;
        try
        {
            errorBody = response.errorBody().string();
        }
        catch (IOException e)
        {
            errorBody = "Could not stringify " + e;
        }
        return "TmdbRetrofitException{" +
                "message=" + getMessage() +
                ", code=" + response.code() +
                ", errorBody=" + errorBody +
                '}';
    }
}
