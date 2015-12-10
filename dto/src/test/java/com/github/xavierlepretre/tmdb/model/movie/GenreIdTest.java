package com.github.xavierlepretre.tmdb.model.movie;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class GenreIdTest
{
    @Test
    public void equalsHashcode() throws Exception
    {
        EqualsVerifier.forClass(GenreId.class)
                .allFieldsShouldBeUsed()
                .verify();
    }
}