package com.github.xavierlepretre.tmdb.model;

import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.movie.CollectionId;
import com.github.xavierlepretre.tmdb.model.movie.GenreId;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyId;
import com.github.xavierlepretre.tmdb.sql.R;
import com.neovisionaries.i18n.CountryCode;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class TmdbContractTest
{
    @Test
    public void contentAuthorityIsCorrect() throws Exception
    {
        assertThat(InstrumentationRegistry.getContext().getString(R.string.content_authority))
                .isEqualTo(TmdbContract.CONTENT_AUTHORITY);
    }

    @Test
    public void buildUriCollection_isCorrect() throws Exception
    {
        assertThat(TmdbContract.CollectionEntity.buildUri(new CollectionId(32)))
                .isEqualTo(Uri.parse("content://com.github.xavierlepretre.tmdb/collection/32"));
    }

    @Test
    public void buildUriGenre_isCorrect() throws Exception
    {
        assertThat(TmdbContract.GenreEntity.buildUri(new GenreId(32)))
                .isEqualTo(Uri.parse("content://com.github.xavierlepretre.tmdb/genre/32"));
    }

    @Test
    public void buildUriProductionCompany_isCorrect() throws Exception
    {
        assertThat(TmdbContract.ProductionCompanyEntity.buildUri(new ProductionCompanyId(32)))
                .isEqualTo(Uri.parse("content://com.github.xavierlepretre.tmdb/productionCompany/32"));
    }

    @Test
    public void buildUriProductionCountry_isCorrect() throws Exception
    {
        assertThat(TmdbContract.ProductionCountryEntity.buildUri(CountryCode.GB))
                .isEqualTo(Uri.parse("content://com.github.xavierlepretre.tmdb/productionCountry/GB"));
    }
}