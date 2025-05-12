package com.zakatfitrah;

import javafx.application.Application;
import com.zakatfitrah.utils.DatabaseInitializer;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        try {
            System.out.println("Memulai aplikasi...");
            DatabaseInitializer.initializeDatabase();
    
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            Scene scene = new Scene(loader.load());
    
            stage.setTitle("Aplikasi Zakat Fitrah Masjid");
            Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));
            stage.getIcons().add(logo);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); // Tampilkan error ke console
        }
    }
    

    public static void main(String[] args) {
        launch(args);
    }
}
