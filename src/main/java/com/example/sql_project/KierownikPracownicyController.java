package com.example.sql_project;

import javafx.beans.property.SimpleDoubleProperty;
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
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class KierownikPracownicyController extends AbstractController implements Initializable {
    @FXML Button buttonWstecz;
    @FXML Button buttonDodaj;
    @FXML Button buttonModyfikuj;
    @FXML Button buttonUsun;
    @FXML TableView tabelka;
    @FXML TableColumn imie;
    @FXML TableColumn nazwisko;
    @FXML TableColumn stawka;
    @FXML TableColumn stan;
    Alert popup = new Alert(Alert.AlertType.ERROR, "Nie wybrano pracownika.", ButtonType.OK);
    Alert popup1 = new Alert(Alert.AlertType.INFORMATION,"Pracownik został poprawnie usunięty.", ButtonType.OK);
    Alert popup2 = new Alert(Alert.AlertType.ERROR,"Coś poszło nie tak! Pracownik nie został usunięty.", ButtonType.OK);
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();

    public KierownikPracownicyController() throws SQLException, ClassNotFoundException {
    }


    public static class Worker {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty imie;
        private final SimpleStringProperty nazwisko;
        private final SimpleDoubleProperty stawka;
        private final SimpleStringProperty stan;

        private Worker(Integer pid, String pimie, String pnazwisko, Double pstawka, String pstan) {
            this.id = new SimpleIntegerProperty(pid);
            this.stawka = new SimpleDoubleProperty(pstawka);
            this.imie = new SimpleStringProperty(pimie);
            this.nazwisko = new SimpleStringProperty(pnazwisko);
            this.stan = new SimpleStringProperty(pstan);
        }

        public Integer getId() { return id.get(); }
        public void setid(Integer pid) { id.set(pid); }

        public Double getStawka() { return stawka.get(); }
        public void setStawka(Double pstawka) {
            stawka.set(pstawka);
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

        public String getStan() {return stan.get();}
        public void setStan(String pstan) {
            stan.set(pstan);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Statement statement = connectionSingleton.con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM PPRACOWNICY ORDER BY id_prac");
            imie.setCellValueFactory(new PropertyValueFactory<KierownikPracownicyController.Worker,String>("imie"));
            nazwisko.setCellValueFactory(new PropertyValueFactory<KierownikPracownicyController.Worker,String>("nazwisko"));
            stawka.setCellValueFactory(new PropertyValueFactory<KierownikPracownicyController.Worker, Double>("stawka"));
            stan.setCellValueFactory(new PropertyValueFactory<KierownikPracownicyController.Worker,String>("stan"));
            tabelka.setItems(parseUserList(rs));
            //tabelka.getColumns().addAll(id, imie, nazwisko, adres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private ObservableList<KierownikPracownicyController.Worker> parseUserList(ResultSet rs) throws SQLException {
        ObservableList<KierownikPracownicyController.Worker> list = FXCollections.observableArrayList();
        Integer id;
        Double stawka;
        String imie;
        String nazwisko;
        String stan;
        while(rs.next()){
            id = rs.getInt("ID_PRAC");
            imie = rs.getString("IMIE");
            nazwisko = rs.getString("NAZWISKO");
            stawka = rs.getDouble("STAWKA_GODZINOWA");
            stan = rs.getString("STAN");
            KierownikPracownicyController.Worker help = new KierownikPracownicyController.Worker(id,imie,nazwisko,stawka,stan);
            list.add(help);
        }
        return list;
    }

    @FXML protected void toKierownik(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/kierownik.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void toDodaj(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/nowy_pracownik.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void toModyfikuj(ActionEvent event) throws IOException, SQLException {
        if (tabelka.getSelectionModel().getSelectedItem()==null){
            popup.showAndWait();
        }
        else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/modyfikuj_pracownika.fxml"));
            Parent parent = loader.load();
            ModyfikujPracownikaController controller = loader.getController();
            Worker worker = (Worker) tabelka.getSelectionModel().getSelectedItem();
            controller.setID(worker.getId());
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML protected void toSelf(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/kierownik_pracownicy.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void toUsun(ActionEvent event) throws IOException{
        if (tabelka.getSelectionModel().getSelectedItem()==null){
            popup.showAndWait();
        }
        else {
            Worker worker = (Worker) tabelka.getSelectionModel().getSelectedItem();
            try {
                CallableStatement statement = connectionSingleton.con.prepareCall("{call Procedury.UsunPracownika(?)}");
                statement.setInt(1,worker.getId());
                statement.execute();
                popup1.showAndWait();
                toSelf(event);
            }catch (SQLException e){
                e.printStackTrace();
                popup2.showAndWait();
            }
        }
    }
}
