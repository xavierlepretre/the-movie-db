package com.github.xavierlepretre.tmdb.model.tag;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class KeywordIdTest
{
    @Test
    public void equalsHashcode() throws Exception
    {
        EqualsVerifier.forClass(KeywordId.class)
                .allFieldsShouldBeUsed()
                .verify();
    }
}