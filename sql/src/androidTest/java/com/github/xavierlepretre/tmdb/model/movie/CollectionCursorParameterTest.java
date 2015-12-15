package com.github.xavierlepretre.tmdb.model.movie;

import android.database.MatrixCursor;

import com.github.xavierlepretre.tmdb.model.ParameterColumnValue;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class CollectionCursorParameterTest
{
    private static final String[][] POTENTIAL_COLUMNS = new String[][]{
            new String[]{CollectionContract.COLUMN_BACKDROP_PATH, "/a123.jpg"},
            new String[]{CollectionContract._ID, "23"},
            new String[]{CollectionContract.COLUMN_NAME, "Action"},
            new String[]{CollectionContract.COLUMN_POSTER_PATH, "/b456.jpg"}
    };

    @Parameterized.Parameter
    public ParameterColumnValue parameter;

    @Parameterized.Parameters()
    public static ParameterColumnValue[] getParameters()
    {
        return ParameterColumnValue.getPossibleParameters(POTENTIAL_COLUMNS);
    }

    @Test
    public void mayCreateCollectionWithNulls() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(parameter.columns.toArray(new String[parameter.columns.size()]));
        for (List<String> row : parameter.rows)
        {
            cursor.addRow(row);
        }
        CollectionCursor entityCursor = new CollectionCursor(cursor);
        entityCursor.moveToFirst();

        Collection collection = entityCursor.getCollection();
        assertThat(collection.getBackdropPath()).isEqualTo(
                parameter.columns.contains(CollectionContract.COLUMN_BACKDROP_PATH)
                        ? new ImagePath("/a123.jpg")
                        : null);
        assertThat(collection.getId()).isEqualTo(
                parameter.columns.contains(CollectionContract._ID)
                        ? new CollectionId(23)
                        : null);
        assertThat(collection.getName()).isEqualTo(
                parameter.columns.contains(CollectionContract.COLUMN_NAME)
                        ? "Action"
                        : null);
        assertThat(collection.getPosterPath()).isEqualTo(
                parameter.columns.contains(CollectionContract.COLUMN_POSTER_PATH)
                        ? new ImagePath("/b456.jpg")
                        : null);
    }
}