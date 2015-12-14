package com.github.xavierlepretre.tmdb.model.movie;

import com.github.xavierlepretre.tmdb.model.AppendableRequestFactory;
import com.github.xavierlepretre.tmdb.model.AppendableRequestSet;
import com.neovisionaries.i18n.LanguageCode;

import org.junit.Test;

import java.util.Arrays;

import nl.jqno.equalsverifier.EqualsVerifier;

public class MovieRequestTest
{
    @Test
    public void equalsHashcode_isOk() throws Exception
    {
        EqualsVerifier.forClass(MovieRequest.class)
                .allFieldsShouldBeUsed()
                .withPrefabValues(
                        MovieId.class,
                        new MovieId(1),
                        new MovieId(2))
                .withPrefabValues(
                        MovieRequestParameters.class,
                        new MovieRequestParameters(
                                LanguageCode.en,
                                new AppendableRequestSet(Arrays.asList(
                                        new AppendableRequestFactory().create("a"),
                                        new AppendableRequestFactory().create("b")
                                ))),
                        new MovieRequestParameters(
                                LanguageCode.fr,
                                new AppendableRequestSet(Arrays.asList(
                                        new AppendableRequestFactory().create("b"),
                                        new AppendableRequestFactory().create("c")
                                ))))
                .verify();
    }
}