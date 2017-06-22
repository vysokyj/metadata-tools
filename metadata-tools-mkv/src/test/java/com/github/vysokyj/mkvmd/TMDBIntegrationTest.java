package com.github.vysokyj.mkvmd;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbSearch;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import org.junit.Test;

import static org.junit.Assert.*;

public class TMDBIntegrationTest {

    @Test
    public void testMovie() {
        TmdbApi tmdbApi = new TmdbApi(Engine.TMDB_API_KEY);
        TmdbMovies movies = tmdbApi.getMovies();
        MovieDb movie = movies.getMovie(5353, "en");
        assertNotNull(movie);

        TmdbSearch tmdbSearch = tmdbApi.getSearch();
        MovieResultsPage movieResultsPage = tmdbSearch.searchMovie("Vrchní prchni", 0, "cs", true, 0);

        int count = 0;
        for (MovieDb movieDb : movieResultsPage) {
            count++;
            //System.out.println(movieDb.getTitle() + " (" + movieDb.getReleaseDate().substring(0, 4) + ")");
            assertEquals("Vrchní, prchni!", movieDb.getTitle());
            assertEquals("1981", movieDb.getReleaseDate().substring(0, 4));

            System.out.println("http://image.tmdb.org/t/p/w600" + movieDb.getPosterPath() + "?api_key=" + Engine.TMDB_API_KEY);

        }

        assertEquals(1, count);


    }
}
