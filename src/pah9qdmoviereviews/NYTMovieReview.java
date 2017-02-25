/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pah9qdmoviereviews;

import java.net.URL;
import java.util.Date;

/**
 *
 * @author pah9qd
 */
public class NYTMovieReview {

    private String displayTitle;
    private String mpaaRating;
    private Date openingDate;
    private URL url;
    
    public NYTMovieReview(String displayTitle, String mpaaRating, Date openingDate, URL url) {
        this.displayTitle = displayTitle;
        this.mpaaRating = mpaaRating;
        this.openingDate = openingDate;
        this.url = url;
    }

    public String getDisplayTitle() {
        return displayTitle;
    }

    public void setDisplayTitle(String displayTitle) {
        this.displayTitle = displayTitle;
    }

    public String getMpaaRating() {
        return mpaaRating;
    }

    public void setMpaaRating(String mpaaRating) {
        this.mpaaRating = mpaaRating;
    }

    public Date getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(Date openingDate) {
        this.openingDate = openingDate;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
    
    @Override
    public String toString() {
        return displayTitle;
    }
}
