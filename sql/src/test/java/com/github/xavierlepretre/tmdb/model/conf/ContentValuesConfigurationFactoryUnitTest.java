package com.github.xavierlepretre.tmdb.model.conf;

import android.content.ContentValues;

import com.github.xavierlepretre.tmdb.model.conf.ConfigurationContract.ImagesConfSegment;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ContentValuesConfigurationFactoryUnitTest
{
    private ContentValuesConfigurationFactory factory;
    private ConfigurationDTO dto1;

    @Before
    public void setUp() throws Exception
    {
        factory = mock(ContentValuesConfigurationFactory.class);
        dto1 = new ConfigurationDTO(
                new ImagesConfDTO(
                        "http://image.tmdb.org/t/p/",
                        "https://image.tmdb.org/t/p/",
                        Arrays.asList(new ImageSize("w300"), new ImageSize("w780")),
                        Arrays.asList(new ImageSize("w45"), new ImageSize("w92")),
                        Arrays.asList(new ImageSize("w92"), new ImageSize("w154")),
                        Arrays.asList(new ImageSize("w45"), new ImageSize("w185")),
                        Arrays.asList(new ImageSize("w92"), new ImageSize("w185"))),
                Arrays.asList(new ChangeKey("adult"), new ChangeKey("air_date")));
    }

    @Test
    public void populate_works() throws Exception
    {
        ContentValues values = mock(ContentValues.class);
        doCallRealMethod().when(factory).populate(any(ContentValues.class), any(ConfigurationDTO.class));
        factory.populate(values, dto1);
        verify(values).put(
                eq(ConfigurationContract._ID),
                eq(ConfigurationContract.UNIQUE_ROW_ID));
        verify(values).put(
                eq(ImagesConfSegment.COLUMN_BASE_URL),
                eq("http://image.tmdb.org/t/p/"));
        verify(values).put(
                eq(ImagesConfSegment.COLUMN_SECURE_BASE_URL),
                eq("https://image.tmdb.org/t/p/"));
        verify(values).put(
                eq(ImagesConfSegment.COLUMN_BACKDROP_SIZES),
                eq("w300,w780"));
        verify(values).put(
                eq(ImagesConfSegment.COLUMN_LOGO_SIZES),
                eq("w45,w92"));
        verify(values).put(
                eq(ImagesConfSegment.COLUMN_POSTER_SIZES),
                eq("w92,w154"));
        verify(values).put(
                eq(ImagesConfSegment.COLUMN_PROFILE_SIZES),
                eq("w45,w185"));
        verify(values).put(
                eq(ImagesConfSegment.COLUMN_STILL_SIZES),
                eq("w92,w185"));
        verify(values).put(
                eq(ConfigurationContract.COLUMN_CHANGE_KEYS),
                eq("adult,air_date"));
    }

    @Test
    public void createSingle_callsPopulate() throws Exception
    {
        doCallRealMethod().when(factory).createFrom(any(ConfigurationDTO.class));
        factory.createFrom(dto1);
        verify(factory).populate(notNull(ContentValues.class), eq(dto1));
    }
}