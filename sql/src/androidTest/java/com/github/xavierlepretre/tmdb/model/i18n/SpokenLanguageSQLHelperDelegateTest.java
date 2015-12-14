package com.github.xavierlepretre.tmdb.model.i18n;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class SpokenLanguageSQLHelperDelegateTest
{
    @Test
    public void createRequestIsCorrect() throws Exception
    {
        assertThat(new SpokenLanguageSQLHelperDelegate().getCreateQuery()).isEqualTo(
                "CREATE TABLE spokenLanguage(_id CHARACTER(2) PRIMARY KEY NOT NULL,name TEXT NULL);");
    }

    @Test
    public void upgradeRequestIsCorrect() throws Exception
    {
        assertThat(new SpokenLanguageSQLHelperDelegate().getUpgradeQuery(1, 2)).isEqualTo(
                "DROP TABLE IF EXISTS spokenLanguage;"
        );
    }
}