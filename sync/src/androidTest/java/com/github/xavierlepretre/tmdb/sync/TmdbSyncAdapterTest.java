package com.github.xavierlepretre.tmdb.sync;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.sync.movie.GenreSyncAdapter;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TmdbSyncAdapterTest
{
    private GenreSyncAdapter genreSyncAdapter;
    private Logger logger;
    private TmdbSyncAdapter syncAdapter;
    private Account account;
    private Bundle extras;
    private ContentProviderClient client;
    private SyncResult syncResult;

    @Before
    public void setUp() throws Exception
    {
        genreSyncAdapter = mock(GenreSyncAdapter.class);
        logger = mock(Logger.class);
        syncAdapter = new TmdbSyncAdapter(InstrumentationRegistry.getContext(), true, genreSyncAdapter, logger);
        account = mock(Account.class);
        extras = new Bundle();
        client = mock(ContentProviderClient.class);
        syncResult = new SyncResult();
    }

    @Test
    public void onPerformSync_callsGenreSync() throws Exception
    {
        syncAdapter.onPerformSync(account, extras, "fake_authority", client, syncResult);

        verify(genreSyncAdapter).onPerformSync(
                eq(account),
                eq(extras),
                eq("fake_authority"),
                eq(client),
                eq(syncResult));
    }

    @Test
    public void onPerformSync_logsIOException() throws Exception
    {
        Exception exception = new IOException();
        doThrow(exception).when(genreSyncAdapter).onPerformSync(
                any(Account.class),
                any(Bundle.class),
                anyString(),
                any(ContentProviderClient.class),
                any(SyncResult.class));

        syncAdapter.onPerformSync(account, extras, "fake_authority", client, syncResult);

        verify(logger).log(eq(Level.SEVERE), anyString(), eq(exception));
    }

    @Test
    public void onPerformSync_logsRemoteException() throws Exception
    {
        Exception exception = new RemoteException();
        doThrow(exception).when(genreSyncAdapter).onPerformSync(
                any(Account.class),
                any(Bundle.class),
                anyString(),
                any(ContentProviderClient.class),
                any(SyncResult.class));

        syncAdapter.onPerformSync(account, extras, "fake_authority", client, syncResult);

        verify(logger).log(eq(Level.SEVERE), anyString(), eq(exception));
    }

    @Test(expected = NullPointerException.class)
    public void onPerformSync_doesNotStopOtherExceptions() throws Exception
    {
        doThrow(new NullPointerException()).when(genreSyncAdapter).onPerformSync(
                any(Account.class),
                any(Bundle.class),
                anyString(),
                any(ContentProviderClient.class),
                any(SyncResult.class));

        syncAdapter.onPerformSync(account, extras, "fake_authority", client, syncResult);
    }
}