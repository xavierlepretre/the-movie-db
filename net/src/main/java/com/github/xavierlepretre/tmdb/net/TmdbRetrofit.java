package com.github.xavierlepretre.tmdb.net;

import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.conf.ConfigurationDTO;
import com.github.xavierlepretre.tmdb.net.TmdbConstants.Configuration;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface TmdbRetrofit
{
    @GET(Configuration.PATH_CONFIGURATION)
    @NonNull Call<ConfigurationDTO> getConfiguration(
            @Query(TmdbConstants.QUERY_API_KEY) @NonNull String apiKey);
}

