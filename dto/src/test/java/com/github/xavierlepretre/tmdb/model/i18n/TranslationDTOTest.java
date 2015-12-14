package com.github.xavierlepretre.tmdb.model.i18n;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.LanguageCode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class TranslationDTOTest
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
        TranslationDTO dto = mapper.readValue(getClass().getResourceAsStream("translation_dto_1.json"), TranslationDTO.class);
        assertThat(dto.getIso639Dash1()).isEqualTo(LanguageCode.fr);
        assertThat(dto.getName()).isEqualTo("Français");
        assertThat(dto.getEnglishName()).isEqualTo("French");
    }

    @Test
    public void cannotDeserialiseMissingIso639Dash1() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("iso_639_1");
        mapper.readValue(getClass().getResourceAsStream("translation_dto_1_missing_iso_639_1.json"), TranslationDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingName() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("name");
        mapper.readValue(getClass().getResourceAsStream("translation_dto_1_missing_name.json"), TranslationDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingEnglishName() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("english_name");
        mapper.readValue(getClass().getResourceAsStream("translation_dto_1_missing_english_name.json"), TranslationDTO.class);
    }
}
