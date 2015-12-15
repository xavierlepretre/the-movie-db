package com.github.xavierlepretre.tmdb.sync.movie;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;

import com.github.xavierlepretre.tmdb.model.TmdbContract.GenreEntity;
import com.github.xavierlepretre.tmdb.model.movie.GenreContentValuesFactory;
import com.github.xavierlepretre.tmdb.model.movie.GenreContract;
import com.github.xavierlepretre.tmdb.model.movie.GenreDTO;
import com.github.xavierlepretre.tmdb.model.movie.GenreId;
import com.github.xavierlepretre.tmdb.model.movie.GenreListDTO;
import com.github.xavierlepretre.tmdb.net.TmdbService;
import com.github.xavierlepretre.tmdb.sync.TmdbSyncConstants;
import com.neovisionaries.i18n.LanguageCode;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

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

public class GenreSyncAdapterTest
{
    private Call<GenreListDTO> call;
    private TmdbService service;
    private GenreContentValuesFactory factory;
    private GenreSyncAdapter syncAdapter;
    private Bundle extras;
    private ContentProviderClient contentProviderClient;
    private SyncResult syncResult;

    @Before
    public void setUp() throws Exception
    {
        //noinspection unchecked
        call = mock(Call.class);
        service = mock(TmdbService.class);
        when(service.getMovieGenreList(any(LanguageCode.class))).thenReturn(call);
        factory = spy(new GenreContentValuesFactory());
        syncAdapter = new GenreSyncAdapter(service, factory);
        extras = new Bundle();
        contentProviderClient = mock(ContentProviderClient.class);
        syncResult = new SyncResult();
    }

    @Test
    public void callsServiceFactoryAndContentProvider() throws Exception
    {
        TmdbSyncConstants.putLanguage(extras, LanguageCode.sl);
        GenreListDTO dto = new GenreListDTO(
                Arrays.asList(
                        new GenreDTO(new GenreId(12), "Adventure"),
                        new GenreDTO(new GenreId(28), "Action")));
        when(call.execute()).thenReturn(Response.success(dto));

        syncAdapter.onPerformSync(null,
                extras,
                null,
                contentProviderClient,
                syncResult);

        ContentValues values1 = new ContentValues(2);
        values1.put(GenreContract._ID, 12);
        values1.put(GenreContract.COLUMN_NAME, "Adventure");
        ContentValues values2 = new ContentValues(2);
        values2.put(GenreContract._ID, 28);
        values2.put(GenreContract.COLUMN_NAME, "Action");
        final ContentValues[] wanted = new ContentValues[]{values1, values2};

        verify(service).getMovieGenreList(eq(LanguageCode.sl));
        verify(factory).createFrom(eq(dto));
        verify(contentProviderClient).bulkInsert(
                eq(GenreEntity.CONTENT_URI),
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
            verify(factory, never()).createFrom(any(GenreListDTO.class));
            verify(contentProviderClient, never()).bulkInsert(any(Uri.class), any(ContentValues[].class));
        }
    }

    @Test(expected = RemoteException.class)
    public void contentProviderFails_setsDatabaseError() throws Exception
    {
        GenreListDTO dto = new GenreListDTO(
                Arrays.asList(
                        new GenreDTO(new GenreId(12), "Adventure"),
                        new GenreDTO(new GenreId(28), "Action")));
        when(call.execute()).thenReturn(Response.success(dto));

        when(contentProviderClient.bulkInsert(any(Uri.class), any(ContentValues[].class)))
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