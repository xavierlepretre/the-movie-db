package com.github.xavierlepretre.tmdb.model;

import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.sql.R;

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
}