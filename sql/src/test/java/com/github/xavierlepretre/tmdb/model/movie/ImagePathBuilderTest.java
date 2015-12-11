package com.github.xavierlepretre.tmdb.model.movie;

import com.github.xavierlepretre.tmdb.model.conf.Configuration;
import com.github.xavierlepretre.tmdb.model.conf.ImageSize;
import com.github.xavierlepretre.tmdb.model.conf.ImagesConf;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ImagePathBuilderTest
{
    @Test(expected = NullPointerException.class)
    public void getUrl_noBaseUrl_fails() throws Exception
    {
        new ImagePathBuilder().getUrl(
                new Configuration(new ImagesConf(null, "fake", null, null, null, null, null),
                        null),
                new ImageSize("w200"),
                "/path.jpg");
    }

    @Test(expected = NullPointerException.class)
    public void getUrl_noSecureBaseUrl_fails() throws Exception
    {
        new ImagePathBuilder().getSecureUrl(
                new Configuration(new ImagesConf("fake", null, null, null, null, null, null),
                        null),
                new ImageSize("w200"),
                "/path.jpg");
    }

    @Test
    public void getUrl_isByConcatenation() throws Exception
    {
        assertThat(new ImagePathBuilder().getUrl(
                new Configuration(new ImagesConf("http://fake.com/", null, null, null, null, null, null),
                        null),
                new ImageSize("w200"),
                "/path.jpg")).isEqualTo("http://fake.com/w200/path.jpg");
    }

    @Test
    public void getSecureUrl_isByConcatenation() throws Exception
    {
        assertThat(new ImagePathBuilder().getSecureUrl(
                new Configuration(new ImagesConf(null ,"https://fake.com/", null, null, null, null, null),
                        null),
                new ImageSize("w200"),
                "/path.jpg")).isEqualTo("https://fake.com/w200/path.jpg");
    }
}