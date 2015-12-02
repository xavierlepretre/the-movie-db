package com.github.xavierlepretre.tmdb.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.github.xavierlepretre.tmdb.model.movie.ContentValuesGenreFactory;
import com.github.xavierlepretre.tmdb.sync.movie.GenreSyncAdapter;
import com.github.xavierlepretre.tmdb.net.BuildConfig;
import com.github.xavierlepretre.tmdb.net.TmdbRetrofitFactory;
import com.github.xavierlepretre.tmdb.net.TmdbService;

import java.util.logging.Logger;

public class TmdbSyncService extends Service
{
    private static final String TAG = TmdbSyncService.class.getSimpleName();

    private static final Object syncAdapterLock = new Object();
    private static TmdbSyncAdapter tmdbSyncAdapter = null;

    @Override public void onCreate()
    {
        super.onCreate();
        TmdbService tmdbService = new TmdbService(
                new TmdbRetrofitFactory().create(),
                BuildConfig.TMDB_API_KEY);
        synchronized (syncAdapterLock)
        {
            if (tmdbSyncAdapter == null)
            {
                tmdbSyncAdapter = new TmdbSyncAdapter(
                        this,
                        true,
                        new GenreSyncAdapter(
                                tmdbService,
                                new ContentValuesGenreFactory()),
                        Logger.getLogger(TAG));
            }
        }
    }

    @Nullable @Override public IBinder onBind(Intent intent)
    {
        return tmdbSyncAdapter.getSyncAdapterBinder();
    }
}
