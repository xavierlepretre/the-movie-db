package com.github.xavierlepretre.tmdb.model.movie;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;
import com.github.xavierlepretre.tmdb.model.people.CastId;
import com.github.xavierlepretre.tmdb.model.people.CreditId;
import com.github.xavierlepretre.tmdb.model.people.PersonId;
import com.github.xavierlepretre.tmdb.model.rate.ReviewId;
import com.github.xavierlepretre.tmdb.model.show.VideoId;
import com.github.xavierlepretre.tmdb.model.tag.KeywordId;
import com.github.xavierlepretre.tmdb.model.tag.ListId;
import com.neovisionaries.i18n.CountryCode;

import org.fest.assertions.data.Offset;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;

import static org.fest.assertions.api.Assertions.assertThat;

public class MovieWithExtraDTOTest
{
    private ObjectMapper mapper;
    private SimpleDateFormat formatter;

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Before public void setUp()
    {
        this.mapper = new ObjectMapper();
        this.formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Test
    public void canDeserialise() throws Exception
    {
        MovieWithExtraDTO dto = mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1.json"), MovieWithExtraDTO.class);
        assertThat(dto.getAdult()).isFalse();
        assertThat(dto.getBackdropPath()).isEqualTo(new ImagePath("/wVTYlkKPKrljJfugXN7UlLNjtuJ.jpg"));
        assertThat(dto.getGenreIds()).isEqualTo(Arrays.asList(new GenreId(28), new GenreId(12), new GenreId(80)));
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getOriginalLanguage()).isEqualTo(new Locale("en"));
        assertThat(dto.getOriginalTitle()).isEqualTo("Spectre");
        assertThat(dto.getOverview()).startsWith("A cryptic message from Bond’s");
        assertThat(dto.getReleaseDate()).isEqualTo(formatter.parse("2015-11-06"));
        assertThat(dto.getPosterPath()).isEqualTo(new ImagePath("/1n9D32o30XOHMdMWuIT4AaA5ruI.jpg"));
        assertThat(dto.getPopularity()).isGreaterThan(50f);
        assertThat(dto.getTitle()).isEqualTo("Spectre");
        assertThat(dto.getVideo()).isFalse();
        assertThat(dto.getVoteAverage()).isGreaterThan(5f);
        assertThat(dto.getVoteCount()).isGreaterThanOrEqualTo(453);
        // Alternative titles
        assertThat(dto.getAlternativeTitles().getTitles().size()).isGreaterThanOrEqualTo(6);
        assertThat(dto.getAlternativeTitles().getTitles().get(1).getIso3166Dash1()).isEqualTo(CountryCode.ES);
        assertThat(dto.getAlternativeTitles().getTitles().get(1).getTitle()).isEqualTo("007 James Bond: Spectre");
        // Credits
        assertThat(dto.getCredits().getCast().get(0).getCastId()).isEqualTo(new CastId(1));
        assertThat(dto.getCredits().getCast().get(0).getCharacter()).isEqualTo("James Bond");
        assertThat(dto.getCredits().getCast().get(0).getCreditId()).isEqualTo(new CreditId("52fe4d22c3a368484e1d8d6b"));
        assertThat(dto.getCredits().getCast().get(0).getId()).isEqualTo(new PersonId(8784));
        assertThat(dto.getCredits().getCast().get(0).getName()).isEqualTo("Daniel Craig");
        assertThat(dto.getCredits().getCast().get(0).getOrder()).isEqualTo(0);
        assertThat(dto.getCredits().getCast().get(0).getProfilePath()).isEqualTo(new ImagePath("/cO5OUQAMM6a4Rndw5Hc81KgpF5p.jpg"));
        assertThat(dto.getCredits().getCrew().size()).isEqualTo(15);
        assertThat(dto.getCredits().getCrew().get(0).getCreditId()).isEqualTo(new CreditId("52fe4d22c3a368484e1d8d71"));
        assertThat(dto.getCredits().getCrew().get(0).getDepartment()).isEqualTo("Writing");
        assertThat(dto.getCredits().getCrew().get(0).getId()).isEqualTo(new PersonId(9856));
        assertThat(dto.getCredits().getCrew().get(0).getJob()).isEqualTo("Characters");
        assertThat(dto.getCredits().getCrew().get(0).getName()).isEqualTo("Ian Fleming");
        assertThat(dto.getCredits().getCrew().get(0).getProfilePath()).isEqualTo(new ImagePath("/91U37Em6Ru87DiAPMdsocGKyQ0W.jpg"));
        // Images
        assertThat(dto.getImages().getBackdrops().size()).isEqualTo(11);
        assertThat(dto.getImages().getBackdrops().get(8).getAspectRatio()).isEqualTo(1.77777777777778f, Offset.offset(0.001f));
        assertThat(dto.getImages().getBackdrops().get(8).getFilePath()).isEqualTo(new ImagePath("/cNiO22mtARcyeNivzeON9sOvr65.jpg"));
        assertThat(dto.getImages().getBackdrops().get(8).getHeight()).isEqualTo(720);
        assertThat(dto.getImages().getBackdrops().get(8).getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getImages().getBackdrops().get(8).getVoteAverage()).isEqualTo(5.18601190476191f, Offset.offset(0.001f));
        assertThat(dto.getImages().getBackdrops().get(8).getVoteCount()).isEqualTo(1);
        assertThat(dto.getImages().getBackdrops().get(8).getWidth()).isEqualTo(1280);
        assertThat(dto.getImages().getPosters().size()).isEqualTo(26);
        assertThat(dto.getImages().getPosters().get(4).getAspectRatio()).isEqualTo(0.666666666666667f, Offset.offset(0.001f));
        assertThat(dto.getImages().getPosters().get(4).getFilePath()).isEqualTo(new ImagePath("/1n9D32o30XOHMdMWuIT4AaA5ruI.jpg"));
        assertThat(dto.getImages().getPosters().get(4).getHeight()).isEqualTo(3000);
        assertThat(dto.getImages().getPosters().get(4).getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getImages().getPosters().get(4).getVoteAverage()).isEqualTo(5.31292517006803f, Offset.offset(0.001f));
        assertThat(dto.getImages().getPosters().get(4).getVoteCount()).isEqualTo(7);
        assertThat(dto.getImages().getPosters().get(4).getWidth()).isEqualTo(2000);
        // Keywords
        assertThat(dto.getKeywords().getKeywords().size()).isEqualTo(4);
        assertThat(dto.getKeywords().getKeywords().get(0).getId()).isEqualTo(new KeywordId(12360));
        assertThat(dto.getKeywords().getKeywords().get(0).getName()).isEqualTo("james bond");
        // Lists
        assertThat(dto.getLists().getPage()).isEqualTo(1);
        assertThat(dto.getLists().getResults().size()).isEqualTo(20);
        assertThat(dto.getLists().getResults().get(0).getDescription()).isEqualTo("James Bond");
        assertThat(dto.getLists().getResults().get(0).getFavoriteCount()).isGreaterThanOrEqualTo(5);
        assertThat(dto.getLists().getResults().get(0).getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getLists().getResults().get(0).getItemCount()).isGreaterThanOrEqualTo(26);
        assertThat(dto.getLists().getResults().get(0).getId()).isEqualTo(new ListId("5308b87fc3a36842010027be"));
        assertThat(dto.getLists().getResults().get(0).getName()).isEqualTo("James Bond - Movie Collection");
        assertThat(dto.getLists().getResults().get(0).getPosterPath()).isEqualTo(new ImagePath("/jHt3L6rxboCMHULYGdmv6TqjvZr.jpg"));
        assertThat(dto.getLists().getTotalPages()).isGreaterThanOrEqualTo(2);
        assertThat(dto.getLists().getTotalResults()).isGreaterThanOrEqualTo(29);
        // Releases
        assertThat(dto.getReleases().getCountries().size()).isEqualTo(59);
        assertThat(dto.getReleases().getCountries().get(0).getCertification()).isEmpty();
        assertThat(dto.getReleases().getCountries().get(0).getIso3166Dash1()).isEqualTo(CountryCode.GB);
        assertThat(dto.getReleases().getCountries().get(0).isPrimary()).isFalse();
        assertThat(dto.getReleases().getCountries().get(0).getReleaseDate()).isEqualTo(formatter.parse("2015-10-26"));
        assertThat(dto.getReleases().getCountries().get(1).getCertification()).isEqualTo("PG-13");
        assertThat(dto.getReleases().getCountries().get(1).getIso3166Dash1()).isEqualTo(CountryCode.US);
        assertThat(dto.getReleases().getCountries().get(1).isPrimary()).isTrue();
        assertThat(dto.getReleases().getCountries().get(1).getReleaseDate()).isEqualTo(formatter.parse("2015-11-06"));
        // Reviews
        assertThat(dto.getReviews().getPage()).isEqualTo(1);
        assertThat(dto.getReviews().getResults().size()).isEqualTo(2);
        assertThat(dto.getReviews().getResults().get(0).getAuthor()).isEqualTo("cutprintchris");
        assertThat(dto.getReviews().getResults().get(0).getContent()).startsWith("<a href=\"http://www.cutprintfilm.com/r");
        assertThat(dto.getReviews().getResults().get(0).getId()).isEqualTo(new ReviewId("563e06159251413b1300c821"));
        assertThat(dto.getReviews().getResults().get(0).getUrl()).isEqualTo("http://j.mp/1MPodnZ");
        assertThat(dto.getReviews().getTotalPages()).isGreaterThanOrEqualTo(1);
        assertThat(dto.getReviews().getTotalResults()).isGreaterThanOrEqualTo(2);
        // Similar
        assertThat(dto.getSimilar().getPage()).isEqualTo(1);
        assertThat(dto.getSimilar().getResults().size()).isEqualTo(20);
        assertThat(dto.getSimilar().getResults().get(0).getAdult()).isEqualTo(false);
        assertThat(dto.getSimilar().getResults().get(0).getBackdropPath()).isEqualTo(new ImagePath("/iyD72nJFBGbEIrpQjzdhE3wFxPL.jpg"));
        assertThat(dto.getSimilar().getResults().get(0).getGenreIds()).isEqualTo(Arrays.asList(
                new GenreId(12),
                new GenreId(28),
                new GenreId(53)));
        assertThat(dto.getSimilar().getResults().get(0).getId()).isEqualTo(new MovieId(658));
        assertThat(dto.getSimilar().getResults().get(0).getOriginalLanguage()).isEqualTo(new Locale("en"));
        assertThat(dto.getSimilar().getResults().get(0).getOriginalTitle()).isEqualTo("Goldfinger");
        assertThat(dto.getSimilar().getResults().get(0).getOverview()).startsWith("Bond is in Miami on holiday when");
        assertThat(dto.getSimilar().getResults().get(0).getPosterPath()).isEqualTo(new ImagePath("/vBNbFU3OS6okJIQBOos1aZXpy2Z.jpg"));
        assertThat(dto.getSimilar().getResults().get(0).getPopularity()).isGreaterThanOrEqualTo(3);
        assertThat(dto.getSimilar().getResults().get(0).getReleaseDate()).isEqualTo(formatter.parse("1964-09-17"));
        assertThat(dto.getSimilar().getResults().get(0).getTitle()).isEqualTo("Goldfinger");
        assertThat(dto.getSimilar().getResults().get(0).getVideo()).isFalse();
        assertThat(dto.getSimilar().getResults().get(0).getVoteAverage()).isGreaterThanOrEqualTo(5);
        assertThat(dto.getSimilar().getResults().get(0).getVoteCount()).isGreaterThanOrEqualTo(356);
        assertThat(dto.getSimilar().getTotalPages()).isEqualTo(3);
        assertThat(dto.getSimilar().getTotalResults()).isEqualTo(44);
        // Translations
        assertThat(dto.getTranslations().getTranslations().size()).isEqualTo(22);
        assertThat(dto.getTranslations().getTranslations().get(1).getEnglishName()).isEqualTo("French");
        assertThat(dto.getTranslations().getTranslations().get(1).getIso639Dash1()).isEqualTo(new Locale("fr"));
        assertThat(dto.getTranslations().getTranslations().get(1).getName()).isEqualTo("Français");
        // Videos
        assertThat(dto.getVideos().getResults().size()).isEqualTo(3);
        assertThat(dto.getVideos().getResults().get(0).getId()).isEqualTo(new VideoId("5641eb2fc3a3685bdc002e0b"));
        assertThat(dto.getVideos().getResults().get(0).getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getVideos().getResults().get(0).getKey()).isEqualTo("BOVriTeIypQ");
        assertThat(dto.getVideos().getResults().get(0).getName()).isEqualTo("Spectre Ultimate 007 Trailer 2015 HD");
        assertThat(dto.getVideos().getResults().get(0).getSite()).isEqualTo("YouTube");
        assertThat(dto.getVideos().getResults().get(0).getSize()).isEqualTo(1080);
        assertThat(dto.getVideos().getResults().get(0).getType()).isEqualTo("Trailer");
    }

