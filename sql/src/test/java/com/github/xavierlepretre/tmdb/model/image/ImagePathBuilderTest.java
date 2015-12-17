package com.github.xavierlepretre.tmdb.model.image;

import com.github.xavierlepretre.tmdb.model.conf.Configuration;
import com.github.xavierlepretre.tmdb.model.conf.ImagesConf;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class ImagePathBuilderTest
{
    private ImagePathBuilder imagePathBuilder;

    @Before
    public void setUp() throws Exception
    {
        this.imagePathBuilder = spy(new ImagePathBuilder());
    }

    @Test
    public void getUrlOnString_isByConcatenation() throws Exception
    {
        assertThat(imagePathBuilder.getUrl(
                "http://fake.com/",
                new ImageSize("w200"),
                new ImagePath("/path.jpg"))).isEqualTo("http://fake.com/w200/path.jpg");
    }

    @Test
    public void getSecureUrlOnString_isByConcatenation() throws Exception
    {
        assertThat(imagePathBuilder.getSecureUrl(
                "https://fake.com/",
                new ImageSize("w200"),
                new ImagePath("/path.jpg"))).isEqualTo("https://fake.com/w200/path.jpg");
    }

    @Test(expected = NullPointerException.class)
    public void getUrl_noBaseUrl_fails() throws Exception
    {
        imagePathBuilder.getUrl(
                new ImagesConf(null, "fake", null, null, null, null, null),
                new ImageSize("w200"),
                new ImagePath("/path.jpg"));
    }

    @Test(expected = NullPointerException.class)
    public void getUrl_noSecureBaseUrl_fails() throws Exception
    {
        imagePathBuilder.getSecureUrl(
                new ImagesConf("fake", null, null, null, null, null, null),
                new ImageSize("w200"),
                new ImagePath("/path.jpg"));
    }

    @Test
    public void getUrl_isByConcatenation() throws Exception
    {
        assertThat(imagePathBuilder.getUrl(
                new ImagesConf("http://fake.com/", null, null, null, null, null, null),
                new ImageSize("w200"),
                new ImagePath("/path.jpg"))).isEqualTo("http://fake.com/w200/path.jpg");
    }

    @Test
    public void getSecureUrl_isByConcatenation() throws Exception
    {
        assertThat(imagePathBuilder.getSecureUrl(
                new ImagesConf(null, "https://fake.com/", null, null, null, null, null),
                new ImageSize("w200"),
                new ImagePath("/path.jpg"))).isEqualTo("https://fake.com/w200/path.jpg");
    }

    @Test
    public void getUrlOnConf_isByConcatenation() throws Exception
    {
        assertThat(imagePathBuilder.getUrl(
                new Configuration(new ImagesConf("http://fake.com/", null, null, null, null, null, null),
                        null),
                new ImageSize("w200"),
                new ImagePath("/path.jpg"))).isEqualTo("http://fake.com/w200/path.jpg");
    }

    @Test
    public void getSecureUrlOnConf_isByConcatenation() throws Exception
    {
        assertThat(imagePathBuilder.getSecureUrl(
                new Configuration(new ImagesConf(null, "https://fake.com/", null, null, null, null, null),
                        null),
                new ImageSize("w200"),
                new ImagePath("/path.jpg"))).isEqualTo("https://fake.com/w200/path.jpg");
    }
}