package com.github.xavierlepretre.tmdb.sync;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.sync.conf.ConfigurationSyncAdapter;
import com.github.xavierlepretre.tmdb.sync.movie.GenreSyncAdapter;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.fest.assertions.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class TmdbSyncAdapterTest
{
    private ConfigurationSyncAdapter configurationSyncAdapter;
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
        configurationSyncAdapter = mock(ConfigurationSyncAdapter.class);
        genreSyncAdapter = mock(GenreSyncAdapter.class);
        logger = mock(Logger.class);
        syncAdapter = new TmdbSyncAdapter(
                InstrumentationRegistry.getContext(),
                true,
                configurationSyncAdapter,
                genreSyncAdapter,
                logger);
        account = mock(Account.class);
        extras = new Bundle();
        client = mock(ContentProviderClient.class);
        syncResult = new SyncResult();
    }

    @Test
    public void onPerformSync_callsConfigurationSync() throws Exception
    {
        syncAdapter.onPerformSync(account, extras, "fake_authority", client, syncResult);

        verify(configurationSyncAdapter).onPerformSync(
                eq(account),
                eq(extras),
                eq("fake_authority"),
                eq(client),
                eq(syncResult));
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
    public void onPerformSync_logsConfigurationIOException() throws Exception
    {
        Exception exception = new IOException();
        doThrow(exception).when(configurationSyncAdapter).onPerformSync(
                any(Account.class),
                any(Bundle.class),
                anyString(),
                any(ContentProviderClient.class),
                any(SyncResult.class));

        syncAdapter.onPerformSync(account, extras, "fake_authority", client, syncResult);

        verify(logger).log(eq(Level.SEVERE), anyString(), eq(exception));
    }

    @Test
    public void onPerformSync_logsGenreIOException() throws Exception
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
    public void onPerformSync_logsConfigurationRemoteException() throws Exception
    {
        Exception exception = new RemoteException();
        doThrow(exception).when(configurationSyncAdapter).onPerformSync(
                any(Account.class),
                any(Bundle.class),
                anyString(),
                any(ContentProviderClient.class),
                any(SyncResult.class));

        syncAdapter.onPerformSync(account, extras, "fake_authority", client, syncResult);

        verify(logger).log(eq(Level.SEVERE), anyString(), eq(exception));
    }

    @Test
    public void onPerformSync_logsGenreRemoteException() throws Exception
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
    public void onPerformSync_doesNotStopOtherConfigurationExceptions() throws Exception
    {
        doThrow(new NullPointerException()).when(configurationSyncAdapter).onPerformSync(
                any(Account.class),
                any(Bundle.class),
                anyString(),
                any(ContentProviderClient.class),
                any(SyncResult.class));

        syncAdapter.onPerformSync(account, extras, "fake_authority", client, syncResult);
    }

    @Test(expected = NullPointerException.class)
    public void onPerformSync_doesNotStopOtherGenreExceptions() throws Exception
    {
        doThrow(new NullPointerException()).when(genreSyncAdapter).onPerformSync(
                any(Account.class),
                any(Bundle.class),
                anyString(),
                any(ContentProviderClient.class),
                any(SyncResult.class));

        syncAdapter.onPerformSync(account, extras, "fake_authority", client, syncResult);
    }

    @Test
    public void onPerformSync_configurationIOException_doesNotStopOthers() throws Exception
    {
        Exception exception = new IOException();
        doThrow(exception).when(configurationSyncAdapter).onPerformSync(
                any(Account.class),
                any(Bundle.class),
                anyString(),
                any(ContentProviderClient.class),
                any(SyncResult.class));

        syncAdapter.onPerformSync(account, extras, "fake_authority", client, syncResult);

        verify(genreSyncAdapter).onPerformSync(
                eq(account),
                eq(extras),
                eq("fake_authority"),
                eq(client),
                eq(syncResult));
    }

    @Test
    public void onPerformSync_genreIOException_doesNotStopOthers() throws Exception
    {
        Exception exception = new IOException();
        doThrow(exception).when(genreSyncAdapter).onPerformSync(
                any(Account.class),
                any(Bundle.class),
                anyString(),
                any(ContentProviderClient.class),
                any(SyncResult.class));

        syncAdapter.onPerformSync(account, extras, "fake_authority", client, syncResult);

        verify(configurationSyncAdapter).onPerformSync(
                eq(account),
                eq(extras),
                eq("fake_authority"),
                eq(client),
                eq(syncResult));
    }

    @Test
    public void onPerformSync_configurationRemoteException_doesNotStopOthers() throws Exception
    {
        Exception exception = new RemoteException();
        doThrow(exception).when(configurationSyncAdapter).onPerformSync(
                any(Account.class),
                any(Bundle.class),
                anyString(),
                any(ContentProviderClient.class),
                any(SyncResult.class));

        syncAdapter.onPerformSync(account, extras, "fake_authority", client, syncResult);

        verify(genreSyncAdapter).onPerformSync(
                eq(account),
                eq(extras),
                eq("fake_authority"),
                eq(client),
                eq(syncResult));
    }

    @Test
    public void onPerformSync_genreRemoteException_doesNotStopOthers() throws Exception
    {
        Exception exception = new RemoteException();
        doThrow(exception).when(genreSyncAdapter).onPerformSync(
                any(Account.class),
                any(Bundle.class),
                anyString(),
                any(ContentProviderClient.class),
                any(SyncResult.class));

        syncAdapter.onPerformSync(account, extras, "fake_authority", client, syncResult);

        verify(configurationSyncAdapter).onPerformSync(
                eq(account),
                eq(extras),
                eq("fake_authority"),
                eq(client),
                eq(syncResult));
    }

    @Test
    public void onPerformSync_configurationOtherException_stopsSome() throws Exception
    {
        Exception exception = new NullPointerException();
        doThrow(exception).when(configurationSyncAdapter).onPerformSync(
                any(Account.class),
                any(Bundle.class),
                anyString(),
                any(ContentProviderClient.class),
                any(SyncResult.class));

        try
        {
            syncAdapter.onPerformSync(account, extras, "fake_authority", client, syncResult);
            fail("We should have thrown before");
        }
        catch (NullPointerException ignored)
        {
        }

        // Genre is stopped
        verify(genreSyncAdapter, never()).onPerformSync(
                any(Account.class),
                any(Bundle.class),
                anyString(),
                any(ContentProviderClient.class),
                any(SyncResult.class));
    }

    @Test
    public void onPerformSync_genreOtherException_stopsSome() throws Exception
    {
        Exception exception = new NullPointerException();
        doThrow(exception).when(genreSyncAdapter).onPerformSync(
                any(Account.class),
                any(Bundle.class),
                anyString(),
                any(ContentProviderClient.class),
                any(SyncResult.class));

        try
        {
            syncAdapter.onPerformSync(account, extras, "fake_authority", client, syncResult);
            fail("We should have thrown before");
        }
        catch (NullPointerException ignored)
        {
        }

        // Configuration is not stopped
        verify(configurationSyncAdapter).onPerformSync(
                eq(account),
                eq(extras),
                eq("fake_authority"),
                eq(client),
                eq(syncResult));
    }
}