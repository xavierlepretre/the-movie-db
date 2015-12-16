package com.github.xavierlepretre.tmdb.model.movie;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class MovieGenreTest
{
    @Test
    public void equalsHashcode_isOk() throws Exception
    {
        EqualsVerifier.forClass(MovieGenre.class)
                .allFieldsShouldBeUsed()
                .withPrefabValues(
                        MovieId.class,
                        new MovieId(1),
                        new MovieId(2))
                .withPrefabValues(
                        GenreId.class,
                        new GenreId(1),
                        new GenreId(2))
                .verify();
    }
}