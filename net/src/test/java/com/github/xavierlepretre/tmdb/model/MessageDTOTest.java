package com.github.xavierlepretre.tmdb.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xavierlepretre.tmdb.net.TmdbRetrofitFactory;

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

    @Before
    public void setUp()
    {
        this.mapper = new TmdbRetrofitFactory().createMapper();
    }

    @Test
    public void canDeserialiseEvenIfExtraField() throws Exception
    {
        MessageDTO dto = mapper.readValue(getClass().getResourceAsStream("message_dto_1_with_extra.json"), MessageDTO.class);
        assertThat(dto.getStatusCode()).isEqualTo(new StatusCode(7));
        assertThat(dto.getMessage()).isEqualTo("Invalid API key: You must be granted a valid key.");
    }
}
