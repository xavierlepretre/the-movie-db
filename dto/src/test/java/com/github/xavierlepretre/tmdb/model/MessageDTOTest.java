package com.github.xavierlepretre.tmdb.model;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class MessageDTOTest
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
        MessageDTO dto = mapper.readValue(getClass().getResourceAsStream("message_dto_1.json"), MessageDTO.class);
        assertThat(dto.getStatusCode()).isEqualTo(new StatusCode(7));
        assertThat(dto.getMessage()).isEqualTo("Invalid API key: You must be granted a valid key.");
    }

    @Test
    public void cannotDeserialiseMissingCode() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("status_code");
        mapper.readValue(getClass().getResourceAsStream("message_dto_1_missing_code.json"), MessageDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingMessage() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("status_message");
        mapper.readValue(getClass().getResourceAsStream("message_dto_1_missing_message.json"), MessageDTO.class);
    }
}
