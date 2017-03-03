/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pah9qdmoviereviews;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javafx.application.Platform;
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

    // Allow Exceptions to move up to UI
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public NYTMoviewReviewManager() {
    }

    public void addPropertyChangeSupport(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeSupport(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void searchApi(String searchString) throws Exception {
        ApiTask apiTask = new ApiTask(searchString, baseUrlString, apiKey) {
            @Override
            public void runOnError(Exception ex) {
                propertyChangeSupport.firePropertyChange("Exception", null, ex);
            }

            @Override
            public void runOnSuccess(String jsonString) {
                try {
                    parseJsonMovieReview(jsonString);
                } catch (Exception ex) {
                    propertyChangeSupport.firePropertyChange("Exception", null, ex);
                }
            }
        };
        new Thread(apiTask).start();

    }

    private void parseJsonMovieReview(String jsonString) throws Exception {
        propertyChangeSupport.firePropertyChange("Clear Movie Reviews", null, null);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD", Locale.ENGLISH);

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
                NYTMovieReview movieReview = new NYTMovieReview();
                JSONObject review = (JSONObject) result;
                movieReview.setDisplayTitle((String) review.getOrDefault("display_title", ""));
                movieReview.setMpaaRating((String) review.getOrDefault("mpaa_rating", ""));
                movieReview.setHeadline((String) review.getOrDefault("headline", ""));
                movieReview.setSummary((String) review.getOrDefault("summary_short", ""));

                JSONObject link = (JSONObject) review.get("link");
                if (link != null) {
                    movieReview.setArticleLink((String) link.getOrDefault("url", ""));
                }

                JSONObject multimedia = (JSONObject) review.get("multimedia");
                if (multimedia != null) {
                    movieReview.setPictureLink((String) multimedia.getOrDefault("src", ""));
                }

                // Handle and parse the date
                String openingDateStr = (String) review.get("opening_date");
                if (openingDateStr != null && !openingDateStr.isEmpty()) {
                    movieReview.setOpeningDate(dateFormat.parse(openingDateStr));
                }

                String publicationDateStr = (String) review.get("publication_date");
                if (publicationDateStr != null && !publicationDateStr.isEmpty()) {
                    movieReview.setPublicationDate(dateFormat.parse(publicationDateStr));
                }

                propertyChangeSupport.firePropertyChange("Add Movie Review", null, movieReview);
            } catch (Exception ex) {
                throw ex;
            }
        }
        propertyChangeSupport.firePropertyChange("Completed", null, null);
    }
}
