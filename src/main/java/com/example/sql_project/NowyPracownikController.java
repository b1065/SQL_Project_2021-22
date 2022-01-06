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
import java.sql.CallableStatement;
import java.sql.SQLException;

public class NowyPracownikController extends AbstractController {
    @FXML TextField imie;
    @FXML TextField nazwisko;
    @FXML Button buttonDodaj;
    @FXML Button buttonPowrot;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();
    Alert popup = new Alert(Alert.AlertType.CONFIRMATION,"Pracownik został dodany poprawnie.", ButtonType.OK);
    Alert popup1 = new Alert(Alert.AlertType.WARNING,"Proszę podać imię oraz nazwisko pracownika.", ButtonType.OK);
    Alert popup2 = new Alert(Alert.AlertType.ERROR,"Coś poszło nie tak! Pracownik nie został dodany.", ButtonType.OK);

    public NowyPracownikController() throws SQLException, ClassNotFoundException {
    }

    @FXML protected void toPracownicy(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/kierownik_pracownicy.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void dodawaniePracownika(ActionEvent event) throws IOException {
        if(imie.getText()==null||imie.getText().trim().isEmpty()||nazwisko.getText()==null ||nazwisko.getText().trim().isEmpty()){
            popup1.showAndWait();
        }
        else {
            try {
                CallableStatement statement = connectionSingleton.con.prepareCall("{call Procedury.DodajPracownika(?,?)}");
                statement.setString(1, imie.getText());
                statement.setString(2, nazwisko.getText());
                statement.execute();
                statement.close();
                popup.showAndWait();
            } catch (SQLException e) {
                popup2.showAndWait();
            }
            toPracownicy(event);
        }
    }
}
