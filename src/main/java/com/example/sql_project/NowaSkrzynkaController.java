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
import java.sql.Types;

public class NowaSkrzynkaController extends AbstractController{
    @FXML TextField fieldProducent;
    @FXML TextField fieldNazwa;
    @FXML TextField fieldCena;
    @FXML TextField fieldIlosc;
    @FXML Button buttonDodaj;
    @FXML Button buttonPowrot;
    Exception zero;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();
    Alert popup = new Alert(Alert.AlertType.CONFIRMATION,"Skrzynka została dodana poprawnie.", ButtonType.OK);
    Alert popup1 = new Alert(Alert.AlertType.WARNING,"Proszę podać wszystkie z wymienionych:\n- producenta,\n- nazwę składnika w skrzynce\n- cenę jednostkową za składnik\n- ilość składników w skrzynce.", ButtonType.OK);
    Alert popup2 = new Alert(Alert.AlertType.ERROR,"Coś poszło nie tak! Skrzynka nie została dodana.", ButtonType.OK);
    Alert popup3 = new Alert(Alert.AlertType.WARNING, "Proszę podać poprawną ilość oraz cenę.", ButtonType.OK);

    public NowaSkrzynkaController() throws SQLException, ClassNotFoundException {
    }

    public void setSkladnik(String x, Integer y){
        fieldNazwa.setText(x);
        fieldIlosc.setText(y.toString());
    }

    @FXML protected void toMagazyn(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/kierownik_magazyn.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void dodawanieSkrzynki(ActionEvent event) throws Exception {
        if(fieldNazwa.getText()==null||fieldNazwa.getText().trim().isEmpty()||
                fieldProducent.getText()==null||fieldProducent.getText().trim().isEmpty()||
                fieldCena.getText()==null||fieldCena.getText().trim().isEmpty()||
                fieldIlosc.getText()==null||fieldIlosc.getText().trim().isEmpty()){
            popup1.showAndWait();
        }
        else {
            try {
                if(Double.parseDouble(fieldCena.getText()) < 0){
                    throw zero;
                }
                if(Integer.parseInt(fieldIlosc.getText()) < 0){
                    throw zero;
                }
                CallableStatement statement = connectionSingleton.con.prepareCall("{call Procedury.DodajSkrzynke(?,?,?,?)}");
                statement.setString(1, fieldProducent.getText());
                statement.setString(2, fieldNazwa.getText());
                statement.setInt(3,Integer.parseInt(fieldIlosc.getText()));
                statement.setDouble(4,Double.parseDouble(fieldCena.getText()));
                statement.execute();
                statement.close();
                popup.showAndWait();
                toMagazyn(event);
            } catch (SQLException e) {
                e.printStackTrace();
                popup2.showAndWait();
            } catch (NumberFormatException e) {
                popup3.showAndWait();
            } catch (Exception e){
                popup3.showAndWait();
            }
        }
    }
}
