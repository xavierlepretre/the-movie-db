package com.github.xavierlepretre.tmdb.sync.movie;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.github.xavierlepretre.tmdb.TmdbRetrofitException;
import com.github.xavierlepretre.tmdb.model.TmdbContract.GenreEntity;
import com.github.xavierlepretre.tmdb.model.movie.ContentValuesGenreFactory;
import com.github.xavierlepretre.tmdb.model.movie.GenreListDTO;
import com.github.xavierlepretre.tmdb.net.TmdbService;
import com.github.xavierlepretre.tmdb.sync.TmdbSyncConstants;

import java.io.IOException;
import java.util.Vector;

import retrofit.Response;

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
        GenreListDTO dtos;
        try
        {
            Response<GenreListDTO> response = tmdbService.getMovieGenreList(TmdbSyncConstants.getLanguage(extras)).execute();
            if (!response.isSuccess())
            {
                throw new TmdbRetrofitException(response);
            }
            dtos = response.body();
        }
        catch (IOException e)
        {
            syncResult.stats.numIoExceptions++;
            throw e;
        }
        Vector<ContentValues> contentValuesVector = factory.createFrom(dtos);
        ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
        contentValuesVector.toArray(contentValuesArray);
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
