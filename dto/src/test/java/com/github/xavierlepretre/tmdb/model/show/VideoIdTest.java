package com.github.xavierlepretre.tmdb.model.show;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class VideoIdTest
{
    @Test
    public void equalsHashcode() throws Exception
    {
        EqualsVerifier.forClass(VideoId.class)
                .allFieldsShouldBeUsed()
                .verify();
    }
}