package com.github.xavierlepretre.tmdb.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.util.SparseArray;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class TmdbContentProviderTest
{
    private SQLiteDatabase fakeReadable;
    private SQLiteDatabase fakeWritable;
    private UriMatcher uriMatcher;
    private SparseArray<EntityProviderDelegate> delegates;
    private TmdbContentProvider contentProvider;
    private Cursor result;

    public static class Parameter
    {
        @NonNull public final String path;
        public final int match;
        @NonNull public final int[] nonMatches;

        public Parameter(@NonNull String path, int match, @NonNull int[] nonMatches)
        {
            this.path = path;
            this.match = match;
            this.nonMatches = nonMatches;
        }
    }

    @Parameterized.Parameter
    public Parameter parameter;

    @Parameters
    public static Parameter[] getParamters()
    {
        return new Parameter[]{
                new Parameter("first", 10, new int[]{20, 30}),
                new Parameter("second", 20, new int[]{10, 30}),
                new Parameter("third", 30, new int[]{10, 20}),
        };
    }

    @Before
    public void setUp() throws Exception
    {
        fakeReadable = SQLiteDatabase.create(null);
        fakeWritable = SQLiteDatabase.create(null);
        TmdbDbHelper dbHelper = mock(TmdbDbHelper.class);
        when(dbHelper.getReadableDatabase()).thenReturn(fakeReadable);
        when(dbHelper.getWritableDatabase()).thenReturn(fakeWritable);
        uriMatcher = spy(new UriMatcher(UriMatcher.NO_MATCH));
        uriMatcher.addURI("authority", "first", 10);
        uriMatcher.addURI("authority", "second", 20);
        uriMatcher.addURI("authority", "third", 30);
        delegates = new SparseArray<>();
        delegates.put(10, mock(EntityProviderDelegate.class));
        delegates.put(20, mock(EntityProviderDelegate.class));
        delegates.put(30, mock(EntityProviderDelegate.class));
        contentProvider = spy(new TmdbContentProvider(uriMatcher, delegates));
        doReturn(dbHelper).when(contentProvider).createHelper(any(Context.class));
        ProviderInfo info = new ProviderInfo();
        info.authority = "authority";
        contentProvider.attachInfo(InstrumentationRegistry.getContext(), info);
    }

    @After
    public void tearDown() throws Exception
    {
        if (result != null)
        {
            result.close();
        }
    }

    private void verifyNonMatchesAreNotCalled() throws Exception
    {
        for (int nonMatch : parameter.nonMatches)
        {
            verify(delegates.get(nonMatch), never()).getType(any(Uri.class));
            verify(delegates.get(nonMatch), never()).query(
                    any(SQLiteDatabase.class),
                    any(Uri.class),
                    any(String[].class),
                    anyString(),
                    any(String[].class),
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString());
            verify(delegates.get(nonMatch), never()).insert(
                    any(SQLiteDatabase.class),
                    any(Uri.class),
                    any(ContentValues.class));
            verify(delegates.get(nonMatch), never()).bulkInsert(
                    any(SQLiteDatabase.class),
                    any(Uri.class),
                    any(ContentValues[].class));
            verify(delegates.get(nonMatch), never()).delete(
                    any(SQLiteDatabase.class),
                    any(Uri.class),
                    anyString(),
                    any(String[].class));
            verify(delegates.get(nonMatch), never()).update(
                    any(SQLiteDatabase.class),
                    any(Uri.class),
                    any(ContentValues.class),
                    anyString(),
                    any(String[].class));
        }
    }

    @Test
    public void getType_callsMatchingHelper() throws Exception
    {
        Uri uri = Uri.parse("content://authority/" + parameter.path);
        contentProvider.getType(uri);

        verify(uriMatcher).match(uri);
        verify(delegates.get(parameter.match)).getType(eq(uri));

        verifyNonMatchesAreNotCalled();
    }

    @Test
    public void query_callsMatchingHelper() throws Exception
    {
        Uri uri = Uri.parse("content://authority/" + parameter.path);
        result = contentProvider.query(uri,
                new String[]{"a"},
                "b",
                new String[]{"c"},
                "d");

        verify(uriMatcher).match(uri);
        verify(delegates.get(parameter.match)).query(
                eq(fakeReadable),
                eq(uri),
                eq(new String[]{"a"}),
                eq("b"),
                eq(new String[]{"c"}),
                eq((String) null),
                eq((String) null),
                eq("d"),
                eq((String) null));

        verifyNonMatchesAreNotCalled();
    }

    @Test
    public void insert_callsMatchingHelper() throws Exception
    {
        Uri uri = Uri.parse("content://authority/" + parameter.path);
        ContentValues values = new ContentValues();
        values.put("key", "value");
        contentProvider.insert(uri, values);

        verify(uriMatcher).match(uri);
        verify(delegates.get(parameter.match)).insert(
                eq(fakeWritable),
                eq(uri),
                eq(values));

        verifyNonMatchesAreNotCalled();
    }

    @Test
    public void bulkInsert_callsMatchingHelper() throws Exception
    {
        Uri uri = Uri.parse("content://authority/" + parameter.path);
        ContentValues value1 = new ContentValues();
        value1.put("key", "value");
        ContentValues[] values = new ContentValues[]{value1};
        contentProvider.bulkInsert(uri, values);

        verify(uriMatcher).match(uri);
        verify(delegates.get(parameter.match)).bulkInsert(
                eq(fakeWritable),
                eq(uri),
                eq(values));

        verifyNonMatchesAreNotCalled();
    }

    @Test
    public void delete_callsMatchingHelper() throws Exception
    {
        Uri uri = Uri.parse("content://authority/" + parameter.path);
        contentProvider.delete(uri, "a", new String[]{"b"});

        verify(uriMatcher).match(uri);
        verify(delegates.get(parameter.match)).delete(
                eq(fakeWritable),
                eq(uri),
                eq("a"),
                eq(new String[]{"b"}));

        verifyNonMatchesAreNotCalled();
    }

    @Test
    public void update_callsMatchingHelper() throws Exception
    {
        Uri uri = Uri.parse("content://authority/" + parameter.path);
        ContentValues values = new ContentValues();
        values.put("key", "value");
        contentProvider.update(uri, values, "a", new String[]{"b"});

        verify(uriMatcher).match(uri);
        verify(delegates.get(parameter.match)).update(
                eq(fakeWritable),
                eq(uri),
                eq(values),
                eq("a"),
                eq(new String[]{"b"}));

        verifyNonMatchesAreNotCalled();
    }
}