package com.example.sql_project;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Date;
import java.util.ResourceBundle;

public class HistoriaZamowienController extends AbstractController {
    @FXML Button buttonWstecz;
    @FXML TableView tabelka;
    @FXML TableColumn id;
    @FXML TableColumn czas;
    @FXML TableColumn cena;
    @FXML TableColumn stan;
    Integer ID;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();

    public HistoriaZamowienController() throws SQLException, ClassNotFoundException {
    }

    public static class Order {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty czas;
        private final SimpleDoubleProperty cena;
        private final SimpleStringProperty stan;

        private Order(Integer pid, String pczas, Double pcena, String pstan) {
            this.id = new SimpleIntegerProperty(pid);
            this.czas = new SimpleStringProperty(pczas);
            this.cena = new SimpleDoubleProperty(pcena);
            this.stan = new SimpleStringProperty(pstan);
        }

        public Integer getId() { return id.get(); }
        public void setId(Integer pid) {
            id.set(pid);
        }

        public String getCzas() {
            return czas.get();
        }
        public void setCzas(String pczas) {
            czas.set(pczas);
        }

        public Double getCena() {
            return cena.get();
        }
        public void setCena(Double pcena) {
            cena.set(pcena);
        }

        public String getStan() {
            return stan.get();
        }
        public void setStan(String pstan) {
            stan.set(pstan);
        }

    }

    public void setID(Integer x) throws SQLException, ClassNotFoundException {
        ID = x;
        try {
            PreparedStatement statement = connectionSingleton.con.prepareStatement("SELECT id_zam,czas_realizacji,cena,stan FROM ZAMOWIENIE WHERE id_klienta = ? ORDER BY id_zam");
            ResultSet rs;
            statement.setInt(1,ID);
            rs=statement.executeQuery();
            id.setCellValueFactory(new PropertyValueFactory<HistoriaZamowienController.Order, Integer>("id"));
            czas.setCellValueFactory(new PropertyValueFactory<HistoriaZamowienController.Order,String>("czas"));
            cena.setCellValueFactory(new PropertyValueFactory<HistoriaZamowienController.Order,Double>("cena"));
            stan.setCellValueFactory(new PropertyValueFactory<HistoriaZamowienController.Order,String>("stan"));
            tabelka.setItems(parseOrderList(rs));
            //tabelka.getColumns().addAll(id, imie, nazwisko, adres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<HistoriaZamowienController.Order> parseOrderList(ResultSet rs) throws SQLException {
        ObservableList<HistoriaZamowienController.Order> list = FXCollections.observableArrayList();
        Integer id;
        String czas;
        Double cena;
        String stan;
        while(rs.next()){
            id = rs.getInt("ID_ZAM");
            czas = rs.getString("CZAS_REALIZACJI");
            czas = czas.substring(2);
            cena = rs.getDouble("CENA");
            stan = rs.getString("STAN");
            HistoriaZamowienController.Order help = new HistoriaZamowienController.Order(id,czas,cena,stan);
            list.add(help);
        }
        return list;
    }

    @FXML protected void toMain(ActionEvent event) throws IOException, SQLException {
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
