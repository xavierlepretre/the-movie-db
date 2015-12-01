package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Vector;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class ContentValuesGenreFactoryTest
{
    private ContentValuesGenreFactory factory;

    @Before
    public void setUp() throws Exception
    {
        factory = spy(new ContentValuesGenreFactory());
    }

    @Test
    public void populate_works() throws Exception
    {
        ContentValues values = new ContentValues();
        GenreDTO dto = new GenreDTO(new GenreId(12), "Adventure");
        factory.populate(values, dto);
        assertThat(values.getAsInteger(GenreContract._ID)).isEqualTo(12);
        assertThat(values.getAsString(GenreContract.COLUMN_NAME)).isEqualTo("Adventure");
    }

    @Test
    public void createSingle_works() throws Exception
    {
        GenreDTO dto = new GenreDTO(new GenreId(12), "Adventure");
        ContentValues values = factory.createFrom(dto);
        assertThat(values.getAsInteger(GenreContract._ID)).isEqualTo(12);
        assertThat(values.getAsString(GenreContract.COLUMN_NAME)).isEqualTo("Adventure");
    }

    @Test
    public void createVectorFromCollection_callsSingle() throws Exception
    {
        GenreDTO dto1 = new GenreDTO(new GenreId(12), "Adventure");
        GenreDTO dto2 = new GenreDTO(new GenreId(28), "Action");
        Vector<ContentValues> values = factory.createFrom(Arrays.asList(dto1, dto2));
        assertThat(values.size()).isEqualTo(2);
        assertThat(values.get(0).getAsInteger(GenreContract._ID)).isEqualTo(12);
        assertThat(values.get(0).getAsString(GenreContract.COLUMN_NAME)).isEqualTo("Adventure");
        assertThat(values.get(1).getAsInteger(GenreContract._ID)).isEqualTo(28);
        assertThat(values.get(1).getAsString(GenreContract.COLUMN_NAME)).isEqualTo("Action");
    }

    @Test
    public void createVectorFromGenreList_callsFromCollection() throws Exception
    {
        GenreDTO dto1 = new GenreDTO(new GenreId(12), "Adventure");
        GenreDTO dto2 = new GenreDTO(new GenreId(28), "Action");
        GenreListDTO listDTO = new GenreListDTO(Arrays.asList(dto1, dto2));
        Vector<ContentValues> values = factory.createFrom(listDTO);
        assertThat(values.size()).isEqualTo(2);
        assertThat(values.get(0).getAsInteger(GenreContract._ID)).isEqualTo(12);
        assertThat(values.get(0).getAsString(GenreContract.COLUMN_NAME)).isEqualTo("Adventure");
        assertThat(values.get(1).getAsInteger(GenreContract._ID)).isEqualTo(28);
        assertThat(values.get(1).getAsString(GenreContract.COLUMN_NAME)).isEqualTo("Action");
    }
}