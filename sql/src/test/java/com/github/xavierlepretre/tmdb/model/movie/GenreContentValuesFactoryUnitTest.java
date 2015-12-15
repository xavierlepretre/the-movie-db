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

public class GenreContentValuesFactoryUnitTest
{
    private GenreContentValuesFactory factory;

    @Before
    public void setUp() throws Exception
    {
        factory = mock(GenreContentValuesFactory.class);
    }

    @Test
    public void populateDto_works() throws Exception
    {
        ContentValues values = mock(ContentValues.class);
        GenreDTO dto = new GenreDTO(new GenreId(12), "Adventure");
        doCallRealMethod().when(factory).populate(any(ContentValues.class), any(GenreDTO.class));
        doCallRealMethod().when(factory).populate(any(ContentValues.class), any(GenreId.class));
        factory.populate(values, dto);
        verify(values).put(
                eq(GenreContract._ID),
                eq(12));
        verify(values).put(
                eq(GenreContract.COLUMN_NAME),
                eq("Adventure"));
    }

    @Test
    public void createSingleFromDto_callsPopulate() throws Exception
    {
        GenreDTO dto = new GenreDTO(new GenreId(12), "Adventure");
        doCallRealMethod().when(factory).createFrom(any(GenreDTO.class));
        factory.createFrom(dto);
        verify(factory).populate(notNull(ContentValues.class), eq(dto));
    }

    @Test
    public void createVectorFromDtoCollection_callsSingle() throws Exception
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

    @Test
    public void populateId_works() throws Exception
    {
        ContentValues values = mock(ContentValues.class);
        GenreId id = new GenreId(12);
        doCallRealMethod().when(factory).populate(any(ContentValues.class), any(GenreId.class));
        factory.populate(values, id);
        verify(values).put(
                eq(GenreContract._ID),
                eq(12));
    }

    @Test
    public void createSingleFromId_callsPopulate() throws Exception
    {
        GenreId id = new GenreId(12);
        doCallRealMethod().when(factory).createFrom(any(GenreId.class));
        factory.createFrom(id);
        verify(factory).populate(notNull(ContentValues.class), eq(id));
    }

    @Test
    public void createVectorFromIdCollection_callsSingle() throws Exception
    {
        GenreId id1 = new GenreId(12);
        GenreId id2 = new GenreId(28);
        doCallRealMethod().when(factory).createFrom(anyCollectionOf(GenreId.class), any(GenreId.class));
        factory.createFrom(Arrays.asList(id1, id2), null);
        verify(factory).createFrom(eq(id1));
        verify(factory).createFrom(eq(id2));
    }
}