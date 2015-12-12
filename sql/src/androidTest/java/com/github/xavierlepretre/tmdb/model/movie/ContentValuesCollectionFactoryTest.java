package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;

import com.github.xavierlepretre.tmdb.model.image.ImagePath;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Vector;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class ContentValuesCollectionFactoryTest
{
    private ContentValuesCollectionFactory factory;

    @Before
    public void setUp() throws Exception
    {
        factory = spy(new ContentValuesCollectionFactory());
    }

    @Test
    public void populate_works() throws Exception
    {
        ContentValues values = new ContentValues();
        CollectionDTO dto = new CollectionDTO(
                new ImagePath("/dOSECZImeyZldoq0ObieBE0lwie.jpg"),
                new CollectionId(645),
                "James Bond Collection",
                new ImagePath("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg"));
        factory.populate(values, dto);
        assertThat(values.getAsString(CollectionContract.COLUMN_BACKDROP_PATH)).isEqualTo("/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        assertThat(values.getAsLong(CollectionContract._ID)).isEqualTo(645);
        assertThat(values.getAsString(CollectionContract.COLUMN_NAME)).isEqualTo("James Bond Collection");
        assertThat(values.getAsString(CollectionContract.COLUMN_POSTER_PATH)).isEqualTo("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
    }

    @Test
    public void createSingle_works() throws Exception
    {
        CollectionDTO dto = new CollectionDTO(
                new ImagePath("/dOSECZImeyZldoq0ObieBE0lwie.jpg"),
                new CollectionId(645),
                "James Bond Collection",
                new ImagePath("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg"));
        ContentValues values = factory.createFrom(dto);
        assertThat(values.getAsString(CollectionContract.COLUMN_BACKDROP_PATH)).isEqualTo("/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        assertThat(values.getAsLong(CollectionContract._ID)).isEqualTo(645);
        assertThat(values.getAsString(CollectionContract.COLUMN_NAME)).isEqualTo("James Bond Collection");
        assertThat(values.getAsString(CollectionContract.COLUMN_POSTER_PATH)).isEqualTo("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
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
        Vector<ContentValues> values = factory.createFrom(Arrays.asList(dto1, dto2));
        assertThat(values.size()).isEqualTo(2);
        assertThat(values.get(0).getAsString(CollectionContract.COLUMN_BACKDROP_PATH)).isEqualTo("/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        assertThat(values.get(0).getAsLong(CollectionContract._ID)).isEqualTo(645);
        assertThat(values.get(0).getAsString(CollectionContract.COLUMN_NAME)).isEqualTo("James Bond Collection");
        assertThat(values.get(0).getAsString(CollectionContract.COLUMN_POSTER_PATH)).isEqualTo("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        assertThat(values.get(1).getAsString(CollectionContract.COLUMN_BACKDROP_PATH)).isEqualTo("/other_backdrop.jpg");
        assertThat(values.get(1).getAsLong(CollectionContract._ID)).isEqualTo(28);
        assertThat(values.get(1).getAsString(CollectionContract.COLUMN_NAME)).isEqualTo("Other collection");
        assertThat(values.get(1).getAsString(CollectionContract.COLUMN_POSTER_PATH)).isEqualTo("/other_poster.jpg");
    }
}