package com.github.xavierlepretre.tmdb.model;

import android.support.test.InstrumentationRegistry;
import android.util.SparseArray;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TmdbDbHelperTest
{
    private static final String DB_NAME = "temp.test.db";
    EntitySQLHelperDelegate delegate1;
    EntitySQLHelperDelegate delegate2;
    SparseArray<EntitySQLHelperDelegate> array;

    @Before
    public void setUp() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(DB_NAME);
        delegate1 = mock(EntitySQLHelperDelegate.class);
        when(delegate1.getCreateQuery()).thenReturn("CREATE TABLE fake1(id INTEGER);");
        when(delegate1.getUpgradeQuery(anyInt(), anyInt())).thenReturn("DROP TABLE fake1;");
        delegate2 = mock(EntitySQLHelperDelegate.class);
        when(delegate2.getCreateQuery()).thenReturn("CREATE TABLE fake2(id INTEGER);");
        when(delegate2.getUpgradeQuery(anyInt(), anyInt())).thenReturn("DROP TABLE fake2;");
        array = new SparseArray<>();
        array.put(1, delegate1);
        array.put(2, delegate2);
    }

    @After
    public void tearDown() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(DB_NAME);
    }

    @Test
    public void onCreate_callsDelegates() throws Exception
    {
        TmdbDbHelper helper = new TmdbDbHelper(
                InstrumentationRegistry.getContext(),
                DB_NAME,
                null,
                1,
                array);

        helper.getReadableDatabase();

        verify(delegate1).getCreateQuery();
        verify(delegate2).getCreateQuery();
    }

    @Test
    public void onUpgrade_callsDelegates() throws Exception
    {
        TmdbDbHelper helper = new TmdbDbHelper(
                InstrumentationRegistry.getContext(),
                DB_NAME,
                null,
                1,
                array);

        helper.getReadableDatabase();

        TmdbDbHelper newHelper = new TmdbDbHelper(
                InstrumentationRegistry.getContext(),
                DB_NAME,
                null,
                2,
                array);

        newHelper.getReadableDatabase();

        verify(delegate1).getUpgradeQuery(eq(1), eq(2));
        verify(delegate2).getUpgradeQuery(eq(1), eq(2));
        verify(delegate1, times(2)).getCreateQuery();
        verify(delegate2, times(2)).getCreateQuery();
    }
}