package com.github.xavierlepretre.tmdb.model;

import android.util.SparseArray;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class EntitySQLHelperDelegateFactoryTest
{
    @Test
    public void create_createsAll() throws Exception
    {
        EntitySQLHelperDelegateFactory factory = spy(new EntitySQLHelperDelegateFactory());

        SparseArray<EntitySQLHelperDelegate> sqlHelpers = factory.createHelpers();

        int expectedSize = 8;
        assertThat(sqlHelpers.size()).isEqualTo(expectedSize);

        Set<Class<? extends EntitySQLHelperDelegate>> classes = new HashSet<>();
        for (int i = 0; i < sqlHelpers.size(); i++)
        {
            classes.add(sqlHelpers.get(sqlHelpers.keyAt(i)).getClass());
        }
        assertThat(classes.size()).isEqualTo(expectedSize);
    }
}