package com.github.xavierlepretre.tmdb.model.conf;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xavierlepretre.tmdb.model.image.ImageSize;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class ImagesConfDTOTest
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
        ImagesConfDTO dto = mapper.readValue(getClass().getResourceAsStream("images_conf_dto_1.json"), ImagesConfDTO.class);
        assertThat(dto.getBaseUrl()).isEqualTo("http://image.tmdb.org/t/p/");
        assertThat(dto.getSecureBaseUrl()).isEqualTo("https://image.tmdb.org/t/p/");
        assertThat(dto.getBackdropSizes().size()).isEqualTo(4);
        assertThat(dto.getBackdropSizes().get(1)).isEqualTo(new ImageSize("w780"));
        assertThat(dto.getLogoSizes().size()).isEqualTo(7);
        assertThat(dto.getLogoSizes().get(3)).isEqualTo(new ImageSize("w185"));
        assertThat(dto.getPosterSizes().size()).isEqualTo(7);
        assertThat(dto.getPosterSizes().get(4)).isEqualTo(new ImageSize("w500"));
        assertThat(dto.getProfileSizes().size()).isEqualTo(4);
        assertThat(dto.getProfileSizes().get(0)).isEqualTo(new ImageSize("w45"));
        assertThat(dto.getStillSizes().size()).isEqualTo(4);
        assertThat(dto.getStillSizes().get(3)).isEqualTo(new ImageSize("original"));
    }

    @Test
    public void cannotDeserialiseMissingBaseUrl() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("base_url");
        mapper.readValue(getClass().getResourceAsStream("images_conf_dto_1_missing_base_url.json"), ImagesConfDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingSecureBaseUrl() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("secure_base_url");
        mapper.readValue(getClass().getResourceAsStream("images_conf_dto_1_missing_secure_base_url.json"), ImagesConfDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingBackdropSizes() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("backdrop_sizes");
        mapper.readValue(getClass().getResourceAsStream("images_conf_dto_1_missing_backdrop_sizes.json"), ImagesConfDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingLogoSizes() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("logo_sizes");
        mapper.readValue(getClass().getResourceAsStream("images_conf_dto_1_missing_logo_sizes.json"), ImagesConfDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingPosterSizes() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("poster_sizes");
        mapper.readValue(getClass().getResourceAsStream("images_conf_dto_1_missing_poster_sizes.json"), ImagesConfDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingProfileSizes() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("profile_sizes");
        mapper.readValue(getClass().getResourceAsStream("images_conf_dto_1_missing_profile_sizes.json"), ImagesConfDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingStillSizes() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("still_sizes");
        mapper.readValue(getClass().getResourceAsStream("images_conf_dto_1_missing_still_sizes.json"), ImagesConfDTO.class);
    }
}
