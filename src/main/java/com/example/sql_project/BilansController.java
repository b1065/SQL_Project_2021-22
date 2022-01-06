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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.ResourceBundle;

public class BilansController extends AbstractController implements Initializable {
    @FXML Button buttonWstecz;
    @FXML TableView tabelka;
    @FXML TableColumn data;
    @FXML TableColumn koszt;
    @FXML TableColumn zysk;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();

    public BilansController() throws SQLException, ClassNotFoundException {
    }

    public static class Bilans {
        private final ObjectProperty<Date> data;
        private final SimpleDoubleProperty koszt;
        private final SimpleDoubleProperty zysk;

        private Bilans(Date pdata, Double pkoszt, Double pzysk) {
            this.data = new SimpleObjectProperty<>(pdata);
            this.koszt = new SimpleDoubleProperty(pkoszt);
            this.zysk = new SimpleDoubleProperty(pzysk);
        }

        public Date getData() {
            return data.get();
        }
        public void setData(Date pdata) {
            data.set(pdata);
        }

        public Double getKoszt() {
            return koszt.get();
        }
        public void setKoszt(Double pkoszt) {
            koszt.set(pkoszt);
        }

        public Double getZysk() {
            return zysk.get();
        }
        public void setZysk(Double pzysk) {
            zysk.set(pzysk);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Statement statement = connectionSingleton.con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM BILANS ORDER BY data");
            data.setCellValueFactory(new PropertyValueFactory<BilansController.Bilans, Date>("data"));
            koszt.setCellValueFactory(new PropertyValueFactory<BilansController.Bilans,Double>("koszt"));
            zysk.setCellValueFactory(new PropertyValueFactory<BilansController.Bilans,Double>("zysk"));
            tabelka.setItems(parseOrderList(rs));
            //tabelka.getColumns().addAll(id, imie, nazwisko, adres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<BilansController.Bilans> parseOrderList(ResultSet rs) throws SQLException {
        ObservableList<BilansController.Bilans> list = FXCollections.observableArrayList();
        Date data;
        Double koszt;
        Double zysk;
        while(rs.next()){
            data = rs.getDate("DATA");
            koszt = rs.getDouble("KOSZT");
            zysk = rs.getDouble("ZYSK");
            BilansController.Bilans help = new BilansController.Bilans(data,koszt,zysk);
            list.add(help);
        }
        return list;
    }

    @FXML protected void toMain(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/kierownik.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
