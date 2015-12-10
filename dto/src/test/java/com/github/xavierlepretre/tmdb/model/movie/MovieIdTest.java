package com.github.xavierlepretre.tmdb.model.movie;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class MovieIdTest
{
    @Test
    public void equalsHashcode() throws Exception
    {
        EqualsVerifier.forClass(MovieId.class)
                .allFieldsShouldBeUsed()
                .verify();
    }
}