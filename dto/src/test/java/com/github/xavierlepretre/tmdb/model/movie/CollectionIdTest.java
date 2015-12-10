package com.github.xavierlepretre.tmdb.model.movie;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class CollectionIdTest
{
    @Test
    public void equalsHashcode() throws Exception
    {
        EqualsVerifier.forClass(CollectionId.class)
                .allFieldsShouldBeUsed()
                .verify();
    }
}