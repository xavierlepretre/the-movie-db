package com.github.xavierlepretre.tmdb.model.people

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class CastIdSpec extends Specification {
    def 'equalsHashcode'() {
        when:
        EqualsVerifier.forClass(CastId)
                .allFieldsShouldBeUsed()
                .verify()

        then:
        noExceptionThrown()
    }
}
