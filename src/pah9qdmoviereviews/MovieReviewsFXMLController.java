/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pah9qdmoviereviews;

import java.awt.Desktop;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringEscapeUtils;

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
    private TextField searchTextField;
    @FXML
    private Text foundText;
    @FXML
    private ImageView movieImage;
    @FXML
    private VBox detailsBox;
    @FXML
    private HBox reviewBox;
    
    private final Text missingImageText = new Text("No Image");

    private String searchString;
    private NYTMoviewReviewManager movieReviewManager;
    public ObservableList<NYTMovieReview> movieReviews;

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
        movieReviewManager.addPropertyChangeSupport(((evt) -> {
            switch (evt.getPropertyName()) {
                case "Exception":
                    Platform.runLater(() -> displayExceptionAlert((Exception) evt.getNewValue()));
                    break;
                case "Add Movie Review":
                    Platform.runLater(() -> {
                        movieReviews.add((NYTMovieReview) evt.getNewValue());
                        this.foundText.setText(("Found " + movieReviews.size() + " results for " + searchString + "."));
                    });
                    break;
                case "Clear Movie Reviews":
                    Platform.runLater(() -> movieReviews.clear());
                    break;
                case "Completed":
                    if(movieReviews.isEmpty())
                        this.foundText.setText("No reviews found for " + searchString);
                    break;
                default:
                    Platform.runLater(() -> displayExceptionAlert(new Exception("Invalid Property Change Support Property")));
                    break;
            }

        }));

        movieReviews = FXCollections.observableArrayList();
        listView.setItems(movieReviews);
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<NYTMovieReview>() {
            @Override
            public void changed(ObservableValue<? extends NYTMovieReview> observable, NYTMovieReview oldValue, NYTMovieReview newValue) {
                detailsBox.getChildren().clear();
                movieImage.setImage(null);
                if (newValue != null) {
                    DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                    String pictureLink = newValue.getPictureLink();
                    if (pictureLink != null && !pictureLink.isEmpty()) {
                        reviewBox.getChildren().remove(0);
                        reviewBox.getChildren().add(0, movieImage);
                        movieImage.setImage(new Image(pictureLink));
                    } else {
                        reviewBox.getChildren().remove(0);
                        reviewBox.getChildren().add(0, missingImageText);
                        movieImage.setImage(null);
                    }

                    ArrayList<Label> labelArray = new ArrayList<>();
                    if (!newValue.getDisplayTitle().isEmpty()) {
                        Label titleLabel = new Label(newValue.getDisplayTitle());
                        titleLabel.fontProperty().set(Font.font(20));
                        labelArray.add(titleLabel);
                    }
                    if (!newValue.getSummary().isEmpty()) {
                        labelArray.add(new Label("Summary: " + newValue.getSummary()));
                    }
                    if (!newValue.getMpaaRating().isEmpty()) {
                        labelArray.add(new Label("MPAA Rating: " + newValue.getMpaaRating()));
                    }
                    if (newValue.getPublicationDate() != null) {
                        labelArray.add(new Label("Publication Date: " + dateFormat.format(newValue.getPublicationDate())));
                    }
                    if (newValue.getOpeningDate() != null) {
                        labelArray.add(new Label("Opening Date: " + dateFormat.format(newValue.getOpeningDate())));
                    }

                    labelArray.forEach((label) -> {
                        label.wrapTextProperty().set(true);
                    });
                    
                    detailsBox.getChildren().addAll(labelArray);

                    Button openButton = new Button("Open Review");
                    openButton.setOnAction( (event) -> {
                        try {
                            Desktop.getDesktop().browse(new URI(newValue.getArticleLink()));
                        } catch (Exception ex) {
                            displayExceptionAlert(ex);
                        }
                    });
                    
                    detailsBox.getChildren().add(openButton);
                }
            }
        });

        HBox.setMargin(missingImageText, new Insets(15, 15, 15, 15));
        searchTextField.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                loadReviews(searchTextField.getText());
            }
        });
//        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
//            if(newValue != null && !newValue.isEmpty())
//                loadReviews(newValue);
//        });
    }

    private void loadReviews(String searchString) {
        this.searchString = searchString;
        if (searchString == null || searchString.isEmpty()) {
            displayErrorAlert("Search field cannot be blank. Please enter one or more words to search for.");
            return;
        }

        foundText.setText("Searching for " + searchString + "...");
        try {
            movieReviewManager.searchApi(searchString);
        } catch (Exception ex) {
            displayExceptionAlert(ex);
            return;
        }

        //Scroll newslist back to top
        if (movieReviews.size() > 0) {
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

    public void displayExceptionAlert(Exception ex) {
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

    private void displayAboutAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("New York Times Moview Review Viewer");
        alert.setContentText("An application for searching and viewing movie reviews from the New York Times. Developed by Pearse Hutson.");

        alert.showAndWait();
    }

    @FXML
    private void handleSearchBtn(ActionEvent e) {
        loadReviews(searchTextField.getText());
    }

    private void handleUpdateMenuBtn(ActionEvent e) {
        loadReviews(searchTextField.getText());
    }

    @FXML
    private void handleAboutMenuBtn(ActionEvent e) {
        displayAboutAlert();
    }
    
}
