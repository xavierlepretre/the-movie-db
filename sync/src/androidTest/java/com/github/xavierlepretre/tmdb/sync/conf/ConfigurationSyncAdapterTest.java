package com.github.xavierlepretre.tmdb.sync.conf;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;

import com.github.xavierlepretre.tmdb.model.TmdbContract.ConfigurationEntity;
import com.github.xavierlepretre.tmdb.model.conf.ChangeKey;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationContract;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationContract.ImagesConfSegment;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationDTO;
import com.github.xavierlepretre.tmdb.model.conf.ContentValuesConfigurationFactory;
import com.github.xavierlepretre.tmdb.model.conf.ImageSize;
import com.github.xavierlepretre.tmdb.model.conf.ImagesConfDTO;
import com.github.xavierlepretre.tmdb.net.TmdbService;
import com.github.xavierlepretre.tmdb.sync.TmdbSyncConstants;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import retrofit.Call;
import retrofit.Response;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConfigurationSyncAdapterTest
{
    private Call<ConfigurationDTO> call;
    private TmdbService service;
    private ContentValuesConfigurationFactory factory;
    private ConfigurationSyncAdapter syncAdapter;
    private Bundle extras;
    private ContentProviderClient contentProviderClient;
    private SyncResult syncResult;

    @Before
    public void setUp() throws Exception
    {
        //noinspection unchecked
        call = mock(Call.class);
        service = mock(TmdbService.class);
        when(service.getConfiguration()).thenReturn(call);
        factory = spy(new ContentValuesConfigurationFactory());
        syncAdapter = new ConfigurationSyncAdapter(service, factory);
        extras = new Bundle();
        contentProviderClient = mock(ContentProviderClient.class);
        syncResult = new SyncResult();
    }

    @Test
    public void callsServiceFactoryAndContentProvider() throws Exception
    {
        TmdbSyncConstants.putLanguage(extras, new Locale("sl"));
        ConfigurationDTO dto = new ConfigurationDTO(
                new ImagesConfDTO(
                        "http://image.tmdb.org/t/p/",
                        "https://image.tmdb.org/t/p/",
                        Arrays.asList(new ImageSize("w300"), new ImageSize("w780")),
                        Arrays.asList(new ImageSize("w45"), new ImageSize("w92")),
                        Arrays.asList(new ImageSize("w92"), new ImageSize("w154")),
                        Arrays.asList(new ImageSize("w45"), new ImageSize("w185")),
                        Arrays.asList(new ImageSize("w92"), new ImageSize("w185"))),
                Arrays.asList(new ChangeKey("adult"), new ChangeKey("air_date")));
        when(call.execute()).thenReturn(Response.success(dto));

        syncAdapter.onPerformSync(null,
                extras,
                null,
                contentProviderClient,
                syncResult);

        ContentValues wanted = new ContentValues();
        wanted.put(ConfigurationContract._ID, ConfigurationContract.UNIQUE_ROW_ID);
        wanted.put(ImagesConfSegment.COLUMN_BASE_URL, "http://image.tmdb.org/t/p/");
        wanted.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, "https://image.tmdb.org/t/p/");
        wanted.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w300,w780");
        wanted.put(ImagesConfSegment.COLUMN_LOGO_SIZES, "w45,w92");
        wanted.put(ImagesConfSegment.COLUMN_POSTER_SIZES, "w92,w154");
        wanted.put(ImagesConfSegment.COLUMN_PROFILE_SIZES, "w45,w185");
        wanted.put(ImagesConfSegment.COLUMN_STILL_SIZES, "w92,w185");
        wanted.put(ConfigurationContract.COLUMN_CHANGE_KEYS, "adult,air_date");

        verify(service).getConfiguration();
        verify(factory).createFrom(eq(dto));
        verify(contentProviderClient).insert(
                eq(ConfigurationEntity.CONTENT_URI),
                eq(wanted));
    }

    @Test(expected = IOException.class)
    public void networkFails_increasesStats() throws Exception
    {
        doThrow(new IOException()).when(call).execute();
        syncResult.stats.numIoExceptions = 3;
        try
        {
            syncAdapter.onPerformSync(null,
                    extras,
                    null,
                    contentProviderClient,
                    syncResult);
        }
        finally
        {
            assertThat(syncResult.stats.numIoExceptions).isEqualTo(4);
            verify(factory, never()).createFrom(any(ConfigurationDTO.class));
            verify(contentProviderClient, never()).insert(any(Uri.class), any(ContentValues.class));
        }
    }

    @Test(expected = RemoteException.class)
    public void contentProviderFails_setsDatabaseError() throws Exception
    {
        ConfigurationDTO dto = new ConfigurationDTO(
                new ImagesConfDTO(
                        "http://image.tmdb.org/t/p/",
                        "https://image.tmdb.org/t/p/",
                        Arrays.asList(new ImageSize("w300"), new ImageSize("w780")),
                        Arrays.asList(new ImageSize("w45"), new ImageSize("w92")),
                        Arrays.asList(new ImageSize("w92"), new ImageSize("w154")),
                        Arrays.asList(new ImageSize("w45"), new ImageSize("w185")),
                        Arrays.asList(new ImageSize("w92"), new ImageSize("w185"))),
                Arrays.asList(new ChangeKey("adult"), new ChangeKey("air_date")));
        when(call.execute()).thenReturn(Response.success(dto));

        when(contentProviderClient.insert(any(Uri.class), any(ContentValues.class)))
                .thenThrow(new RemoteException());
        syncResult.databaseError = false;

        try
        {
            syncAdapter.onPerformSync(null,
                    extras,
                    null,
                    contentProviderClient,
                    syncResult);
        }
        finally
        {
            assertThat(syncResult.databaseError).isTrue();
        }
    }
}