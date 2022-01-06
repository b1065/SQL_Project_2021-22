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

public class ModyfikujSkrzynkeController extends AbstractController{
    @FXML Button buttonEdit;
    @FXML Button buttonBack;
    @FXML TextField fieldCena;
    @FXML TextField fieldIlosc;
    String producent,nazwa_skladnika;
    Date data_dodania;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();
    Alert popup = new Alert(Alert.AlertType.CONFIRMATION,"Skrzynka została zmodyfikowana poprawnie.", ButtonType.OK);
    Alert popup1 = new Alert(Alert.AlertType.WARNING,"Proszę podać cenę oraz ilość.", ButtonType.OK);
    Alert popup2 = new Alert(Alert.AlertType.ERROR,"Coś poszło nie tak! Skrzynka nie została zmodyfikowana.", ButtonType.OK);
    Alert popup3 = new Alert(Alert.AlertType.ERROR,"Proszę podać poprawną ceną oraz ilość.", ButtonType.OK);

    public ModyfikujSkrzynkeController() throws SQLException, ClassNotFoundException {
    }

    public void setSkrzynka(String x, String y, Date z) throws SQLException{
        producent = x;
        nazwa_skladnika = y;
        data_dodania = z;
        ResultSet rs;
        String prod=null,nazwa=null;
        Integer ilosc=null;
        Double cena=null;
        Date data=null;
        PreparedStatement statement = connectionSingleton.con.prepareStatement("SELECT * FROM SKRZYNKA WHERE producent = ? AND nazwa_produktu = ? AND data = ?");
        statement.setString(1,producent);
        statement.setString(2,nazwa_skladnika);
        statement.setDate(3, data_dodania);
        rs = statement.executeQuery();
        while(rs.next()) {
            prod = rs.getString("PRODUCENT");
            nazwa = rs.getString("NAZWA_PRODUKTU");
            cena = rs.getDouble("CENA");
            ilosc = rs.getInt("ILOSC");
            data = rs.getDate("DATA");
        }
        fieldCena.setText(String.valueOf(cena));
        fieldIlosc.setText(String.valueOf(ilosc));
    }

    @FXML protected void modyfikacjaSkrzynki(ActionEvent event) throws SQLException, IOException {
        if(fieldCena.getText()==null|| fieldCena.getText().trim().isEmpty()||
                fieldIlosc.getText()==null ||fieldIlosc.getText().trim().isEmpty()){
            popup1.showAndWait();
        }
        else {
            try {
                CallableStatement statement = connectionSingleton.con.prepareCall("{call Procedury.ModyfikujSkrzynke(?,?,?,?,?)}");
                statement.setString(1,producent);
                statement.setString(2,nazwa_skladnika);
                statement.setDate(3,data_dodania);
                statement.setInt(4, Integer.parseInt(fieldIlosc.getText()));
                statement.setDouble(5, Double.parseDouble(fieldCena.getText()));
                statement.execute();
                statement.close();
                popup.showAndWait();
                toMagazyn(event);
            } catch (SQLException e) {
                popup2.showAndWait();
            } catch (NumberFormatException e){
                popup3.showAndWait();
                toSelf(event);
            } catch (IllegalArgumentException e){
                System.out.println("Problem z konwersją daty!");
            }
        }
    }

    @FXML protected void toMagazyn(ActionEvent event) throws IOException, SQLException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/kierownik_magazyn.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void toSelf(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/modyfikuj_skrzynke.fxml"));
        Parent parent = loader.load();
        ModyfikujSkrzynkeController controller = loader.getController();
        controller.setSkrzynka(producent,nazwa_skladnika,data_dodania);
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
