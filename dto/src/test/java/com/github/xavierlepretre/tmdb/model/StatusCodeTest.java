package com.github.xavierlepretre.tmdb.model;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class StatusCodeTest
{
    @Test
    public void equalsHashcode() throws Exception
    {
        EqualsVerifier.forClass(StatusCode.class)
                .allFieldsShouldBeUsed()
                .verify();
    }
}