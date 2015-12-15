package com.github.xavierlepretre.tmdb.model.conf;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.conf.ConfigurationContract.ImagesConfSegment;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class ConfigurationContentValuesFactoryTest
{
    private ConfigurationContentValuesFactory factory;
    private ConfigurationDTO dto1;

    @Before
    public void setUp() throws Exception
    {
        factory = spy(new ConfigurationContentValuesFactory());
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

    private void assertValuesAreCorrect(@NonNull ContentValues values)
    {
        assertThat(values.getAsString(ImagesConfSegment.COLUMN_BASE_URL)).isEqualTo("http://image.tmdb.org/t/p/");
        assertThat(values.getAsString(ImagesConfSegment.COLUMN_SECURE_BASE_URL)).isEqualTo("https://image.tmdb.org/t/p/");
        assertThat(values.getAsString(ImagesConfSegment.COLUMN_BACKDROP_SIZES)).isEqualTo("w300,w780");
        assertThat(values.getAsString(ImagesConfSegment.COLUMN_LOGO_SIZES)).isEqualTo("w45,w92");
        assertThat(values.getAsString(ImagesConfSegment.COLUMN_POSTER_SIZES)).isEqualTo("w92,w154");
        assertThat(values.getAsString(ImagesConfSegment.COLUMN_PROFILE_SIZES)).isEqualTo("w45,w185");
        assertThat(values.getAsString(ImagesConfSegment.COLUMN_STILL_SIZES)).isEqualTo("w92,w185");
        assertThat(values.getAsString(ConfigurationContract.COLUMN_CHANGE_KEYS)).isEqualTo("adult,air_date");
    }

    @Test
    public void populate_works() throws Exception
    {
        ContentValues values = new ContentValues();
        factory.populate(values, dto1);
        assertValuesAreCorrect(values);
    }

    @Test
    public void createSingle_works() throws Exception
    {
        ContentValues values = factory.createFrom(dto1);
        assertValuesAreCorrect(values);
    }
}