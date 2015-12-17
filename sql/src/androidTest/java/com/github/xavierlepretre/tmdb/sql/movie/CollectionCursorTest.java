package com.github.xavierlepretre.tmdb.sql.movie;

import android.database.MatrixCursor;

import com.github.xavierlepretre.tmdb.model.image.ImagePath;
import com.github.xavierlepretre.tmdb.model.movie.Collection;
import com.github.xavierlepretre.tmdb.model.movie.CollectionContract;
import com.github.xavierlepretre.tmdb.model.movie.CollectionId;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class CollectionCursorTest
{
    private static final String[] COLUMNS = new String[]{
            CollectionContract.COLUMN_BACKDROP_PATH,
            CollectionContract._ID,
            CollectionContract.COLUMN_NAME,
            CollectionContract.COLUMN_POSTER_PATH
    };
    private static final String[] VALUES = new String[]{
            "/a123.jpg",
            "23",
            "Action",
            "/b456.jpg"
    };

    @Test
    public void mayCreateCollection() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(VALUES);
        CollectionCursor entityCursor = new CollectionCursor(cursor);
        entityCursor.moveToFirst();

        Collection collection = entityCursor.getCollection();
        assertThat(collection.getBackdropPath()).isEqualTo(new ImagePath("/a123.jpg"));
        assertThat(collection.getId()).isEqualTo(new CollectionId(23));
        assertThat(collection.getName()).isEqualTo("Action");
        assertThat(collection.getPosterPath()).isEqualTo(new ImagePath("/b456.jpg"));
    }

    @Test
    public void mayCreateCollectionWithMissing() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(new String[0]);
        cursor.addRow(new String[0]);
        CollectionCursor entityCursor = new CollectionCursor(cursor);
        entityCursor.moveToFirst();

        Collection collection = entityCursor.getCollection();
        assertThat(collection.getBackdropPath()).isNull();
        assertThat(collection.getId()).isNull();
        assertThat(collection.getName()).isNull();
        assertThat(collection.getPosterPath()).isNull();
    }

    @Test
    public void mayCreateCollectionWithNulls() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(new String[COLUMNS.length]);
        CollectionCursor entityCursor = new CollectionCursor(cursor);
        entityCursor.moveToFirst();

        Collection collection = entityCursor.getCollection();
        assertThat(collection.getBackdropPath()).isNull();
        assertThat(collection.getId()).isNull();
        assertThat(collection.getName()).isNull();
        assertThat(collection.getPosterPath()).isNull();
    }
}