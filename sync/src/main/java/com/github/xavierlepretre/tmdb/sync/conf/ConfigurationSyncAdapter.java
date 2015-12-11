package com.github.xavierlepretre.tmdb.sync.conf;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.github.xavierlepretre.tmdb.model.TmdbContract.ConfigurationEntity;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationDTO;
import com.github.xavierlepretre.tmdb.model.conf.ContentValuesConfigurationFactory;
import com.github.xavierlepretre.tmdb.net.TmdbService;

import java.io.IOException;

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
            dto = tmdbService.getConfiguration().execute().body();
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
