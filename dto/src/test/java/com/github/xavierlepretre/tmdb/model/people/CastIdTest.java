package com.github.xavierlepretre.tmdb.model.people;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class CastIdTest
{
    @Test
    public void equalsHashcode() throws Exception
    {
        EqualsVerifier.forClass(CastId.class)
                .allFieldsShouldBeUsed()
                .verify();
    }
}