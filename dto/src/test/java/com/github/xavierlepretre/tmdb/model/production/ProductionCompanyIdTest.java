package com.github.xavierlepretre.tmdb.model.production;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ProductionCompanyIdTest
{
    @Test
    public void equalsHashcode() throws Exception
    {
        EqualsVerifier.forClass(ProductionCompanyId.class)
                .allFieldsShouldBeUsed()
                .verify();
    }
}