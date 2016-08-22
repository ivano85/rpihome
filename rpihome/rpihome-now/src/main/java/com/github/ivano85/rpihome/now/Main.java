package com.github.ivano85.rpihome.now;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import net.objectof.actof.widgets.masonry.MasonryPane;

public class Main extends Application {
    
    public static void main (String[] args) {
        launch(args);
    }
    
    private Stage primaryStage;
    private MasonryPane rootLayout;
    private CardsController cardsController;
    
    @Override
    public void start(Stage stage) throws Exception {
        
        primaryStage = stage;
        primaryStage.setTitle("Home");
        
        initRootLayout();
        
        rootLayout.setSpacing(10.0);
        
        cardsController.addCard(new Card(Main.class.getClassLoader(), "scenes/weather.fxml"));
        cardsController.addCard(new Card(Main.class.getClassLoader(), "scenes/test.fxml"));
        cardsController.addCard(new Card(Main.class.getClassLoader(), "scenes/test.fxml"));
        cardsController.addCard(new Card(Main.class.getClassLoader(), "scenes/test.fxml"));
        cardsController.addCard(new Card(Main.class.getClassLoader(), "scenes/test.fxml"));
        cardsController.addCard(new Card(Main.class.getClassLoader(), "scenes/test.fxml"));
        
    }
    
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/scenes/main.fxml"));
            ScrollPane scrollPane = (ScrollPane) loader.load();
            cardsController = loader.getController();
            rootLayout = (MasonryPane) scrollPane.getContent();
//            rootLayout.prefWidthProperty().bind(scrollPane.prefWidthProperty());
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(scrollPane);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
