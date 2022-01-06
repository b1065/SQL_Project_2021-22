package com.example.sql_project;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
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

public class FrekwencjaController extends AbstractController implements Initializable {
    @FXML Button buttonWstecz;
    @FXML Button buttonZakoncz;
    @FXML TableView tabelka;
    @FXML TableColumn id;
    @FXML TableColumn pocz;
    @FXML TableColumn kon;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();
    Alert popup = new Alert(Alert.AlertType.INFORMATION,"Pomyślnie zakończono wszystkie zmiany.", ButtonType.OK);

    public FrekwencjaController() throws SQLException, ClassNotFoundException {
    }

    public static class Frek {
        private final ObjectProperty<Timestamp> pocz;
        private final ObjectProperty<Timestamp> kon;
        private final SimpleIntegerProperty id;

        private Frek(Integer pid, Timestamp ppocz, Timestamp pkon) {
            this.id = new SimpleIntegerProperty(pid);
            this.pocz = new SimpleObjectProperty<>(ppocz);
            this.kon = new SimpleObjectProperty<>(pkon);
        }

        public Integer getId() {
            return id.get();
        }
        public void setId(Integer pid) {
            id.set(pid);
        }

        public Timestamp getPocz() {
            return pocz.get();
        }
        public void setPocz(Timestamp ppocz) {
            pocz.set(ppocz);
        }

        public Timestamp getKon() {
            return kon.get();
        }
        public void setKon(Timestamp pkon) {
            kon.set(pkon);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Statement statement = connectionSingleton.con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM FREKWENCJA ORDER BY id_prac");
            id.setCellValueFactory(new PropertyValueFactory<FrekwencjaController.Frek, Integer>("id"));
            pocz.setCellValueFactory(new PropertyValueFactory<FrekwencjaController.Frek, Timestamp>("pocz"));
            kon.setCellValueFactory(new PropertyValueFactory<FrekwencjaController.Frek, Timestamp>("kon"));
            tabelka.setItems(parseOrderList(rs));
            //tabelka.getColumns().addAll(id, imie, nazwisko, adres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<FrekwencjaController.Frek> parseOrderList(ResultSet rs) throws SQLException {
        ObservableList<FrekwencjaController.Frek> list = FXCollections.observableArrayList();
        Integer id;
        Timestamp pocz;
        Timestamp kon;
        while(rs.next()){
            pocz = rs.getTimestamp("POCZATEK_ZMIANY");
            kon = rs.getTimestamp("KONIEC_ZMIANY");
            id = rs.getInt("ID_PRAC");
            FrekwencjaController.Frek help = new FrekwencjaController.Frek(id,pocz,kon);
            list.add(help);
        }
        return list;
    }

    @FXML protected void zakonczZmiany(ActionEvent event) throws SQLException, IOException {
        CallableStatement statement = ConnectionSingleton.con.prepareCall("{call Procedury.ZakonczWszystkieZmiany}");
        statement.execute();
        statement.close();
        popup.showAndWait();
        toSelf(event);
    }

    @FXML protected void toMain(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/kierownik.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void toSelf(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/frekwencja.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
