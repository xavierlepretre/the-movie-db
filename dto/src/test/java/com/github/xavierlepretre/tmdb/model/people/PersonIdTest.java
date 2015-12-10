package com.github.xavierlepretre.tmdb.model.people;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class PersonIdTest
{
    @Test
    public void equalsHashcode() throws Exception
    {
        EqualsVerifier.forClass(PersonId.class)
                .allFieldsShouldBeUsed()
                .verify();
    }
}