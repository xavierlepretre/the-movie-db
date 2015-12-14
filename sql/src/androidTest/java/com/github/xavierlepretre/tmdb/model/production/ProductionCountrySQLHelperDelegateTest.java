package com.github.xavierlepretre.tmdb.model.production;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ProductionCountrySQLHelperDelegateTest
{
    @Test
    public void createRequestIsCorrect() throws Exception
    {
        assertThat(new ProductionCountrySQLHelperDelegate().getCreateQuery()).isEqualTo(
                "CREATE TABLE productionCountry(_id CHARACTER(2) PRIMARY KEY NOT NULL,name TEXT NULL);");
    }

    @Test
    public void upgradeRequestIsCorrect() throws Exception
    {
        assertThat(new ProductionCountrySQLHelperDelegate().getUpgradeQuery(1, 2)).isEqualTo(
                "DROP TABLE IF EXISTS productionCountry;"
        );
    }
}