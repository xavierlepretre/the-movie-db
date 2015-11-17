package com.github.xavierlepretre.tmdb.model;

import android.util.SparseArray;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class EntityProviderDelegateFactoryTest
{
    @Test
    public void create_callsAllCreations() throws Exception
    {
        EntityProviderDelegateFactory factory = spy(new EntityProviderDelegateFactory());

        SparseArray<EntityProviderDelegate> created = factory.createProviders();

        assertThat(created.size()).isEqualTo(1);
        verify(factory).createGenreProvider();
    }
}