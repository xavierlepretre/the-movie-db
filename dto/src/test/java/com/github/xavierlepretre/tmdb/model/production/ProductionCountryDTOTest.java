package com.github.xavierlepretre.tmdb.model.production;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class ProductionCountryDTOTest
{
    private ObjectMapper mapper;

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Before public void setUp()
    {
        this.mapper = new ObjectMapper();
    }

    @Test
    public void canDeserialise() throws Exception
    {
        ProductionCountryDTO dto = mapper.readValue(getClass().getResourceAsStream("production_country_dto_1.json"), ProductionCountryDTO.class);
        assertThat(dto.getIso3166Dash1()).isEqualTo(CountryCode.GB);
        assertThat(dto.getName()).isEqualTo("United Kingdom");
    }

    @Test
    public void cannotDeserialiseMissingIdo3166Dash1() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("iso_3166_1");
        mapper.readValue(getClass().getResourceAsStream("production_country_dto_1_missing_iso_3166_1.json"), ProductionCountryDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingName() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("name");
        mapper.readValue(getClass().getResourceAsStream("production_country_dto_1_missing_name.json"), ProductionCountryDTO.class);
    }
}
