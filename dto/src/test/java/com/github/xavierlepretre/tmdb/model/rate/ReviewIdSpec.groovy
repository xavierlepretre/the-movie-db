package com.github.xavierlepretre.tmdb.model.rate

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class ReviewIdSpec extends Specification {
    def 'equalsHashcode'() {
        when:
        EqualsVerifier.forClass(ReviewId)
                .allFieldsShouldBeUsed()
                .verify()

        then:
        noExceptionThrown()
    }
}
