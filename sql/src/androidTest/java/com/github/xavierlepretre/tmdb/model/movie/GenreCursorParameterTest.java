package com.github.xavierlepretre.tmdb.model.movie;

import android.database.MatrixCursor;

import com.github.xavierlepretre.tmdb.model.ParameterColumnValue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class GenreCursorParameterTest
{
    private static final String[][] POTENTIAL_COLUMNS = new String[][]{
            new String[]{GenreContract._ID, "23"},
            new String[]{GenreContract.COLUMN_NAME, "Action"}
    };

    @Parameterized.Parameter
    public ParameterColumnValue parameter;

    @Parameterized.Parameters()
    public static ParameterColumnValue[] getParameters()
    {
        return ParameterColumnValue.getPossibleParameters(POTENTIAL_COLUMNS);
    }

    @Test
    public void mayCreateGenreWithNulls() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(parameter.columns.toArray(new String[parameter.columns.size()]));
        for (List<String> row : parameter.rows)
        {
            cursor.addRow(row);
        }
        GenreCursor entityCursor = new GenreCursor(cursor);
        entityCursor.moveToFirst();

        Genre genre = entityCursor.getGenre();
        assertThat(genre.getId()).isEqualTo(
                parameter.columns.contains(GenreContract._ID)
                        ? new GenreId(23)
                        : null);
        assertThat(genre.getName()).isEqualTo(
                parameter.columns.contains(GenreContract.COLUMN_NAME)
                        ? "Action"
                        : null);
    }
}