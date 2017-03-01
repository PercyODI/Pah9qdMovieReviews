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

/**
 *
 * @author pah9qd
 */
public abstract class ApiTask implements Runnable {

    private String searchString;
    private String baseUrlString;
    private String apiKey;
    
    public ApiTask(String searString, String baseUrlString, String apiKey) {
        this.searchString = searString;
        this.baseUrlString = baseUrlString;
        this.apiKey = apiKey;
    }
    
    @Override
    public void run() {
        if (searchString == null || searchString.isEmpty()) {
            runOnError(new Exception("The search string was empty"));
            return;
        }

        String encodedSearchString;
        try {
            encodedSearchString = URLEncoder.encode(searchString, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            runOnError(ex);
            return;
        }

        URL url;
        try {
            url = new URL(baseUrlString + "?query=" + encodedSearchString + "&api-key=" + apiKey);
        } catch (MalformedURLException ex) {
            runOnError(ex);
            return;
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
            runOnError(ex);
        }
        
        runOnSuccess(jsonString);
    }
    
    public abstract void runOnError(Exception ex);
    public abstract void runOnSuccess(String jsonString);
}
