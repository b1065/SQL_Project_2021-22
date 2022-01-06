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
import java.sql.Date;
import java.sql.SQLException;

public class NoweDanieController extends AbstractController{
    @FXML TextField nazwa;
    @FXML TextField czas;
    @FXML TextField cena;
    @FXML Button buttonDodaj;
    @FXML Button buttonPowrot;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();
    Alert popup = new Alert(Alert.AlertType.CONFIRMATION,"Danie zostało dodane poprawnie.", ButtonType.OK);
    Alert popup1 = new Alert(Alert.AlertType.WARNING,"Proszę podać nazw, cenę oraz czas przygotowania dania.", ButtonType.OK);
    Alert popup2 = new Alert(Alert.AlertType.ERROR,"Coś poszło nie tak! Danie nie zostało dodane.", ButtonType.OK);
    Alert popup3 = new Alert(Alert.AlertType.ERROR,"Proszę podać poprawną cenę oraz czas przygotowania\n(format czasu: HH:MM:SS).", ButtonType.OK);

    public NoweDanieController() throws SQLException, ClassNotFoundException {
    }

    @FXML protected void toMenu(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/kierownik_menu.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void toSelf(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/nowe_danie.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void dodawanieDania(ActionEvent event) throws IOException {
        if(nazwa.getText()==null||nazwa.getText().trim().isEmpty()||czas.getText()==null ||czas.getText().trim().isEmpty()||cena.getText()==null ||cena.getText().trim().isEmpty()){
            popup1.showAndWait();
        }
        else {
            try {
                String help = null;
                help = "00 "+czas.getText();
                CallableStatement statement = connectionSingleton.con.prepareCall("{call Procedury.DodajDanie(?,?,?)}");
                statement.setString(1, nazwa.getText());
                statement.setString(2, help);
                statement.setDouble(3,Double.parseDouble(cena.getText()));
                statement.execute();
                statement.close();
                popup.showAndWait();
                toMenu(event);
            } catch (SQLException e) {
                if(e.getErrorCode() == 1850 || e.getErrorCode() == 1867){
                    popup3.showAndWait();
                }
                else {
                    popup2.showAndWait();
                }
            } catch (NumberFormatException e) {
                popup3.showAndWait();
            }
        }
    }
}
