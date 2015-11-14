package com.github.xavierlepretre.tmdb.model.people

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class PersonIdSpec extends Specification {
    def 'equalsHashcode'() {
        when:
        EqualsVerifier.forClass(PersonId)
                .allFieldsShouldBeUsed()
                .verify()

        then:
        noExceptionThrown()
    }
}
