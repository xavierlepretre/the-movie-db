package com.github.xavierlepretre.tmdb.model.people;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xavierlepretre.tmdb.model.movie.GenreId;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class CastDTOTest
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
        CastDTO dto = mapper.readValue(getClass().getResourceAsStream("cast_dto_1.json"), CastDTO.class);
        assertThat(dto.getCastId()).isEqualTo(new CastId(1));
        assertThat(dto.getCharacter()).isEqualTo("James Bond");
        assertThat(dto.getCreditId()).isEqualTo(new CreditId("52fe4d22c3a368484e1d8d6b"));
        assertThat(dto.getId()).isEqualTo(new PersonId(8784));
        assertThat(dto.getName()).isEqualTo("Daniel Craig");
        assertThat(dto.getOrder()).isEqualTo(0);
        assertThat(dto.getProfilePath()).isEqualTo("/cO5OUQAMM6a4Rndw5Hc81KgpF5p.jpg");
    }

    @Test
    public void cannotDeserialiseMissingCastId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("cast_id");
        mapper.readValue(getClass().getResourceAsStream("cast_dto_1_missing_cast_id.json"), CastDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingCharacter() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("character");
        mapper.readValue(getClass().getResourceAsStream("cast_dto_1_missing_character.json"), CastDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingCreditId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("credit_id");
        mapper.readValue(getClass().getResourceAsStream("cast_dto_1_missing_credit_id.json"), CastDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("cast_dto_1_missing_id.json"), CastDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingName() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("name");
        mapper.readValue(getClass().getResourceAsStream("cast_dto_1_missing_name.json"), CastDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingOrder() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("order");
        mapper.readValue(getClass().getResourceAsStream("cast_dto_1_missing_order.json"), CastDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingProfilePath() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("profile_path");
        mapper.readValue(getClass().getResourceAsStream("cast_dto_1_missing_profile_path.json"), CastDTO.class);
    }
}
