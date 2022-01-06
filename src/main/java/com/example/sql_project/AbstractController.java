package com.example.sql_project;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AbstractController {
    Alert popupnot = new Alert(Alert.AlertType.ERROR, "Niestety, tego jeszcze nie zaimplementowano :(", ButtonType.OK);

    @FXML
    protected void notImplemented(){
        popupnot.showAndWait();
    }
}
