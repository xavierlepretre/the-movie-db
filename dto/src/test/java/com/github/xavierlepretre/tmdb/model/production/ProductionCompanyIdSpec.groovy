package com.github.xavierlepretre.tmdb.model.production

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class ProductionCompanyIdSpec extends Specification {
    def 'equalsHashcode'() {
        when:
        EqualsVerifier.forClass(ProductionCompanyId)
                .allFieldsShouldBeUsed()
                .verify()

        then:
        noExceptionThrown()
    }
}