    @Test
    public void cannotDeserialiseMissingAdult() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("adult");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_adult.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingBackdropPath() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("backdrop_path");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_backdrop_path.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingBudget() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("budget");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_budget.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingBelongsToCollection() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("belongs_to_collection");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_belongs_to_collection.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingGenres() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("genres");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_genres.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingHomepage() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("homepage");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_homepage.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("id");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_id.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingImdbId() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("imdb_id");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_imdb_id.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingOriginalLanguage() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("original_language");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_original_language.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingOriginalTitle() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("original_title");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_original_title.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingOverview() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("overview");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_overview.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingPopularity() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("popularity");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_popularity.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingPosterPath() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("poster_path");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_poster_path.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingProductionCompanies() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("production_companies");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_production_companies.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingProductionCountries() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("production_countries");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_production_countries.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingReleaseDate() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("release_date");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_release_date.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingRevenue() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("revenue");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_revenue.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingRuntime() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("runtime");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_runtime.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingSpokenLanguages() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("spoken_languages");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_spoken_languages.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingStatus() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("status");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_status.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingTagline() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("tagline");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_tagline.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingTitle() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("title");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_title.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingVideo() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("video");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_video.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingVoteAverage() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("vote_average");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_vote_average.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void cannotDeserialiseMissingVoteCount() throws Exception
    {
        thrown.expect(JsonMappingException.class);
        thrown.expectMessage("vote_count");
        mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_vote_count.json"), MovieWithExtraDTO.class);
    }

