package com.example.sql_project;

import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class KlientLogowanieController extends AbstractController implements Initializable {
    @FXML Button buttonWstecz;
    @FXML Button buttonZaloguj;
    @FXML Button buttonZalozKonto;
    @FXML TableView tabelka;
    @FXML TableColumn id;
    @FXML TableColumn imie;
    @FXML TableColumn nazwisko;
    @FXML TableColumn adres;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();
    Alert popup = new Alert(Alert.AlertType.ERROR, "Nie wybrano konta.", ButtonType.OK);

    public static class User {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty imie;
        private final SimpleStringProperty nazwisko;
        private final SimpleStringProperty adres;

        private User(Integer pid, String pimie, String pnazwisko, String padres) {
            this.id = new SimpleIntegerProperty(pid);
            this.imie = new SimpleStringProperty(pimie);
            this.nazwisko = new SimpleStringProperty(pnazwisko);
            this.adres = new SimpleStringProperty(padres);
        }

        public Integer getId() { return id.get(); }
        public void setId(Integer pid) {
            id.set(pid);
        }

        public String getImie() {
            return imie.get();
        }
        public void setImie(String pimie) {
            imie.set(pimie);
        }

        public String getNazwisko() {
            return nazwisko.get();
        }
        public void setNazwisko(String pnazwisko) {
            nazwisko.set(pnazwisko);
        }

        public String getAdres() {
            return adres.get();
        }
        public void setAdres(String padres) {
            adres.set(padres);
        }

    }
    public KlientLogowanieController() throws SQLException, ClassNotFoundException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Statement statement = connectionSingleton.con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM KLIENT ORDER BY id_klienta");
            id.setCellValueFactory(new PropertyValueFactory<User, Integer>("id"));
            imie.setCellValueFactory(new PropertyValueFactory<User,String>("imie"));
            nazwisko.setCellValueFactory(new PropertyValueFactory<User,String>("nazwisko"));
            adres.setCellValueFactory(new PropertyValueFactory<User,String>("adres"));
            tabelka.setItems(parseUserList(rs));
            //tabelka.getColumns().addAll(id, imie, nazwisko, adres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private ObservableList<User> parseUserList(ResultSet rs) throws SQLException {
        ObservableList<User> list = FXCollections.observableArrayList();
        Integer id;
        String imie;
        String nazwisko;
        String adres;
        while(rs.next()){
            id = rs.getInt("ID_KLIENTA");
            imie = rs.getString("IMIE");
            nazwisko = rs.getString("NAZWISKO");
            adres = rs.getString("ADRES");
            User help = new User(id,imie,nazwisko,adres);
            list.add(help);
        }
        return list;
    }

    @FXML protected void toMain(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/main_page.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void toKlient(ActionEvent event) throws IOException, SQLException {
        if (tabelka.getSelectionModel().getSelectedItem()==null){
            popup.showAndWait();
        }
        else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/klient.fxml"));
            Parent parent = loader.load();
            KlientController controller = loader.getController();
            User user = (User) tabelka.getSelectionModel().getSelectedItem();
            controller.setID(user.getId());
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML protected void toNoweKonto(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/nowe_konto.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
