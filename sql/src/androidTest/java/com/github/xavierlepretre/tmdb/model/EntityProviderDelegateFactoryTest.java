package com.github.xavierlepretre.tmdb.model;

import android.util.SparseArray;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

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

        int expectedSize = 7;
        assertThat(created.size()).isEqualTo(expectedSize);
        verify(factory).createCollectionProvider();
        verify(factory).createConfigurationProvider();
        verify(factory).createGenreProvider();
        verify(factory).createMovieProvider();
        verify(factory).createProductionCompanyProvider();
        verify(factory).createProductionCountryProvider();
        verify(factory).createSpokenLanguageProvider();

        Set<Class<? extends EntityProviderDelegate>> classes = new HashSet<>();
        for (int i = 0; i < created.size(); i++)
        {
            classes.add(created.get(created.keyAt(i)).getClass());
        }
        assertThat(classes.size()).isEqualTo(expectedSize);
    }
}