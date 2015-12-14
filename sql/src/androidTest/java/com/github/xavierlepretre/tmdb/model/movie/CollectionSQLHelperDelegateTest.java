package com.github.xavierlepretre.tmdb.model.movie;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class CollectionSQLHelperDelegateTest
{
    @Test
    public void createRequestIsCorrect() throws Exception
    {
        assertThat(new CollectionSQLHelperDelegate().getCreateQuery()).isEqualTo(
                "CREATE TABLE collection(backdropPath TEXT NULL,_id INTEGER PRIMARY KEY NOT NULL,name TEXT NULL,posterPath TEXT NULL);"
        );
    }

    @Test
    public void upgradeRequestIsCorrect() throws Exception
    {
        assertThat(new CollectionSQLHelperDelegate().getUpgradeQuery(1, 2)).isEqualTo(
                "DROP TABLE IF EXISTS collection;"
        );
    }
}