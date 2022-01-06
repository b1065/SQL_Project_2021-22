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

public class MenuController extends AbstractController implements Initializable {
    @FXML Button buttonWstecz;
    @FXML Button buttonDodaj;
    @FXML Button buttonModyfikuj;
    @FXML Button buttonSkladniki;
    @FXML Button buttonUsun;
    @FXML TableView tabelka;
    @FXML TableColumn id;
    @FXML TableColumn nazwa;
    @FXML TableColumn czas;
    @FXML TableColumn cena;
    Alert popup = new Alert(Alert.AlertType.ERROR, "Nie wybrano dania.", ButtonType.OK);
    Alert popup1 = new Alert(Alert.AlertType.INFORMATION,"Danie zostało poprawnie usunięte.", ButtonType.OK);
    Alert popup2 = new Alert(Alert.AlertType.ERROR,"Coś poszło nie tak! Danie nie zostało usunięte.", ButtonType.OK);
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();

    public MenuController() throws SQLException, ClassNotFoundException {
    }


    public static class Dish {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty nazwa;
        private final SimpleStringProperty czas;
        private final SimpleDoubleProperty cena;

        private Dish(Integer pid, String pnazwa, String pczas, Double pcena) {
            this.id = new SimpleIntegerProperty(pid);
            this.nazwa = new SimpleStringProperty(pnazwa);
            this.czas = new SimpleStringProperty(pczas);
            this.cena = new SimpleDoubleProperty(pcena);
        }

        public Integer getId() { return id.get(); }
        public void setid(Integer pid) { id.set(pid); }

        public String getNazwa() {
            return nazwa.get();
        }
        public void setNazwa(String pnazwa) {
            nazwa.set(pnazwa);
        }

        public String getCzas() {return czas.get();}
        public void setCzas(String pczas) {
            czas.set(pczas);
        }

        public Double getCena() {return cena.get();}
        public void setCena(Double pcena) {cena.set(pcena);}

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Statement statement = connectionSingleton.con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM DANIE ORDER BY id_dania");
            id.setCellValueFactory(new PropertyValueFactory<MenuController.Dish,Integer>("id"));
            nazwa.setCellValueFactory(new PropertyValueFactory<MenuController.Dish,String>("nazwa"));
            czas.setCellValueFactory(new PropertyValueFactory<MenuController.Dish,String>("czas"));
            cena.setCellValueFactory(new PropertyValueFactory<MenuController.Dish,Double>("cena"));
            tabelka.setItems(parseUserList(rs));
            //tabelka.getColumns().addAll(id, imie, nazwisko, adres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private ObservableList<MenuController.Dish> parseUserList(ResultSet rs) throws SQLException {
        ObservableList<MenuController.Dish> list = FXCollections.observableArrayList();
        Integer id;
        String nazwa;
        String czas;
        Double cena;
        while(rs.next()){
            id = rs.getInt("ID_DANIA");
            nazwa = rs.getString("NAZWA");
            czas = rs.getString("CZAS_PRZYGOTOWANIA");
            czas = czas.substring(2);
            cena = rs.getDouble("CENA");
            MenuController.Dish help = new MenuController.Dish(id,nazwa,czas,cena);
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
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/nowe_danie.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/modyfikuj_danie.fxml"));
            Parent parent = loader.load();
            ModyfikujDanieController controller = loader.getController();
            MenuController.Dish dish = (MenuController.Dish) tabelka.getSelectionModel().getSelectedItem();
            controller.setID(dish.getId());
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML protected void toSkladniki(ActionEvent event) throws IOException, SQLException {
        if (tabelka.getSelectionModel().getSelectedItem()==null){
            popup.showAndWait();
        }
        else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/lista_skladnikow.fxml"));
            Parent parent = loader.load();
            ListaSkladnikowController controller = loader.getController();
            MenuController.Dish dish = (MenuController.Dish) tabelka.getSelectionModel().getSelectedItem();
            controller.setID(dish.getId());
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML protected void toSelf(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/kierownik_menu.fxml"));
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
            MenuController.Dish dish = (MenuController.Dish) tabelka.getSelectionModel().getSelectedItem();
            try {
                CallableStatement statement = connectionSingleton.con.prepareCall("{call Procedury.UsunDanie(?)}");
                statement.setInt(1,dish.getId());
                statement.execute();
                popup1.showAndWait();
                toSelf(event);
            }catch (SQLException e){
                popup2.showAndWait();
            }
        }
    }
}
