package com.github.xavierlepretre.tmdb.model.rate;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ReviewIdTest
{
    @Test
    public void equalsHashcode() throws Exception
    {
        EqualsVerifier.forClass(ReviewId.class)
                .allFieldsShouldBeUsed()
                .verify();
    }
}