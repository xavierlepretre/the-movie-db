package com.github.xavierlepretre.tmdb.model.people

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class CreditIdSpec extends Specification {
    def 'equalsHashcode'() {
        when:
        EqualsVerifier.forClass(CreditId)
                .allFieldsShouldBeUsed()
                .verify()

        then:
        noExceptionThrown()
    }
}
