package com.github.xavierlepretre.tmdb.model.i18n;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;

public class SpokenLanguageDTOTest
{
    private ObjectMapper mapper;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before public void setUp()
    {
        this.mapper = new ObjectMapper();
    }

    @Test
    public void canDeserialise() throws Exception
    {
        SpokenLanguageDTO dto = mapper.readValue(getClass().getResourceAsStream("spoken_language_dto_1.json"), SpokenLanguageDTO.class);
        assertThat(dto.getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getName()).isEqualTo("English");
    }

    @Test
    public void cannotDeserialiseMissingIso639Dash1() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("iso_639_1");
        mapper.readValue(getClass().getResourceAsStream("spoken_language_dto_1_missing_iso_639_1.json"), SpokenLanguageDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingName() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("name");
        mapper.readValue(getClass().getResourceAsStream("spoken_language_dto_1_missing_name.json"), SpokenLanguageDTO.class);
    }
}
