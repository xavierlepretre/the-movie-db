package com.github.xavierlepretre.tmdb.model.movie;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.github.xavierlepretre.tmdb.model.TmdbContract.GenreEntity;
import com.github.xavierlepretre.tmdb.model.TmdbSyncConstants;
import com.github.xavierlepretre.tmdb.net.TmdbService;

import java.io.IOException;
import java.util.Vector;

public class GenreSyncAdapter
{
    @NonNull private final TmdbService tmdbService;
    @NonNull private final ContentValuesGenreFactory factory;

    public GenreSyncAdapter(@NonNull TmdbService tmdbService,
                            @NonNull ContentValuesGenreFactory factory)
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
        GenreListDTO dtos = null;
        try
        {
            dtos = tmdbService.getMovieGenreList(TmdbSyncConstants.getLanguage(extras)).execute().body();
        }
        catch (IOException e)
        {
            syncResult.stats.numIoExceptions++;
            throw e;
        }
        Vector<ContentValues> contentValuesVector = factory.createFrom(dtos);
        ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
        try
        {
            provider.bulkInsert(GenreEntity.CONTENT_URI, contentValuesArray);
        }
        catch (RemoteException e)
        {
            syncResult.databaseError = true;
            throw e;
        }
    }
}
