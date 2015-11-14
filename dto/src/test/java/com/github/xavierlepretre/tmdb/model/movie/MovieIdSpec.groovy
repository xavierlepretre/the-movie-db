package com.github.xavierlepretre.tmdb.model.movie

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class MovieIdSpec extends Specification {
    def 'equalsHashcode'() {
        when:
        EqualsVerifier.forClass(MovieId)
                .allFieldsShouldBeUsed()
                .verify()

        then:
        noExceptionThrown()
    }
}
