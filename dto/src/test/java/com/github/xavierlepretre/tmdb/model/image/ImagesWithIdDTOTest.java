package com.github.xavierlepretre.tmdb.model.image;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;
import com.neovisionaries.i18n.LanguageCode;

import org.fest.assertions.data.Offset;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class ImagesWithIdDTOTest
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
        ImagesWithIdDTO dto = mapper.readValue(getClass().getResourceAsStream("images_with_id_dto_1.json"), ImagesWithIdDTO.class);
        assertThat(dto.getBackdrops().size()).isEqualTo(11);
        assertThat(dto.getBackdrops().get(8).getAspectRatio()).isEqualTo(1.77777777777778f, Offset.offset(0.001f));
        assertThat(dto.getBackdrops().get(8).getFilePath()).isEqualTo(new ImagePath("/cNiO22mtARcyeNivzeON9sOvr65.jpg"));
        assertThat(dto.getBackdrops().get(8).getHeight()).isEqualTo(720);
        assertThat(dto.getBackdrops().get(8).getIso639Dash1()).isEqualTo(LanguageCode.en);
        assertThat(dto.getBackdrops().get(8).getVoteAverage()).isEqualTo(5.18601190476191f, Offset.offset(0.001f));
        assertThat(dto.getBackdrops().get(8).getVoteCount()).isEqualTo(1);
        assertThat(dto.getBackdrops().get(8).getWidth()).isEqualTo(1280);
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getPosters().size()).isEqualTo(26);
        assertThat(dto.getPosters().get(4).getAspectRatio()).isEqualTo(0.666666666666667f, Offset.offset(0.001f));
        assertThat(dto.getPosters().get(4).getFilePath()).isEqualTo(new ImagePath("/1n9D32o30XOHMdMWuIT4AaA5ruI.jpg"));
        assertThat(dto.getPosters().get(4).getHeight()).isEqualTo(3000);
        assertThat(dto.getPosters().get(4).getIso639Dash1()).isEqualTo(LanguageCode.en);
        assertThat(dto.getPosters().get(4).getVoteAverage()).isEqualTo(5.31292517006803f, Offset.offset(0.001f));
        assertThat(dto.getPosters().get(4).getVoteCount()).isEqualTo(7);
        assertThat(dto.getPosters().get(4).getWidth()).isEqualTo(2000);
    }
    
    @Test
    public void cannotDeserialiseMissingBackdrops() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("backdrops");
        mapper.readValue(getClass().getResourceAsStream("images_with_id_dto_1_missing_backdrops.json"), ImagesWithIdDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("images_with_id_dto_1_missing_id.json"), ImagesWithIdDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingFilePath() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("posters");
        mapper.readValue(getClass().getResourceAsStream("images_with_id_dto_1_missing_posters.json"), ImagesWithIdDTO.class);
    }
}