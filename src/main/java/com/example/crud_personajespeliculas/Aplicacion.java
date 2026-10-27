package com.example.crud_personajespeliculas;

import Controladores.PersonajeControlador;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Aplicacion extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Aplicacion.class.getResource("PersonajeView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("CRUD Personajes MongoDB");
        stage.setScene(scene);
        stage.show();
        PersonajeControlador controller = fxmlLoader.getController();
        controller.iniciarDatos();
    }
}
