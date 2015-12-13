package com.github.xavierlepretre.tmdb.sync.conf;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.github.xavierlepretre.tmdb.TmdbRetrofitException;
import com.github.xavierlepretre.tmdb.model.TmdbContract.ConfigurationEntity;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationDTO;
import com.github.xavierlepretre.tmdb.model.conf.ContentValuesConfigurationFactory;
import com.github.xavierlepretre.tmdb.net.TmdbService;

import java.io.IOException;

import retrofit.Response;

public class ConfigurationSyncAdapter
{
    @NonNull private final TmdbService tmdbService;
    @NonNull private final ContentValuesConfigurationFactory factory;

    public ConfigurationSyncAdapter(@NonNull TmdbService tmdbService,
                                    @NonNull ContentValuesConfigurationFactory factory)
    {
        this.tmdbService = tmdbService;
        this.factory = factory;
    }

    @WorkerThread
    public void onPerformSync(Account account,
                              @NonNull Bundle extras,
                              String authority,
                              @NonNull ContentProviderClient provider,
                              @NonNull SyncResult syncResult)
            throws IOException, RemoteException
    {
        ConfigurationDTO dto;
        try
        {
            Response<ConfigurationDTO> response = tmdbService.getConfiguration().execute();
            if (!response.isSuccess())
            {
                throw new TmdbRetrofitException(response);
            }
            dto = response.body();
        }
        catch (IOException e)
        {
            syncResult.stats.numIoExceptions++;
            throw e;
        }
        ContentValues contentValues = factory.createFrom(dto);
        try
        {
            provider.insert(ConfigurationEntity.CONTENT_URI, contentValues);
        }
        catch (RemoteException e)
        {
            syncResult.databaseError = true;
            throw e;
        }
    }
}
