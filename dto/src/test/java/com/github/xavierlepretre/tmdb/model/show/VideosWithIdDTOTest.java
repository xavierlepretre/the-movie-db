package com.github.xavierlepretre.tmdb.model.show;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;
import com.neovisionaries.i18n.LanguageCode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class VideosWithIdDTOTest
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
        VideosWithIdDTO dto = mapper.readValue(getClass().getResourceAsStream("videos_with_id_dto_1.json"), VideosWithIdDTO.class);
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getResults().size()).isEqualTo(3);
        assertThat(dto.getResults().get(0).getId()).isEqualTo(new VideoId("5641eb2fc3a3685bdc002e0b"));
        assertThat(dto.getResults().get(0).getIso639Dash1()).isEqualTo(LanguageCode.en);
        assertThat(dto.getResults().get(0).getKey()).isEqualTo("BOVriTeIypQ");
        assertThat(dto.getResults().get(0).getName()).isEqualTo("Spectre Ultimate 007 Trailer 2015 HD");
        assertThat(dto.getResults().get(0).getSite()).isEqualTo("YouTube");
        assertThat(dto.getResults().get(0).getSize()).isEqualTo(1080);
        assertThat(dto.getResults().get(0).getType()).isEqualTo("Trailer");
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("videos_with_id_dto_1_missing_id.json"), VideosWithIdDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingResults() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("results");
        mapper.readValue(getClass().getResourceAsStream("videos_with_id_dto_1_missing_results.json"), VideosWithIdDTO.class);
    }
}
