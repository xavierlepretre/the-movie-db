package com.github.xavierlepretre.tmdb.model.tag;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ListIdTest
{
    @Test
    public void equalsHashcode() throws Exception
    {
        EqualsVerifier.forClass(ListId.class)
                .allFieldsShouldBeUsed()
                .verify();
    }
}