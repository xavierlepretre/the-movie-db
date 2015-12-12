package com.github.xavierlepretre.tmdb.model.image;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ImagePathTest
{
    @Test
    public void equalsHashcode() throws Exception
    {
        EqualsVerifier.forClass(ImagePath.class)
                .allFieldsShouldBeUsed()
                .verify();
    }
}