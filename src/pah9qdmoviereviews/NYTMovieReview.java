/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pah9qdmoviereviews;

import java.net.URL;
import java.util.Date;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author pah9qd
 */
public class NYTMovieReview {

    private String displayTitle = "";
    private String mpaaRating = "";
    private String headline = "";
    private String summary = "";
    private Date publicationDate;
    private Date openingDate;
    private String articleLink = "";
    private String pictureLink = "";
    
    public NYTMovieReview() {
        
    }

    public String getDisplayTitle() {
        return displayTitle;
    }

    public void setDisplayTitle(String displayTitle) {
        this.displayTitle = StringEscapeUtils.unescapeHtml4(displayTitle);
    }

    public String getMpaaRating() {
        return mpaaRating;
    }

    public void setMpaaRating(String mpaaRating) {
        this.mpaaRating = StringEscapeUtils.unescapeHtml4(mpaaRating);
    }

    public Date getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(Date openingDate) {
        this.openingDate = openingDate;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = StringEscapeUtils.unescapeHtml4(headline);
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = StringEscapeUtils.unescapeHtml4(summary);
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getArticleLink() {
        return articleLink;
    }

    public void setArticleLink(String articleLink) {
        this.articleLink = articleLink;
    }

    public String getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }
    
    @Override
    public String toString() {
        return headline;
    }
}
