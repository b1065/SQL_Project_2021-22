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

public class ModyfikujPracownikaController extends AbstractController {
    @FXML Button buttonEdit;
    @FXML Button buttonBack;
    @FXML TextField fieldImie;
    @FXML TextField fieldNazwisko;
    @FXML TextField fieldStawka;
    Integer ID;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();
    Alert popup = new Alert(Alert.AlertType.CONFIRMATION,"Konto zostało zmodyfikowane poprawnie.", ButtonType.OK);
    Alert popup1 = new Alert(Alert.AlertType.WARNING,"Proszę podać imię, nazwisko oraz adres.", ButtonType.OK);
    Alert popup2 = new Alert(Alert.AlertType.ERROR,"Coś poszło nie tak! Konto nie zostało zmodyfikowane.", ButtonType.OK);
    Alert popup3 = new Alert(Alert.AlertType.ERROR,"Proszę podać poprawną kwotę stawki godzinowej.", ButtonType.OK);

    public ModyfikujPracownikaController() throws SQLException, ClassNotFoundException {
    }

    public void setID(Integer x) throws SQLException{
        ID = x;
        String imie = null, nazwisko = null;
        Double stawka = null;
        ResultSet rs;
        PreparedStatement statement = connectionSingleton.con.prepareStatement("SELECT imie, nazwisko, stawka_godzinowa FROM PPRACOWNICY WHERE id_prac = ?");
        statement.setInt(1,ID);
        rs = statement.executeQuery();
        while(rs.next()) {
            imie = rs.getString("IMIE");
            nazwisko = rs.getString("NAZWISKO");
            stawka = rs.getDouble("STAWKA_GODZINOWA");
        }
        fieldImie.setText(imie);
        fieldNazwisko.setText(nazwisko);
        fieldStawka.setText(stawka.toString());
    }

    @FXML protected void modyfikacjaKonta(ActionEvent event) throws SQLException, IOException {
        if(fieldImie.getText()==null||fieldImie.getText().trim().isEmpty()||fieldNazwisko.getText()==null
                ||fieldNazwisko.getText().trim().isEmpty()||fieldStawka.getText()==null||fieldStawka.getText().trim().isEmpty()){
            popup1.showAndWait();
        }
        else {
            try {
                CallableStatement statement = connectionSingleton.con.prepareCall("{call Procedury.ModyfikujPracownika(?,?,?,?)}");
                statement.setInt(1,ID);
                statement.setString(2, fieldImie.getText());
                statement.setString(3, fieldNazwisko.getText());
                statement.setDouble(4, Double.parseDouble(fieldStawka.getText()));
                statement.execute();
                statement.close();
                popup.showAndWait();
                toPracownicy(event);
            } catch (SQLException e) {
                popup2.showAndWait();
            } catch (NumberFormatException n){
                popup3.showAndWait();
                toSelf(event);
            }
        }
    }

    @FXML protected void toPracownicy(ActionEvent event) throws IOException, SQLException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/kierownik_pracownicy.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void toSelf(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/modyfikuj_pracownika.fxml"));
        Parent parent = loader.load();
        ModyfikujPracownikaController controller = loader.getController();
        controller.setID(ID);
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
