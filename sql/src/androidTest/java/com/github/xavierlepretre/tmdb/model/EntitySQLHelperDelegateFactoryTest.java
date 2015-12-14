package com.github.xavierlepretre.tmdb.model;

import android.util.SparseArray;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class EntitySQLHelperDelegateFactoryTest
{
    @Test
    public void create_createsAll() throws Exception
    {
        EntitySQLHelperDelegateFactory factory = spy(new EntitySQLHelperDelegateFactory());

        SparseArray<EntitySQLHelperDelegate> sqlHelpers = factory.createHelpers();
        assertThat(sqlHelpers.size()).isEqualTo(5);
    }
}