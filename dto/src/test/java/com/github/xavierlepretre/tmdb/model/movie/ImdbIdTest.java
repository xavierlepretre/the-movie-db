package com.github.xavierlepretre.tmdb.model.movie;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ImdbIdTest
{
    @Test
    public void equalsHashcode() throws Exception
    {
        EqualsVerifier.forClass(ImdbId.class)
                .allFieldsShouldBeUsed()
                .verify();
    }
}