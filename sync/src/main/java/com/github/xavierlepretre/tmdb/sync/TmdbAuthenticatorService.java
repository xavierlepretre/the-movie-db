package com.github.xavierlepretre.tmdb.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class TmdbAuthenticatorService extends Service
{
    @NonNull private final TmdbAuthenticator authenticator;

    public TmdbAuthenticatorService()
    {
        super();
        this.authenticator = new TmdbAuthenticator(this);
    }

    @Nullable @Override public IBinder onBind(Intent intent)
    {
        return this.authenticator.getIBinder();
    }
}
