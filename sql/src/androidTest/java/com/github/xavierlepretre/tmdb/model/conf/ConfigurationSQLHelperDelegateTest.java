package com.github.xavierlepretre.tmdb.model.conf;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ConfigurationSQLHelperDelegateTest
{
    @Test
    public void createRequestIsCorrect() throws Exception
    {
        assertThat(new ConfigurationSQLHelperDelegate().getCreateQuery()).isEqualTo(
                "CREATE TABLE configuration(_id INTEGER PRIMARY KEY NOT NULL," +
                        "imagesBaseUrl TEXT NULL," +
                        "imagesSecureBaseUrl TEXT NULL," +
                        "imagesBackdropSizes TEXT NULL," +
                        "imagesLogoSizes TEXT NULL," +
                        "imagesPosterSizes TEXT NULL," +
                        "imagesProfileSizes TEXT NULL," +
                        "imagesStillSizes TEXT NULL," +
                        "changeKeys TEXT NULL);"
        );
    }

    @Test
    public void upgradeRequestIsCorrect() throws Exception
    {
        assertThat(new ConfigurationSQLHelperDelegate().getUpgradeQuery(1, 2)).isEqualTo(
                "DROP TABLE IF EXISTS configuration;"
        );
    }
}