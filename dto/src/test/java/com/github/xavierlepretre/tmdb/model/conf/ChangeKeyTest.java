package com.github.xavierlepretre.tmdb.model.conf;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ChangeKeyTest
{
    @Test
    public void equalsHashcode() throws Exception
    {
        EqualsVerifier.forClass(ChangeKey.class)
                .allFieldsShouldBeUsed()
                .verify();
    }
}