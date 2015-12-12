package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.ParameterColumnValue;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RunWith(Parameterized.class)
public class CollectionCursorWithProviderTest
{
    private static final String TEMP_DB_NAME = "temp.collection.db";
    private static final String[][] POTENTIAL_COLUMNS = new String[][]{
            new String[]{CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg", "/other_backdrop.jpg"},
            new String[]{CollectionContract._ID, "3", "4"},
            new String[]{CollectionContract.COLUMN_NAME, "James Bond Collection", "Other Collection"},
            new String[]{CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg", "/other_poster.jpg"}
    };

    private CollectionProviderDelegate providerDelegate;
    private CollectionSQLiteOpenHelper sqlHelper;

    @Parameterized.Parameter
    public ParameterColumnValue parameter;

    @Parameterized.Parameters()
    public static ParameterColumnValue[] getParameters()
    {
        return ParameterColumnValue.getPossibleParameters(POTENTIAL_COLUMNS);
    }

    @Before
    public void setUp() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
        providerDelegate = spy(new CollectionProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/collection"),
                "dir_type",
                "item_type"));
        sqlHelper = new CollectionSQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1);

        ContentValues values1 = new ContentValues();
        ContentValues values2 = new ContentValues();
        for (String[] pair : POTENTIAL_COLUMNS)
        {
            values1.put(pair[0], pair[1]);
            values2.put(pair[0], pair[2]);
        }
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                new ContentValues[]{values1, values2});
    }

    @After
    public void tearDown() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
    }

    @Test
    public void queryListFromDb_isOk() throws Exception
    {
        //noinspection ConstantConditions
        CollectionCursor found = new CollectionCursor(providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection"),
                parameter.columns.toArray(new String[parameter.columns.size()]),
                null, null, null, null,
                CollectionContract._ID + " ASC", null));

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        Collection collection = found.getCollection();
        assertThat(collection.getBackdropPath()).isEqualTo(
                (parameter.columns.contains(CollectionContract.COLUMN_BACKDROP_PATH) || parameter.columns.size() == 0)
                        ? new ImagePath("/dOSECZImeyZldoq0ObieBE0lwie.jpg")
                        : null);
        assertThat(collection.getId()).isEqualTo(
                (parameter.columns.contains(CollectionContract._ID) || parameter.columns.size() == 0)
                        ? new CollectionId(3)
                        : null);
        assertThat(collection.getName()).isEqualTo(
                (parameter.columns.contains(CollectionContract.COLUMN_NAME) || parameter.columns.size() == 0)
                        ? "James Bond Collection"
                        : null);
        assertThat(collection.getPosterPath()).isEqualTo(
                (parameter.columns.contains(CollectionContract.COLUMN_POSTER_PATH) || parameter.columns.size() == 0)
                        ? new ImagePath("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg")
                        : null);
        assertThat(found.moveToNext()).isTrue();
        collection = found.getCollection();
        assertThat(collection.getBackdropPath()).isEqualTo(
                (parameter.columns.contains(CollectionContract.COLUMN_BACKDROP_PATH) || parameter.columns.size() == 0)
                        ? new ImagePath("/other_backdrop.jpg")
                        : null);
        assertThat(collection.getId()).isEqualTo(
                (parameter.columns.contains(CollectionContract._ID) || parameter.columns.size() == 0)
                        ? new CollectionId(4)
                        : null);
        assertThat(collection.getName()).isEqualTo(
                (parameter.columns.contains(CollectionContract.COLUMN_NAME) || parameter.columns.size() == 0)
                        ? "Other Collection"
                        : null);
        assertThat(collection.getPosterPath()).isEqualTo(
                (parameter.columns.contains(CollectionContract.COLUMN_POSTER_PATH) || parameter.columns.size() == 0)
                        ? new ImagePath("/other_poster.jpg")
                        : null);
        assertThat(found.moveToNext()).isFalse();
    }
}