    @Test
    public void canDeserialiseMissingAlternativeTitles() throws Exception
    {
        MovieWithExtraDTO dto = mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_alternative_titles.json"), MovieWithExtraDTO.class);
        assertThat(dto.getAlternativeTitles()).isNull();
    }

    @Test
    public void canDeserialiseMissingCredits() throws Exception
    {
        MovieWithExtraDTO dto = mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_credits.json"), MovieWithExtraDTO.class);
        assertThat(dto.getCredits()).isNull();
    }

    @Test
    public void canDeserialiseMissingImages() throws Exception
    {
        MovieWithExtraDTO dto = mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_images.json"), MovieWithExtraDTO.class);
        assertThat(dto.getImages()).isNull();
    }

    @Test
    public void canDeserialiseMissingKeywords() throws Exception
    {
        MovieWithExtraDTO dto = mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_keywords.json"), MovieWithExtraDTO.class);
        assertThat(dto.getKeywords()).isNull();
    }

    @Test
    public void canDeserialiseMissingLists() throws Exception
    {
        MovieWithExtraDTO dto = mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_lists.json"), MovieWithExtraDTO.class);
        assertThat(dto.getLists()).isNull();
    }

    @Test
    public void canDeserialiseMissingReleases() throws Exception
    {
        MovieWithExtraDTO dto = mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_releases.json"), MovieWithExtraDTO.class);
        assertThat(dto.getReleases()).isNull();
    }

    @Test
    public void canDeserialiseMissingReviews() throws Exception
    {
        MovieWithExtraDTO dto = mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_reviews.json"), MovieWithExtraDTO.class);
        assertThat(dto.getReviews()).isNull();
    }

    @Test
    public void canDeserialiseMissingSimilar() throws Exception
    {
        MovieWithExtraDTO dto = mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_similar.json"), MovieWithExtraDTO.class);
        assertThat(dto.getSimilar()).isNull();
    }

    @Test
    public void canDeserialiseMissingTranslations() throws Exception
    {
        MovieWithExtraDTO dto = mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_translations.json"), MovieWithExtraDTO.class);
        assertThat(dto.getTranslations()).isNull();
    }

    @Test
    public void canDeserialiseMissingVideos() throws Exception
    {
        MovieWithExtraDTO dto = mapper.readValue(getClass().getResourceAsStream("movie_with_extra_dto_1_missing_videos.json"), MovieWithExtraDTO.class);
        assertThat(dto.getVideos()).isNull();
    }
}
