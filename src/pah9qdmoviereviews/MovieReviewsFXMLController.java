/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pah9qdmoviereviews;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author pah9qd
 */
public class MovieReviewsFXMLController implements Initializable {

    private Stage stage;
    private Scene scene;

    @FXML
    private ListView listView;
    @FXML
    private WebView webView;
    @FXML
    private TextField searchTextField;
    @FXML
    private Text foundText;

    private NYTMoviewReviewManager movieReviewManager;
//    private ObservableList<String> movieReviewItems;
    private WebEngine webEngine;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void ready(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;

        movieReviewManager = new NYTMoviewReviewManager();
//        movieReviewItems = FXCollections.observableArrayList();
        webEngine = webView.getEngine();
        listView.setItems(movieReviewManager.movieReviews);
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<NYTMovieReview>() {
            @Override
            public void changed(ObservableValue<? extends NYTMovieReview> observable, NYTMovieReview oldValue, NYTMovieReview newValue) {
                if (newValue != null)
                    webEngine.load(newValue.getUrl().toString());
            }
        });

        searchTextField.setOnKeyPressed((event) -> {
            if(event.getCode() == KeyCode.ENTER)
                loadReviews(searchTextField.getText());
        });
    }

    private void loadReviews(String searchString) {
        foundText.setText("");
        
        if(searchString == null || searchString.isEmpty()){
            displayErrorAlert("Search field cannot be blank. Please enter one or more words to search for.");
            return;
        }
        
        try {
            movieReviewManager.searchApi(searchString);
        } catch (Exception ex) {
            displayExceptionAlert(ex);
            return;
        }
        
        // Display found text
        foundText.setText("Found " + movieReviewManager.getNumMovieReviews() + " results for " + searchString + ".");
        
        //Scroll newslist back to top
        if(movieReviewManager.getNumMovieReviews() > 0) {
            listView.scrollTo(0);
        }

//        movieReviewItems.clear();
//        for (NYTMovieReview review : movieReviewManager.getMovieReviews()) {
//            movieReviewItems.add(review.getDisplayTitle());
//        }
    }
    
    private void displayErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error!");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void displayExceptionAlert(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception");
        alert.setHeaderText("An Exception Occurred!");
        alert.setContentText("An exception occurred.  View the exception information below by clicking Show Details.");

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    @FXML
    private void handleSearchBtn(ActionEvent e) {
        loadReviews(searchTextField.getText());
    }
    
    @FXML
    private void handleUpdateMenuBtn(ActionEvent e) {
        loadReviews(searchTextField.getText());
    }
}
