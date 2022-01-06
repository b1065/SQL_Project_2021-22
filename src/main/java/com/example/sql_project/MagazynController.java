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
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.ResourceBundle;

public class MagazynController extends AbstractController implements Initializable {
    @FXML Button buttonWstecz;
    @FXML Button buttonUzupelnienia;
    @FXML Button buttonDodaj;
    @FXML Button buttonModyfikuj;
    @FXML Button buttonUsun;
    @FXML TableView tabelka;
    @FXML TableColumn data;
    @FXML TableColumn nazwa;
    @FXML TableColumn prod;
    @FXML TableColumn cena;
    @FXML TableColumn ilosc;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();
    Alert popup = new Alert(Alert.AlertType.ERROR, "Nie wybrano skrzynki.", ButtonType.OK);
    Alert popup1 = new Alert(Alert.AlertType.INFORMATION,"Skrzynka została poprawnie usunięta.", ButtonType.OK);
    Alert popup2 = new Alert(Alert.AlertType.ERROR,"Coś poszło nie tak! Skrzynka nie została usunięta.", ButtonType.OK);


    public MagazynController() throws SQLException, ClassNotFoundException {
    }

    public static class Box {

        private final ObjectProperty<Date> data;
        private final SimpleStringProperty nazwa;
        private final SimpleStringProperty prod;
        private final SimpleDoubleProperty cena;
        private final SimpleIntegerProperty ilosc;


        private Box(Date pdata, String pnazwa, String pprod, Double pcena, Integer pilosc) {
            this.data = new SimpleObjectProperty<>(pdata);
            this.nazwa = new SimpleStringProperty(pnazwa);
            this.prod = new SimpleStringProperty(pprod);
            this.cena = new SimpleDoubleProperty(pcena);
            this.ilosc = new SimpleIntegerProperty(pilosc);

        }

        public Date getData() {return data.get();}
        public void setData(Date pdata) {
            data.set(pdata);
        }

        public String getNazwa() {
            return nazwa.get();
        }
        public void setNazwa(String pnazwa) {
            nazwa.set(pnazwa);
        }

        public String getProd() {
            return prod.get();
        }
        public void setProd(String pprod) {
            prod.set(pprod);
        }

        public Double getCena() {
            return cena.get();
        }
        public void setCena(Double pcena) {
            cena.set(pcena);
        }

        public Integer getIlosc() {
            return ilosc.get();
        }
        public void setIlosc(Integer pilosc) {
            ilosc.set(pilosc);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Statement statement = connectionSingleton.con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM SKRZYNKA");
            data.setCellValueFactory(new PropertyValueFactory<MagazynController.Box,Date>("data"));
            nazwa.setCellValueFactory(new PropertyValueFactory<MagazynController.Box,String>("nazwa"));
            prod.setCellValueFactory(new PropertyValueFactory<MagazynController.Box,String>("prod"));
            cena.setCellValueFactory(new PropertyValueFactory<MagazynController.Box,Integer>("cena"));
            ilosc.setCellValueFactory(new PropertyValueFactory<MagazynController.Box,Integer>("ilosc"));
            tabelka.setItems(parseUserList(rs));
            //tabelka.getColumns().addAll(id, imie, nazwisko, adres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private ObservableList<MagazynController.Box> parseUserList(ResultSet rs) throws SQLException {
        ObservableList<MagazynController.Box> list = FXCollections.observableArrayList();
        Date data;
        String nazwa;
        String prod;
        Double cena;
        Integer ilosc;
        while(rs.next()){
            data = rs.getDate("DATA");
            nazwa = rs.getString("NAZWA_PRODUKTU");
            prod = rs.getString("PRODUCENT");
            cena = rs.getDouble("CENA");
            ilosc = rs.getInt("ILOSC");
            MagazynController.Box help = new MagazynController.Box(data,nazwa,prod,cena,ilosc);
            list.add(help);
        }
        return list;
    }

    @FXML protected void toUsun(ActionEvent event) throws IOException{
        if (tabelka.getSelectionModel().getSelectedItem()==null){
            popup.showAndWait();
        }
        else {
            MagazynController.Box box = (MagazynController.Box) tabelka.getSelectionModel().getSelectedItem();
            try {
                CallableStatement statement = connectionSingleton.con.prepareCall("{call Procedury.UsunSkrzynke(?,?,?)}");
                statement.setString(1,box.getProd());
                statement.setString(2, box.getNazwa());
                statement.setDate(3, (java.sql.Date) box.getData());
                statement.execute();
                popup1.showAndWait();
                toSelf(event);
            }catch (SQLException e){
                popup2.showAndWait();
            }
        }
    }

    @FXML protected void toKierownik(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/kierownik.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void toDodaj(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/nowa_skrzynka.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/modyfikuj_skrzynke.fxml"));
            Parent parent = loader.load();
            ModyfikujSkrzynkeController controller = loader.getController();
            MagazynController.Box box = (MagazynController.Box) tabelka.getSelectionModel().getSelectedItem();
            controller.setSkrzynka(box.getProd(),box.getNazwa(), (java.sql.Date) box.getData());
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML protected void toSelf(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/kierownik_magazyn.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void toUzupelnienia(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/uzupelnienia.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
