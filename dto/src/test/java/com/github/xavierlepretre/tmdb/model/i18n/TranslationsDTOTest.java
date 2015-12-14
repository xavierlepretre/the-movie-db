package com.github.xavierlepretre.tmdb.model.i18n;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.LanguageCode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class TranslationsDTOTest
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
        TranslationsDTO dto = mapper.readValue(getClass().getResourceAsStream("translations_dto_1.json"), TranslationsDTO.class);
        assertThat(dto.getTranslations().size()).isEqualTo(22);
        assertThat(dto.getTranslations().get(1).getEnglishName()).isEqualTo("French");
        assertThat(dto.getTranslations().get(1).getIso639Dash1()).isEqualTo(LanguageCode.fr);
        assertThat(dto.getTranslations().get(1).getName()).isEqualTo("Français");
    }

    @Test
    public void cannotDeserialiseMissingTranslations() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("translations");
        mapper.readValue(getClass().getResourceAsStream("translations_dto_1_missing_translations.json"), TranslationsDTO.class);
    }
}
