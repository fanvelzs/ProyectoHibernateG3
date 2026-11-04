package com.example.proyectohibernateg3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("libro-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 740, 580);
        stage.setTitle("Catálogo de libros");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        com.example.proyectohibernateg3.persistence.LibroRepository.close();
    }
}
