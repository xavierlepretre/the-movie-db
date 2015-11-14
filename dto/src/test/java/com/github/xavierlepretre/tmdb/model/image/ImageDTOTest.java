package com.github.xavierlepretre.tmdb.model.image;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.fest.assertions.data.Offset;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;

public class ImageDTOTest
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
        ImageDTO dto = mapper.readValue(getClass().getResourceAsStream("image_dto_1.json"), ImageDTO.class);
        assertThat(dto.getAspectRatio()).isEqualTo(1.77777777777778f, Offset.offset(0.001f));
        assertThat(dto.getFilePath()).isEqualTo("/cNiO22mtARcyeNivzeON9sOvr65.jpg");
        assertThat(dto.getHeight()).isEqualTo(720);
        assertThat(dto.getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getVoteAverage()).isEqualTo(5.18601190476191f, Offset.offset(0.001f));
        assertThat(dto.getVoteCount()).isEqualTo(1);
        assertThat(dto.getWidth()).isEqualTo(1280);
    }

    @Test
    public void canDeserialise2() throws Exception
    {
        ImageDTO dto = mapper.readValue(getClass().getResourceAsStream("image_dto_2.json"), ImageDTO.class);
        assertThat(dto.getAspectRatio()).isEqualTo(1.77777777777778f, Offset.offset(0.001f));
        assertThat(dto.getFilePath()).isEqualTo("/ozasA59FuPv1QBD2yUueCGmcInv.jpg");
        assertThat(dto.getHeight()).isEqualTo(1080);
        assertThat(dto.getIso639Dash1()).isNull();
        assertThat(dto.getVoteAverage()).isEqualTo(5.21048999309869f, Offset.offset(0.001f));
        assertThat(dto.getVoteCount()).isEqualTo(6);
        assertThat(dto.getWidth()).isEqualTo(1920);
    }

    @Test
    public void cannotDeserialiseMissingAspectRatio() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("aspect_ratio");
        mapper.readValue(getClass().getResourceAsStream("image_dto_1_missing_aspect_ratio.json"), ImageDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingFilePath() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("file_path");
        mapper.readValue(getClass().getResourceAsStream("image_dto_1_missing_file_path.json"), ImageDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingHeight() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("height");
        mapper.readValue(getClass().getResourceAsStream("image_dto_1_missing_height.json"), ImageDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingIso639Dash1() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("iso_639_1");
        mapper.readValue(getClass().getResourceAsStream("image_dto_1_missing_iso_639_1.json"), ImageDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingVoteAverage() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("vote_average");
        mapper.readValue(getClass().getResourceAsStream("image_dto_1_missing_vote_average.json"), ImageDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingVoteCount() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("vote_count");
        mapper.readValue(getClass().getResourceAsStream("image_dto_1_missing_vote_count.json"), ImageDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingWidth() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("width");
        mapper.readValue(getClass().getResourceAsStream("image_dto_1_missing_width.json"), ImageDTO.class);
    }
}