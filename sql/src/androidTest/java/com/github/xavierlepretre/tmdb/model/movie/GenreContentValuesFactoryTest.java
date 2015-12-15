package com.github.xavierlepretre.tmdb.model.movie;

import org.junit.Before;
import org.junit.Test;

import android.content.ContentValues;

import java.util.Arrays;
import java.util.Vector;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class GenreContentValuesFactoryTest
{
    private GenreContentValuesFactory factory;

    @Before
    public void setUp() throws Exception
    {
        factory = spy(new GenreContentValuesFactory());
    }

    @Test
    public void populateFromDto_works() throws Exception
    {
        ContentValues values = new ContentValues();
        GenreDTO dto = new GenreDTO(new GenreId(12), "Adventure");
        factory.populate(values, dto);
        assertThat(values.size()).isEqualTo(2);
        assertThat(values.getAsInteger(GenreContract._ID)).isEqualTo(12);
        assertThat(values.getAsString(GenreContract.COLUMN_NAME)).isEqualTo("Adventure");
    }

    @Test
    public void createFromSingleDto_works() throws Exception
    {
        GenreDTO dto = new GenreDTO(new GenreId(12), "Adventure");
        ContentValues values = factory.createFrom(dto);
        assertThat(values.size()).isEqualTo(2);
        assertThat(values.getAsInteger(GenreContract._ID)).isEqualTo(12);
        assertThat(values.getAsString(GenreContract.COLUMN_NAME)).isEqualTo("Adventure");
    }

    @Test
    public void createVectorFromDtoCollection_callsSingle() throws Exception
    {
        GenreDTO dto1 = new GenreDTO(new GenreId(12), "Adventure");
        GenreDTO dto2 = new GenreDTO(new GenreId(28), "Action");
        Vector<ContentValues> values = factory.createFrom(Arrays.asList(dto1, dto2));
        assertThat(values.size()).isEqualTo(2);
        assertThat(values.get(0).size()).isEqualTo(2);
        assertThat(values.get(0).getAsInteger(GenreContract._ID)).isEqualTo(12);
        assertThat(values.get(0).getAsString(GenreContract.COLUMN_NAME)).isEqualTo("Adventure");
        assertThat(values.get(1).size()).isEqualTo(2);
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

    @Test
    public void populateFromId_works() throws Exception
    {
        ContentValues values = new ContentValues();
        GenreId id = new GenreId(12);
        factory.populate(values, id);
        assertThat(values.size()).isEqualTo(1);
        assertThat(values.getAsInteger(GenreContract._ID)).isEqualTo(12);
    }

    @Test
    public void createFromSingleId_works() throws Exception
    {
        GenreId id = new GenreId(12);
        ContentValues values = factory.createFrom(id);
        assertThat(values.size()).isEqualTo(1);
        assertThat(values.getAsInteger(GenreContract._ID)).isEqualTo(12);
    }

    @Test
    public void createVectorFromIdCollection_callsSingle() throws Exception
    {
        GenreId dto1 = new GenreId(12);
        GenreId dto2 = new GenreId(28);
        Vector<ContentValues> values = factory.createFrom(Arrays.asList(dto1, dto2), null);
        assertThat(values.size()).isEqualTo(2);
        assertThat(values.get(0).size()).isEqualTo(1);
        assertThat(values.get(0).getAsInteger(GenreContract._ID)).isEqualTo(12);
        assertThat(values.get(1).size()).isEqualTo(1);
        assertThat(values.get(1).getAsInteger(GenreContract._ID)).isEqualTo(28);
    }
}