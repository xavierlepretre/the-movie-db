package com.github.xavierlepretre.tmdb.model.movie;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class GenreSQLHelperDelegateTest
{
    @Test
    public void createRequestIsCorrect() throws Exception
    {
        assertThat(new GenreSQLHelperDelegate().getCreateQuery()).isEqualTo(
                "CREATE TABLE genre(_id INTEGER PRIMARY KEY NOT NULL,name TEXT NULL);"
        );
    }

    @Test
    public void upgradeRequestIsCorrect() throws Exception
    {
        assertThat(new GenreSQLHelperDelegate().getUpgradeQuery(1, 2)).isEqualTo(
                "DROP TABLE IF EXISTS genre;"
        );
    }
}