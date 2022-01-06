package com.example.sql_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class NoweKontoController extends AbstractController {
    @FXML TextField imie;
    @FXML TextField nazwisko;
    @FXML TextField adres;
    @FXML Button buttonZaloz;
    @FXML Button buttonPowrot;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();
    Alert popup = new Alert(Alert.AlertType.CONFIRMATION,"Konto zostało założone poprawnie.", ButtonType.OK);
    Alert popup1 = new Alert(Alert.AlertType.WARNING,"Proszę podać imię, nazwisko oraz adres.", ButtonType.OK);
    Alert popup2 = new Alert(Alert.AlertType.ERROR,"Coś poszło nie tak! Konto nie zostało założone.", ButtonType.OK);

    public NoweKontoController() throws SQLException, ClassNotFoundException {
    }

    @FXML protected void toKlient(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/klient_logowanie.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    @FXML protected void zakladanieKonta(ActionEvent event) throws IOException {
        if(imie.getText()==null||imie.getText().trim().isEmpty()||nazwisko.getText()==null
                ||nazwisko.getText().trim().isEmpty()||adres.getText()==null||adres.getText().trim().isEmpty()){
            popup1.showAndWait();
        }
        else {
            try {
                CallableStatement statement = connectionSingleton.con.prepareCall("{call Procedury.DodajKlienta(?,?,?)}");
                statement.setString(1, imie.getText());
                statement.setString(2, nazwisko.getText());
                statement.setString(3, adres.getText());
                statement.execute();
                statement.close();
                popup.showAndWait();
            } catch (SQLException e) {
                popup2.showAndWait();
            }
            toKlient(event);
        }
    }
}
