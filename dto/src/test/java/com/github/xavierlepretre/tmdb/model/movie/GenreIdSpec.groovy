package com.github.xavierlepretre.tmdb.model.movie

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class GenreIdSpec extends Specification {
    def 'equalsHashcode'() {
        when:
        EqualsVerifier.forClass(GenreId)
                .allFieldsShouldBeUsed()
                .verify()

        then:
        noExceptionThrown()
    }
}
