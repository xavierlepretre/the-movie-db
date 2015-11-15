package com.github.xavierlepretre.tmdb.model.movie

import com.github.xavierlepretre.tmdb.model.AppendableRequestFactory
import com.github.xavierlepretre.tmdb.model.AppendableRequestSet
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class MovieRequestSpec extends Specification {
    def 'equalsHashcode'() {
        when:
        EqualsVerifier.forClass(MovieRequest)
                .allFieldsShouldBeUsed()
                .withPrefabValues(
                MovieId.class,
                new MovieId(1),
                new MovieId(2))
                .withPrefabValues(
                MovieRequestParameters.class,
                new MovieRequestParameters(
                        new Locale("en"),
                        new AppendableRequestSet(Arrays.asList(
                                new AppendableRequestFactory().create("a"),
                                new AppendableRequestFactory().create("b")
                        ))),
                new MovieRequestParameters(
                        new Locale("fr"),
                        new AppendableRequestSet(Arrays.asList(
                                new AppendableRequestFactory().create("b"),
                                new AppendableRequestFactory().create("c")
                        ))))
                .verify()

        then:
        noExceptionThrown()
    }
}
