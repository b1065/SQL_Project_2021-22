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

public class ModyfikujDanieController extends AbstractController {
    @FXML Button buttonEdit;
    @FXML Button buttonBack;
    @FXML TextField fieldNazwa;
    @FXML TextField fieldCzas;
    @FXML TextField fieldCena;
    Integer ID;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();
    Alert popup = new Alert(Alert.AlertType.CONFIRMATION,"Danie zostało zmodyfikowane poprawnie.", ButtonType.OK);
    Alert popup1 = new Alert(Alert.AlertType.WARNING,"Proszę podać nazwę, czas przygotowania oraz cenę.", ButtonType.OK);
    Alert popup2 = new Alert(Alert.AlertType.ERROR,"Coś poszło nie tak! Danie nie zostało zmodyfikowane.", ButtonType.OK);
    Alert popup3 = new Alert(Alert.AlertType.ERROR,"Proszę podać poprawny czas przygotowania oraz cenę.", ButtonType.OK);

    public ModyfikujDanieController() throws SQLException, ClassNotFoundException {
    }

    public void setID(Integer x) throws SQLException{
        ID = x;
        String nazwa = null;
        String czas = null;
        Double cena = null;
        ResultSet rs;
        PreparedStatement statement = connectionSingleton.con.prepareStatement("SELECT nazwa, czas_przygotowania, cena FROM DANIE WHERE id_dania = ?");
        statement.setInt(1,ID);
        rs = statement.executeQuery();
        while(rs.next()) {
            nazwa = rs.getString("NAZWA");
            czas = rs.getString("CZAS_PRZYGOTOWANIA");
            cena = rs.getDouble("CENA");
        }
        fieldNazwa.setText(nazwa);
        fieldCzas.setText(czas.substring(2,czas.length()-2).toString());
        fieldCena.setText(cena.toString());
    }

    @FXML protected void modyfikacjaDania(ActionEvent event) throws SQLException, IOException {
        if(fieldNazwa.getText()==null ||fieldNazwa.getText().trim().isEmpty()||fieldCzas.getText()==null||fieldCzas.getText().trim().isEmpty()||fieldCena.getText()==null||fieldCena.getText().trim().isEmpty()){
            popup1.showAndWait();
        }
        else {
            try {
                String help = null;
                help = "00 "+fieldCzas.getText();
                CallableStatement statement = connectionSingleton.con.prepareCall("{call Procedury.ModyfikujDanie(?,?,?,?)}");
                statement.setInt(1,ID);
                statement.setString(2, fieldNazwa.getText());
                statement.setString(3, help);
                statement.setDouble(4,Double.parseDouble(fieldCena.getText()));
                statement.execute();
                statement.close();
                popup.showAndWait();
                toMenu(event);
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

    @FXML protected void toMenu(ActionEvent event) throws IOException, SQLException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/kierownik_menu.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void toSelf(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/modyfikuj_danie.fxml"));
        Parent parent = loader.load();
        ModyfikujPracownikaController controller = loader.getController();
        controller.setID(ID);
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
