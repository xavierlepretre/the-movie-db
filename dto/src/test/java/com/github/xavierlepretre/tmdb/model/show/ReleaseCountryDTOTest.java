package com.github.xavierlepretre.tmdb.model.show;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static org.fest.assertions.api.Assertions.assertThat;

public class ReleaseCountryDTOTest
{
    private ObjectMapper mapper;

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Before public void setUp()
    {
        this.mapper = new ObjectMapper();
    }

    @Test
    public void canDeserialise1() throws Exception
    {
        ReleaseCountryDTO dto = mapper.readValue(getClass().getResourceAsStream("release_country_dto_1.json"), ReleaseCountryDTO.class);
        assertThat(dto.getCertification()).isEmpty();
        assertThat(dto.getIso3166Dash1()).isEqualTo(CountryCode.GB);
        assertThat(dto.isPrimary()).isFalse();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertThat(dto.getReleaseDate()).isEqualTo(formatter.parse("2015-10-26"));
    }

    @Test
    public void canDeserialise2() throws Exception
    {
        ReleaseCountryDTO dto = mapper.readValue(getClass().getResourceAsStream("release_country_dto_2.json"), ReleaseCountryDTO.class);
        assertThat(dto.getCertification()).isEqualTo("PG-13");
        assertThat(dto.getIso3166Dash1()).isEqualTo(CountryCode.US);
        assertThat(dto.isPrimary()).isTrue();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertThat(dto.getReleaseDate()).isEqualTo(formatter.parse("2015-11-06"));
    }

    @Test
    public void cannotDeserialiseMissingCertification() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("certification");
        mapper.readValue(getClass().getResourceAsStream("release_country_dto_1_missing_certification.json"), ReleaseCountryDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingIso3166Dash1() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("iso_3166_1");
        mapper.readValue(getClass().getResourceAsStream("release_country_dto_1_missing_iso_3166_1.json"), ReleaseCountryDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingPrimary() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("primary");
        mapper.readValue(getClass().getResourceAsStream("release_country_dto_1_missing_primary.json"), ReleaseCountryDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingReleaseDate() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("release_date");
        mapper.readValue(getClass().getResourceAsStream("release_country_dto_1_missing_release_date.json"), ReleaseCountryDTO.class);
    }
}
