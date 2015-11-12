package com.github.xavierlepretre.tmdb.model.conf;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class ConfigurationDTOTest
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
        ConfigurationDTO dto = mapper.readValue(getClass().getResourceAsStream("configuration_dto_1.json"), ConfigurationDTO.class);
        assertThat(dto.getImagesConf()).isNotNull();
        assertThat(dto.getChangeKeys().size()).isEqualTo(53);
        assertThat(dto.getChangeKeys().get(10)).isEqualTo(new ChangeKeyDTO("created_by"));
    }

    @Test
    public void cannotDeserialiseMissingImages() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("images");
        mapper.readValue(getClass().getResourceAsStream("configuration_dto_1_missing_images.json"), ConfigurationDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingChangeKeys() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("change_keys");
        mapper.readValue(getClass().getResourceAsStream("configuration_dto_1_missing_change_keys.json"), ConfigurationDTO.class);
    }
}
