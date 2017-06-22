package com.github.vysokyj.mkvmd;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import info.movito.themoviedbapi.*;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import net.coobird.thumbnailator.Thumbnails;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Engine {

    /**
     * The Movie Database API Key
     */
    public static final String TMDB_API_KEY = "ae74ea0a61729dbfee019982f35958a0";
    public static final String DEFAULT_LANG = "cs";
    /**
     * Base poster URI. The part w600 means width 600px.
     */
    public static final String POSTER_BASE_PATH = "http://image.tmdb.org/t/p/w" + Matroska.LARGE_COVER_PORT_WIDTH;


    private File file;
    private TmdbApi tmdbApi;
    private Map<Integer, MovieDb> movieResults;

    public Engine(File file) {
        this.file = file;
        this.tmdbApi = new TmdbApi(TMDB_API_KEY);
    }

    public Map<Integer, String> search(String name) {
        int i = 0;
        TmdbSearch tmdbSearch = tmdbApi.getSearch();
        MovieResultsPage movieResultsPage = tmdbSearch.searchMovie(name, 0, DEFAULT_LANG, true, 0);
        movieResults = new HashMap<>(movieResultsPage.getTotalResults());
        Map<Integer, String> stringResults = new HashMap<>(movieResultsPage.getTotalResults());
        for (MovieDb movieDb : movieResultsPage) {
            i++;
            movieResults.put(i, movieDb);
            stringResults.put(i, getMovieString(i, movieDb));
        }
        return stringResults;
    }

    public void save(Integer index) {
        MovieDb movieDb = movieResults.get(index);
        if (movieDb != null) try {
            processMovie(movieDb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMovieString(Integer index, MovieDb movieDb) {
        String in = index.toString();
        String t1 = movieDb.getTitle();
        String t2 = movieDb.getOriginalTitle();
        String ry = getYear(movieDb);
        return in + ": \"" + t1 + "\" / \""  + t2 + "\" / " + ry;
    }

    private String getYear(MovieDb movieDb) {
        String s = movieDb.getReleaseDate();
        if (s == null) return "????";
        else if (s.length() < 4) return s;
        else return s.substring(0, 4);
    }

    private File createLargeCover(File tempDir, MovieDb movieDb) throws Exception {
        String uri = POSTER_BASE_PATH + movieDb.getPosterPath() + "?api_key=" + Engine.TMDB_API_KEY;
        URL url = new URL(uri);
        File file = new File(tempDir, Matroska.LARGE_COVER_PORT_NAME);
        byte[] bytes = Resources.toByteArray(url);
        Files.write(bytes, file);
        return file;
    }

    private File createSmallCover(File tempDir, File largeCoverFile) throws Exception {
        File file = new File(tempDir, Matroska.SMALL_COVER_PORT_NAME);
        Thumbnails.of(largeCoverFile).width(Matroska.SMALL_COVER_PORT_WIDTH).toFile(file);
        return file;
    }

    private void processMovie(MovieDb movieDb) throws Exception {
        File tempDir = Files.createTempDir();
        File coverLargeFile = createLargeCover(tempDir, movieDb);
        File coverSmallFile = createSmallCover(tempDir, coverLargeFile);
        Matroska matroska = new Matroska(file);
        matroska.setTitle(movieDb.getTitle());
        matroska.setCoverFiles(coverLargeFile, coverSmallFile);
        matroska.write();
    }
}
