package com.github.xavierlepretre.tmdb.model.image;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.fest.assertions.api.Assertions.assertThat;

public class ImageSizeTest
{
    @Test
    public void equalsHashcode() throws Exception
    {
        EqualsVerifier.forClass(ImageSize.class)
                .allFieldsShouldBeUsed()
                .verify();
    }

    @Test
    public void getWidth_isOk() throws Exception
    {
        assertThat(new ImageSize("w1").getWidth()).isEqualTo(1);
        assertThat(new ImageSize("w200").getWidth()).isEqualTo(200);
    }

    @Test(expected = NumberFormatException.class)
    public void getWidth_cannotGetWidth() throws Exception
    {
        new ImageSize("h200").getWidth();
    }
}