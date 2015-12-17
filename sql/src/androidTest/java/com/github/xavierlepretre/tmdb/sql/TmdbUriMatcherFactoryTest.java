package com.github.xavierlepretre.tmdb.sql;

import android.content.UriMatcher;
import android.net.Uri;
import android.util.SparseArray;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TmdbUriMatcherFactoryTest
{
    @Test
    public void create_ifNoneThenNoMatch() throws Exception
    {
        UriMatcher matcher = new TmdbUriMatcherFactory().create(new SparseArray<EntityProviderDelegate>());

        assertThat(matcher.match(Uri.parse("http://no_match"))).isEqualTo(UriMatcher.NO_MATCH);
    }

    @Test
    public void create_callsRegisterOnDelegate() throws Exception
    {
        SparseArray<EntityProviderDelegate> array = new SparseArray<>();
        EntityProviderDelegate delegate1 = mock(EntityProviderDelegate.class);
        EntityProviderDelegate delegate2 = mock(EntityProviderDelegate.class);
        array.put(1, delegate1);
        array.put(2, delegate2);

        UriMatcher matcher = new TmdbUriMatcherFactory().create(array);

        verify(delegate1).registerWith(eq(matcher), eq(1));
        verify(delegate2).registerWith(eq(matcher), eq(2));
    }
}