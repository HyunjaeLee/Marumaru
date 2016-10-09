import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ResourceBundle;
public class MainController implements Initializable {

    @FXML private ListView listView;
    @FXML private TextField textField;
    @FXML private ObservableList<Data> observableList = FXCollections.observableArrayList();

    private Collection<Data> searchData;
    private int page;

    public void daemon(Runnable runnable) {
        Thread daemon = new Thread(runnable);
        daemon.setDaemon(true);
        daemon.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        daemon(() -> {
            searchData = Marumaru.all();
            observableList.addAll(searchData);
        });

        listView.setItems(observableList);
        listView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {

            Data value = (Data) newValue;

            if(page == 0) {

                daemon(() -> {
                    Collection<Data> dataCollection = Marumaru.list(value.getUrl());
                    Platform.runLater(() -> observableList.setAll(dataCollection));
                });
                page = 1;

            } else {

                VBox vbox = new VBox();

                daemon(() -> {
                    java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF); // Silent
                    Collection<Data> dataCollection = Marumaru.images(value.getUrl());
                    Collection<Image> images = new LinkedList<>();
                    dataCollection.forEach(data -> {
                        try {
                            URLConnection connection = new URL(data.getUrl()).openConnection();
                            connection.addRequestProperty("User-Agent", "Mozilla");
                            images.add(new Image(connection.getInputStream()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    Platform.runLater(() -> {
                        images.forEach(image -> {
                            ImageView imageView = new ImageView(image);
                            vbox.getChildren().add(imageView);

                        });
                    });
                });

                ScrollPane scrollPane = new ScrollPane(vbox);

                Pane root = new BorderPane(scrollPane);

                Scene scene = new Scene(root, 500, 500);

                Stage stage = new Stage();
                stage.setTitle(((Data) newValue).getName());
                stage.setScene(scene);
                stage.show();

            }
        }));
    }

    public void handleSubmitButtonAction(ActionEvent actionEvent) {

        observableList.clear();
        daemon(() -> {
            searchData = Marumaru.search(textField.getText());
            Platform.runLater(() -> observableList.addAll(searchData));
        });
        page = 0;
    }

    public void handleBackButtonAction(ActionEvent actionEvent) {
        observableList.setAll(searchData);
        page = 0;
    }
}
