package com.github.xavierlepretre.tmdb.model.production;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

public class ProductionCompanyDTOTest
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
        ProductionCompanyDTO dto = mapper.readValue(getClass().getResourceAsStream("production_company_dto_1.json"), ProductionCompanyDTO.class);
        assertThat(dto.getId()).isEqualTo(new ProductionCompanyId(7576));
        assertThat(dto.getName()).isEqualTo("Eon Productions");
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("production_company_dto_1_missing_id.json"), ProductionCompanyDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingName() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("name");
        mapper.readValue(getClass().getResourceAsStream("production_company_dto_1_missing_name.json"), ProductionCompanyDTO.class);
    }
}
