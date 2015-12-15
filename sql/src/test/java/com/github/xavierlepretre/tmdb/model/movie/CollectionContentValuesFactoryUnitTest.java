package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;

import com.github.xavierlepretre.tmdb.model.image.ImagePath;

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

public class CollectionContentValuesFactoryUnitTest
{
    private CollectionContentValuesFactory factory;

    @Before
    public void setUp() throws Exception
    {
        factory = mock(CollectionContentValuesFactory.class);
    }

    @Test
    public void populate_works() throws Exception
    {
        ContentValues values = mock(ContentValues.class);
        CollectionDTO dto = new CollectionDTO(
                new ImagePath("/dOSECZImeyZldoq0ObieBE0lwie.jpg"),
                new CollectionId(645),
                "James Bond Collection",
                new ImagePath("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg"));
        doCallRealMethod().when(factory).populate(any(ContentValues.class), any(CollectionDTO.class));
        factory.populate(values, dto);
        verify(values).put(
                eq(CollectionContract.COLUMN_BACKDROP_PATH),
                eq("/dOSECZImeyZldoq0ObieBE0lwie.jpg"));
        verify(values).put(
                eq(CollectionContract._ID),
                eq(645L));
        verify(values).put(
                eq(CollectionContract.COLUMN_NAME),
                eq("James Bond Collection"));
        verify(values).put(
                eq(CollectionContract.COLUMN_POSTER_PATH),
                eq("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg"));
    }

    @Test
    public void createSingle_callsPopulate() throws Exception
    {
        CollectionDTO dto = new CollectionDTO(
                new ImagePath("/dOSECZImeyZldoq0ObieBE0lwie.jpg"),
                new CollectionId(645),
                "James Bond Collection",
                new ImagePath("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg"));
        doCallRealMethod().when(factory).createFrom(any(CollectionDTO.class));
        factory.createFrom(dto);
        verify(factory).populate(notNull(ContentValues.class), eq(dto));
    }

    @Test
    public void createVectorFromCollection_callsSingle() throws Exception
    {
        CollectionDTO dto1 = new CollectionDTO(
                new ImagePath("/dOSECZImeyZldoq0ObieBE0lwie.jpg"),
                new CollectionId(645),
                "James Bond Collection",
                new ImagePath("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg"));
        CollectionDTO dto2 = new CollectionDTO(
                new ImagePath("/other_backdrop.jpg"),
                new CollectionId(28),
                "Other collection",
                new ImagePath("/other_poster.jpg"));
        doCallRealMethod().when(factory).createFrom(anyCollectionOf(CollectionDTO.class));
        factory.createFrom(Arrays.asList(dto1, dto2));
        verify(factory).createFrom(eq(dto1));
        verify(factory).createFrom(eq(dto2));
    }
}