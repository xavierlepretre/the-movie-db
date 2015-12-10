package com.github.xavierlepretre.tmdb.model.people;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class CreditIdTest
{
    @Test
    public void equalsHashcode() throws Exception
    {
        EqualsVerifier.forClass(CreditId.class)
                .allFieldsShouldBeUsed()
                .verify();
    }
}