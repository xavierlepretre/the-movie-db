package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ContentValuesGenreFactoryUnitTest
{
    private ContentValuesGenreFactory factory;

    @Before
    public void setUp() throws Exception
    {
        factory = mock(ContentValuesGenreFactory.class);
    }

    @Test
    public void populate_works() throws Exception
    {
        ContentValues values = mock(ContentValues.class);
        GenreDTO dto = new GenreDTO(new GenreId(12), "Adventure");
        doCallRealMethod().when(factory).populate(any(ContentValues.class), any(GenreDTO.class));
        factory.populate(values, dto);
        verify(values).put(
                eq(GenreContract._ID),
                eq(12));
        verify(values).put(
                eq(GenreContract.COLUMN_NAME),
                eq("Adventure"));
    }

    @Test
    public void createSingle_callsPopulate() throws Exception
    {
        GenreDTO dto = new GenreDTO(new GenreId(12), "Adventure");
        doCallRealMethod().when(factory).createFrom(any(GenreDTO.class));
        factory.createFrom(dto);
        verify(factory).populate(notNull(ContentValues.class), eq(dto));
    }

    @Test
    public void createVectorFromCollection_callsSingle() throws Exception
    {
        GenreDTO dto1 = new GenreDTO(new GenreId(12), "Adventure");
        GenreDTO dto2 = new GenreDTO(new GenreId(28), "Action");
        doCallRealMethod().when(factory).createFrom(anyCollectionOf(GenreDTO.class));
        factory.createFrom(Arrays.asList(dto1, dto2));
        verify(factory).createFrom(eq(dto1));
        verify(factory).createFrom(eq(dto2));
    }

    @Test
    public void createVectorFromGenreList_callsFromCollection() throws Exception
    {
        GenreDTO dto1 = new GenreDTO(new GenreId(12), "Adventure");
        GenreDTO dto2 = new GenreDTO(new GenreId(28), "Action");
        GenreListDTO listDTO = new GenreListDTO(Arrays.asList(dto1, dto2));
        doCallRealMethod().when(factory).createFrom(any(GenreListDTO.class));
        factory.createFrom(listDTO);
        verify(factory).createFrom(eq(listDTO.getGenres()));
    }
}