package com.example.sql_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class KlientController extends AbstractController {
    @FXML Button buttonWstecz;
    @FXML Button zamowienie;
    @FXML Button historia;
    @FXML Button modyfkuj;
    @FXML Button usun;
    @FXML Label witaj;
    Integer ID;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();
    Alert popup1 = new Alert(Alert.AlertType.INFORMATION,"Konto zostało poprawnie usunięte.", ButtonType.OK);
    Alert popup2 = new Alert(Alert.AlertType.ERROR,"Coś poszło nie tak! Konto nie zostało usunięte.", ButtonType.OK);

    public KlientController() throws SQLException, ClassNotFoundException {
    }

    public void setID(Integer x) throws SQLException {
        ID = x;
        String imie = null, nazwisko = null;
        ResultSet rs;
        PreparedStatement statement = connectionSingleton.con.prepareStatement("SELECT imie, nazwisko FROM KLIENT WHERE id_klienta = ?");
        statement.setInt(1,ID);
        rs = statement.executeQuery();
        while(rs.next()) {
            imie = rs.getString("IMIE");
            nazwisko = rs.getString("NAZWISKO");
        }
        witaj.setText("Witaj "+imie+" "+nazwisko+"!");
    }

    @FXML protected void usunKonto(ActionEvent event) throws IOException {
        try {
            CallableStatement statement = connectionSingleton.con.prepareCall("{call Procedury.UsunKlienta(?)}");
            statement.setInt(1, ID);
            statement.execute();
            popup1.showAndWait();
            toMain(event);
        }catch (SQLException e){
            popup2.showAndWait();
        }
    }

    @FXML protected void toMain(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/main_page.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void toModify(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/modyfikuj_konto.fxml"));
        Parent parent = loader.load();
        ModyfikujKontoController controller = loader.getController();
        controller.setID(ID);
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void toHistory(ActionEvent event) throws IOException, SQLException, ClassNotFoundException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/historia_zamowien.fxml"));
        Parent parent = loader.load();
        HistoriaZamowienController controller = loader.getController();
        controller.setID(ID);
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void toOrder(ActionEvent event) throws IOException, SQLException, ClassNotFoundException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/nowe_zamowienie.fxml"));
        Parent parent = loader.load();
        NoweZamowienieController controller = loader.getController();
        controller.setID(ID);
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
