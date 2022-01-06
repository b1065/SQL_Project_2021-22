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
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class ZadanieController extends AbstractController implements Initializable {
    @FXML Button buttonWstecz;
    @FXML Button buttonZakoncz;
    @FXML TableView tabelka;
    @FXML TableColumn zam;
    @FXML TableColumn prac;
    @FXML TableColumn przepis;
    @FXML TableColumn data;
    @FXML TableColumn stan;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();
    Alert popup1 = new Alert(Alert.AlertType.INFORMATION, "Pomyślnie zakończono zadanie.", ButtonType.OK);
    Alert popup2 = new Alert(Alert.AlertType.ERROR, "Nie wybrano zadania.", ButtonType.OK);
    Alert popup3 = new Alert(Alert.AlertType.ERROR, "Coś poszło nie tak! Zadanie nie zostało zakończone.", ButtonType.OK);
    Alert popup4 = new Alert(Alert.AlertType.WARNING,"Wybrane zadanie zostało już zrealizowane.",ButtonType.OK);

    public ZadanieController() throws SQLException, ClassNotFoundException {
    }

    public static class Task {
        private final SimpleIntegerProperty zam;
        private final SimpleIntegerProperty prac;
        private final SimpleIntegerProperty przepis;
        private final ObjectProperty<Timestamp> data;
        private final SimpleStringProperty stan;

        private Task(Integer pzam, Integer pprac, Integer pprzepis, Timestamp pdata, String pstan) {
            this.zam = new SimpleIntegerProperty(pzam);
            this.prac = new SimpleIntegerProperty(pprac);
            this.przepis = new SimpleIntegerProperty(pprzepis);
            this.data = new SimpleObjectProperty<>(pdata);
            this.stan = new SimpleStringProperty(pstan);
        }

        public Integer getZam() {
            return zam.get();
        }

        public void setZam(Integer pzam) {
            this.zam.set(pzam);
        }

        public Integer getPrac() {
            return prac.get();
        }

        public void setPrac(Integer pprac) {
            this.prac.set(pprac);
        }

        public Integer getPrzepis() {
            return przepis.get();
        }

        public void setPrzepis(Integer pprzepis) {
            this.przepis.set(pprzepis);
        }

        public Timestamp getData() {
            return data.get();
        }

        public void setData(Timestamp pdata) {
            this.data.set(pdata);
        }

        public String getStan() {
            return stan.get();
        }

        public void setStan(String pstan) {
            this.stan.set(pstan);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Statement statement = connectionSingleton.con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM ZADANIE ORDER BY data");
            zam.setCellValueFactory(new PropertyValueFactory<ZadanieController.Task, Integer>("zam"));
            prac.setCellValueFactory(new PropertyValueFactory<ZadanieController.Task, Integer>("prac"));
            przepis.setCellValueFactory(new PropertyValueFactory<ZadanieController.Task, Integer>("przepis"));
            data.setCellValueFactory(new PropertyValueFactory<ZadanieController.Task, Timestamp>("data"));
            stan.setCellValueFactory(new PropertyValueFactory<ZadanieController.Task, String>("stan"));
            tabelka.setItems(parseOrderList(rs));
            //tabelka.getColumns().addAll(id, imie, nazwisko, adres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<ZadanieController.Task> parseOrderList(ResultSet rs) throws SQLException {
        ObservableList<ZadanieController.Task> list = FXCollections.observableArrayList();
        Integer zam;
        Integer prac;
        Integer przepis;
        Timestamp data;
        String stan;
        while (rs.next()) {
            zam = rs.getInt("ID_ZAM");
            prac = rs.getInt("ID_PRAC");
            przepis = rs.getInt("ID_PRZEPISU");
            data = rs.getTimestamp("DATA");
            stan = rs.getString("STAN");
            ZadanieController.Task help = new ZadanieController.Task(zam, prac, przepis, data, stan);
            list.add(help);
        }
        return list;
    }

    @FXML
    protected void toMain(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/kierownik.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void toSelf(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/zadania.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void zakonczZadanie(ActionEvent event) throws IOException {
        if (tabelka.getSelectionModel().getSelectedItem() == null) {
            popup2.showAndWait();
        } else {
            Task help = (Task) tabelka.getSelectionModel().getSelectedItem();
            if(help.getStan().equals("ZREALIZOWANE")){
                popup4.showAndWait();
            } else {
                try {

                    CallableStatement statement1 = ConnectionSingleton.con.prepareCall("{call Procedury.ZmienStatusPracownika(?,?)}");
                    statement1.setInt(1, help.getPrac());
                    statement1.setString(2, "WOLNY");
                    statement1.execute();
                    statement1.close();
                    CallableStatement statement2 = ConnectionSingleton.con.prepareCall("{call Procedury.ZmienStatusZadania(?,?)}");
                    statement2.setInt(1, help.getZam());
                    statement2.setString(2, "ZREALIZOWANE");
                    statement2.execute();
                    statement2.close();
                    CallableStatement statement3 = ConnectionSingleton.con.prepareCall("{call Procedury.ZmienStatusZamowienia(?,?)}");
                    statement3.setInt(1, help.getZam());
                    statement3.setString(2, "ZREALIZOWANE");
                    statement3.execute();
                    statement3.close();
                    popup1.showAndWait();
                    toSelf(event);
                } catch (SQLException e) {
                    popup3.showAndWait();
                }
            }
        }
    }
}
