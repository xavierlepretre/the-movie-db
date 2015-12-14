package com.github.xavierlepretre.tmdb.model.production;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ProductionCompanySQLHelperDelegateTest
{
    @Test
    public void createRequestIsCorrect() throws Exception
    {
        assertThat(new ProductionCompanySQLHelperDelegate().getCreateQuery()).isEqualTo(
                "CREATE TABLE productionCompany(_id INTEGER PRIMARY KEY NOT NULL,name TEXT NULL);");
    }

    @Test
    public void upgradeRequestIsCorrect() throws Exception
    {
        assertThat(new ProductionCompanySQLHelperDelegate().getUpgradeQuery(1, 2)).isEqualTo(
                "DROP TABLE IF EXISTS productionCompany;"
        );
    }
}