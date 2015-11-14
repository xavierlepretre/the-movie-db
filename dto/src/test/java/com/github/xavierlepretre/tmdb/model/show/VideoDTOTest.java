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
import java.util.Locale;
import java.util.TimeZone;

import static org.fest.assertions.api.Assertions.assertThat;

public class VideoDTOTest
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
        VideoDTO dto = mapper.readValue(getClass().getResourceAsStream("video_dto_1.json"), VideoDTO.class);
        assertThat(dto.getId()).isEqualTo(new VideoId("5641eb2fc3a3685bdc002e0b"));
        assertThat(dto.getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getKey()).isEqualTo("BOVriTeIypQ");
        assertThat(dto.getName()).isEqualTo("Spectre Ultimate 007 Trailer 2015 HD");
        assertThat(dto.getSite()).isEqualTo("YouTube");
        assertThat(dto.getSize()).isEqualTo(1080);
        assertThat(dto.getType()).isEqualTo("Trailer");
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("video_dto_1_missing_id.json"), VideoDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingIso639Dash1() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("iso_639_1");
        mapper.readValue(getClass().getResourceAsStream("video_dto_1_missing_iso_639_1.json"), VideoDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingPrimary() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("key");
        mapper.readValue(getClass().getResourceAsStream("video_dto_1_missing_key.json"), VideoDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingReleaseDate() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("name");
        mapper.readValue(getClass().getResourceAsStream("video_dto_1_missing_name.json"), VideoDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingSite() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("site");
        mapper.readValue(getClass().getResourceAsStream("video_dto_1_missing_site.json"), VideoDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingSize() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("size");
        mapper.readValue(getClass().getResourceAsStream("video_dto_1_missing_size.json"), VideoDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingType() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("type");
        mapper.readValue(getClass().getResourceAsStream("video_dto_1_missing_type.json"), VideoDTO.class);
    }
}
