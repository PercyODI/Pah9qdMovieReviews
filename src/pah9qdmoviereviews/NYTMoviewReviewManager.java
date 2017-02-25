/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pah9qdmoviereviews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author pah9qd
 */
public class NYTMoviewReviewManager {

    private final String baseUrlString = "https://api.nytimes.com/svc/movies/v2/reviews/search.json";
    private final String apiKey = "74d25e46dcaa4fbc905160ac96eb0798";

    public ObservableList<NYTMovieReview> movieReviews;

    public NYTMoviewReviewManager() {
        movieReviews = FXCollections.observableArrayList();
    }

    public void searchApi(String searchString) throws Exception {
        if (searchString == null || searchString.isEmpty()) {
            throw new Exception("The search string was empty");
        }

        String encodedSearchString;
        try {
            encodedSearchString = URLEncoder.encode(searchString, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw ex;
        }

        URL url;
        try {
            url = new URL(baseUrlString + "?query=" + encodedSearchString + "&api-key=" + apiKey);
        } catch (MalformedURLException ex) {
            throw ex;
        }

        String jsonString = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                jsonString += inputLine;
            }
            in.close();
        } catch (IOException ex) {
            throw ex;
        }

        try {
            parseJsonMovieReview(jsonString);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void parseJsonMovieReview(String jsonString) throws Exception {
        movieReviews.clear();

        if (jsonString == null || jsonString.isEmpty()) {
            return;
        }

        JSONObject jsonObj;
        try {
            jsonObj = (JSONObject) JSONValue.parse(jsonString);
        } catch (Exception ex) {
            throw ex;
        }

        if (jsonObj == null) {
            return;
        }

        String status;
        try {
            status = (String) jsonObj.get("status");
        } catch (Exception ex) {
            throw ex;
        }

        if (status == null || !status.equals("OK")) {
            throw new Exception("Status from API not OK.");
        }

        JSONArray results;
        try {
            results = (JSONArray) jsonObj.get("results");
        } catch (Exception ex) {
            throw ex;
        }

        for (Object result : results) {
            try {
                JSONObject review = (JSONObject) result;
                String displayTitle = (String) review.getOrDefault("display_title", "");
                String mpaaRating = (String) review.getOrDefault("mpaa_rating", "");
                
                // Handle and parse the date
                Date openingDate = null;
                String openingDateStr = (String) review.get("opening_date");
                if(openingDateStr != null && !openingDateStr.isEmpty()) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD", Locale.ENGLISH);
                    openingDate = dateFormat.parse(openingDateStr);
                }
                
                // Handle and parse the url
                URL url = null;
                JSONObject link = (JSONObject) review.get("link");
                String urlStr = (String) link.get("url");
                if(urlStr != null && !urlStr.isEmpty())
                    url = new URL(urlStr);

                movieReviews.add(new NYTMovieReview(displayTitle, mpaaRating, openingDate, url));

            } catch (Exception ex) {
                throw ex;
            }

        }

    }

    public ObservableList<NYTMovieReview> getMovieReviews() {
        return movieReviews;
    }

    public int getNumMovieReviews() {
        return movieReviews.size();
    }
}
