package com.github.xavierlepretre.tmdb.model.i18n;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;

public class TranslationsWithIdDTOTest
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
        TranslationsWithIdDTO dto = mapper.readValue(getClass().getResourceAsStream("translations_with_id_dto_1.json"), TranslationsWithIdDTO.class);
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getTranslations().size()).isEqualTo(22);
        assertThat(dto.getTranslations().get(1).getEnglishName()).isEqualTo("French");
        assertThat(dto.getTranslations().get(1).getIso639Dash1()).isEqualTo(new Locale("fr"));
        assertThat(dto.getTranslations().get(1).getName()).isEqualTo("Français");
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("translations_with_id_dto_1_missing_id.json"), TranslationsWithIdDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingTranslations() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("translations");
        mapper.readValue(getClass().getResourceAsStream("translations_with_id_dto_1_missing_translations.json"), TranslationsWithIdDTO.class);
    }
}
