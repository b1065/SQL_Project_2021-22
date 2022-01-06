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
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class PracownikController extends AbstractController {
    @FXML Button buttonWstecz;
    @FXML Button buttonRozpocznij;
    @FXML Button buttonZakoncz;
    @FXML Label witaj;
    Integer ID;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();
    Alert popup1 = new Alert(Alert.AlertType.INFORMATION, "Pomyślnie rozpoczęto zmianę. Miłego dnia!", ButtonType.NO);
    Alert popup2 = new Alert(Alert.AlertType.INFORMATION, "Pomyślnie zakończono zmianę. Do widzenia!", ButtonType.OK);
    Alert popup3 = new Alert(Alert.AlertType.ERROR, "Twoja zmiana jest już rozpoczęta!", ButtonType.OK);
    Alert popup4 = new Alert(Alert.AlertType.ERROR, "Nie możesz zakończyć nie rozpoczętej zmiany!", ButtonType.OK);
    Alert popup5 = new Alert(Alert.AlertType.ERROR, "Coś poszło nie tak! Spróbuj ponownie później.", ButtonType.OK);
    public PracownikController() throws SQLException, ClassNotFoundException {
    }

    public void setID(Integer x) throws SQLException {
        ID = x;
        String imie = null, nazwisko = null;
        ResultSet rs;
        PreparedStatement statement = connectionSingleton.con.prepareStatement("SELECT imie, nazwisko FROM PPRACOWNICY WHERE id_prac = ?");
        statement.setInt(1,ID);
        rs = statement.executeQuery();
        while(rs.next()) {
            imie = rs.getString("IMIE");
            nazwisko = rs.getString("NAZWISKO");
        }
        witaj.setText("Witaj "+imie+" "+nazwisko+"!");
    }

    @FXML protected void toMain(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/main_page.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void rozpocznijZmiane(ActionEvent event){
        Integer czyGit;
        try {
            CallableStatement statement = ConnectionSingleton.con.prepareCall("{? = call Funkcje.ZnajdzFrekwencje(?)");
            statement.setInt(2,ID);
            statement.registerOutParameter(1, Types.INTEGER);
            statement.execute();
            czyGit = statement.getInt(1);
            statement.close();
            if(czyGit == 1){
                popup3.showAndWait();
            }
            else{
                CallableStatement statement1 = ConnectionSingleton.con.prepareCall("{call Procedury.ZacznijZmiane(?)}");
                statement1.setInt(1,ID);
                statement1.execute();
                statement1.close();
                popup1.showAndWait();
            }
        } catch (SQLException e) {
            popup5.showAndWait();
        }
    }

    @FXML protected void zakonczZmiane(ActionEvent event){
        Integer czyGit;
        try {
            CallableStatement statement = ConnectionSingleton.con.prepareCall("{? = call Funkcje.ZnajdzFrekwencje(?)");
            statement.setInt(2,ID);
            statement.registerOutParameter(1, Types.INTEGER);
            statement.execute();
            czyGit = statement.getInt(1);
            statement.close();
            if(czyGit == 0){
                popup4.showAndWait();
            }
            else{
                CallableStatement statement1 = ConnectionSingleton.con.prepareCall("{call Procedury.ZakonczZmiane(?)}");
                statement1.setInt(1,ID);
                statement1.execute();
                statement1.close();
                popup2.showAndWait();
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            popup5.showAndWait();
        }
    }
}
