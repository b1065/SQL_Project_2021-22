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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModyfikujKontoController extends AbstractController{
    @FXML Button buttonEdit;
    @FXML Button buttonBack;
    @FXML TextField fieldImie;
    @FXML TextField fieldNazwisko;
    @FXML TextField fieldAdres;
    Integer ID;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();
    Alert popup = new Alert(Alert.AlertType.CONFIRMATION,"Konto zostało zmodyfikowane poprawnie.", ButtonType.OK);
    Alert popup1 = new Alert(Alert.AlertType.WARNING,"Proszę podać imię, nazwisko oraz adres.", ButtonType.OK);
    Alert popup2 = new Alert(Alert.AlertType.ERROR,"Coś poszło nie tak! Konto nie zostało zmodyfikowane.", ButtonType.OK);

    public ModyfikujKontoController() throws SQLException, ClassNotFoundException {
    }

    public void setID(Integer x) throws SQLException{
        ID = x;
        String imie = null, nazwisko = null, adres = null;
        ResultSet rs;
        PreparedStatement statement = connectionSingleton.con.prepareStatement("SELECT imie, nazwisko, adres FROM KLIENT WHERE id_klienta = ?");
        statement.setInt(1,ID);
        rs = statement.executeQuery();
        while(rs.next()) {
            imie = rs.getString("IMIE");
            nazwisko = rs.getString("NAZWISKO");
            adres = rs.getString("ADRES");
        }
        fieldImie.setText(imie);
        fieldNazwisko.setText(nazwisko);
        fieldAdres.setText(adres);
    }

    @FXML protected void modyfikacjaKonta(ActionEvent event) throws SQLException, IOException {
        if(fieldImie.getText()==null||fieldImie.getText().trim().isEmpty()||fieldNazwisko.getText()==null
                ||fieldNazwisko.getText().trim().isEmpty()||fieldAdres.getText()==null||fieldAdres.getText().trim().isEmpty()){
            popup1.showAndWait();
        }
        else {
            try {
                CallableStatement statement = connectionSingleton.con.prepareCall("{call Procedury.ModyfikujKlienta(?,?,?,?)}");
                statement.setInt(1,ID);
                statement.setString(2, fieldImie.getText());
                statement.setString(3, fieldNazwisko.getText());
                statement.setString(4, fieldAdres.getText());
                statement.execute();
                statement.close();
                popup.showAndWait();
            } catch (SQLException e) {
                popup2.showAndWait();
            }
            toKlient(event);
        }
    }

    @FXML protected void toKlient(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/klient.fxml"));
        Parent parent = loader.load();
        KlientController controller = loader.getController();
        controller.setID(ID);
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
