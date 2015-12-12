package com.github.xavierlepretre.tmdb.model.movie;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyId;
import com.neovisionaries.i18n.CountryCode;

import org.fest.assertions.data.Offset;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;

import static org.fest.assertions.api.Assertions.assertThat;

public class MovieDTOTest
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
        MovieDTO dto = mapper.readValue(getClass().getResourceAsStream("movie_dto_1.json"), MovieDTO.class);
        assertThat(dto.getAdult()).isFalse();
        assertThat(dto.getBackdropPath()).isEqualTo(new ImagePath("/wVTYlkKPKrljJfugXN7UlLNjtuJ.jpg"));
        assertThat(dto.getBelongsToCollection().getBackdropPath()).isEqualTo(new ImagePath("/dOSECZImeyZldoq0ObieBE0lwie.jpg"));
        assertThat(dto.getBelongsToCollection().getId()).isEqualTo(new CollectionId(645));
        assertThat(dto.getBelongsToCollection().getName()).isEqualTo("James Bond Collection");
        assertThat(dto.getBelongsToCollection().getPosterPath()).isEqualTo(new ImagePath("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg"));
        assertThat(dto.getBudget()).isEqualTo(300000000);
        assertThat(dto.getGenreIds()).isEqualTo(Arrays.asList(new GenreId(28), new GenreId(12), new GenreId(80)));
        assertThat(dto.getGenres().size()).isEqualTo(3);
        assertThat(dto.getGenres().get(0).getId()).isEqualTo(new GenreId(28));
        assertThat(dto.getGenres().get(0).getName()).isEqualTo("Action");
        assertThat(dto.getGenres().get(1).getId()).isEqualTo(new GenreId(12));
        assertThat(dto.getGenres().get(1).getName()).isEqualTo("Adventure");
        assertThat(dto.getGenres().get(2).getId()).isEqualTo(new GenreId(80));
        assertThat(dto.getGenres().get(2).getName()).isEqualTo("Crime");
        assertThat(dto.getHomepage()).isEqualTo("http://www.sonypictures.com/movies/spectre/");
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getImdbId()).isEqualTo(new ImdbId("tt2379713"));
        assertThat(dto.getOriginalLanguage()).isEqualTo(new Locale("en"));
        assertThat(dto.getOriginalTitle()).isEqualTo("Spectre");
        assertThat(dto.getOverview()).startsWith("A cryptic message from Bond’s");
        assertThat(dto.getPopularity()).isEqualTo(56.231904f, Offset.offset(0.01f));
        assertThat(dto.getPosterPath()).isEqualTo(new ImagePath("/1n9D32o30XOHMdMWuIT4AaA5ruI.jpg"));
        assertThat(dto.getProductionCompanies().size()).isEqualTo(1);
        assertThat(dto.getProductionCompanies().get(0).getId()).isEqualTo(new ProductionCompanyId(7576));
        assertThat(dto.getProductionCompanies().get(0).getName()).isEqualTo("Eon Productions");
        assertThat(dto.getProductionCountries().size()).isEqualTo(1);
        assertThat(dto.getProductionCountries().get(0).getIso3166Dash1()).isEqualTo(CountryCode.GB);
        assertThat(dto.getProductionCountries().get(0).getName()).isEqualTo("United Kingdom");
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertThat(dto.getReleaseDate()).isEqualTo(formatter.parse("2015-11-06"));
        assertThat(dto.getRevenue()).isEqualTo(0);
        assertThat(dto.getRuntime()).isEqualTo(148);
        assertThat(dto.getSpokenLanguages().size()).isEqualTo(1);
        assertThat(dto.getSpokenLanguages().get(0).getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getSpokenLanguages().get(0).getName()).isEqualTo("English");
        assertThat(dto.getStatus()).isEqualTo("Released");
        assertThat(dto.getTagline()).isEmpty();
        assertThat(dto.getTitle()).isEqualTo("Spectre");
        assertThat(dto.getVideo()).isFalse();
        assertThat(dto.getVoteAverage()).isEqualTo(6.6f);
        assertThat(dto.getVoteCount()).isEqualTo(479);
    }

    @Test
    public void cannotDeserialiseMissingAdult() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("adult");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_adult.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingBackdropPath() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("backdrop_path");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_backdrop_path.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingBudget() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("budget");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_budget.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingBelongsToCollection() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("belongs_to_collection");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_belongs_to_collection.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingGenres() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("genres");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_genres.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingHomepage() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("homepage");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_homepage.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_id.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingImdbId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("imdb_id");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_imdb_id.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingOriginalLanguage() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("original_language");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_original_language.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingOriginalTitle() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("original_title");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_original_title.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingOverview() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("overview");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_overview.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingPopularity() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("popularity");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_popularity.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingPosterPath() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("poster_path");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_poster_path.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingProductionCompanies() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("production_companies");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_production_companies.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingProductionCountries() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("production_countries");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_production_countries.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingReleaseDate() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("release_date");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_release_date.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingRevenue() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("revenue");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_revenue.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingRuntime() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("runtime");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_runtime.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingSpokenLanguages() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("spoken_languages");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_spoken_languages.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingStatus() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("status");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_status.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingTagline() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("tagline");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_tagline.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingTitle() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("title");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_title.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingVideo() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("video");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_video.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingVoteAverage() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("vote_average");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_vote_average.json"), MovieDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingVoteCount() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("vote_count");
        mapper.readValue(getClass().getResourceAsStream("movie_dto_1_missing_vote_count.json"), MovieDTO.class);
    }
}
