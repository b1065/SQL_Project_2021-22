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
import java.sql.*;
import java.util.ResourceBundle;

public class ListaSkladnikowController extends AbstractController {
    @FXML Button buttonWstecz;
    @FXML Button buttonDodaj;
    @FXML Button buttonUsun;
    @FXML TableView tabelka;
    @FXML TableColumn nazwa;
    @FXML TableColumn ilosc;
    @FXML Label tytul;
    Integer ID;
    Alert popup = new Alert(Alert.AlertType.ERROR, "Nie wybrano skladnika.", ButtonType.OK);
    Alert popup1 = new Alert(Alert.AlertType.INFORMATION,"Składnik został poprawnie usunięty.", ButtonType.OK);
    Alert popup2 = new Alert(Alert.AlertType.ERROR,"Coś poszło nie tak! Składnik nie został usunięty.", ButtonType.OK);
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();

    public ListaSkladnikowController() throws SQLException, ClassNotFoundException {
    }


    public static class Ingredient {
        private final SimpleIntegerProperty ilosc;
        private final SimpleStringProperty nazwa;

        private Ingredient(String pnazwa, Integer pilosc) {
            this.nazwa = new SimpleStringProperty(pnazwa);
            this.ilosc = new SimpleIntegerProperty(pilosc);
        }

        public Integer getIlosc() { return ilosc.get(); }
        public void setIlosc(Integer pilosc) { ilosc.set(pilosc); }

        public String getNazwa() {
            return nazwa.get();
        }
        public void setNazwa(String pnazwa) {
            nazwa.set(pnazwa);
        }
    }

    public void setID(Integer x) {
        try {
            ID = x;
            String nazwa_dania = null;
            ResultSet rs1, rs2;
            PreparedStatement statement1 = connectionSingleton.con.prepareStatement("SELECT nazwa FROM DANIE WHERE id_dania = ?");
            statement1.setInt(1,ID);
            rs1 = statement1.executeQuery();
            while(rs1.next()) {
                nazwa_dania = rs1.getString("NAZWA");
            }
            tytul.setText("Danie: "+nazwa_dania);
            PreparedStatement statement2 = connectionSingleton.con.prepareStatement("SELECT nazwa, ilosc FROM SKLADNIK WHERE ID_PRZEPISU = ?");
            statement2.setInt(1,ID);
            rs2 = statement2.executeQuery();
            nazwa.setCellValueFactory(new PropertyValueFactory<ListaSkladnikowController.Ingredient,String>("nazwa"));
            ilosc.setCellValueFactory(new PropertyValueFactory<ListaSkladnikowController.Ingredient,Integer>("ilosc"));
            tabelka.setItems(parseUserList(rs2));
            //tabelka.getColumns().addAll(id, imie, nazwisko, adres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private ObservableList<ListaSkladnikowController.Ingredient> parseUserList(ResultSet rs) throws SQLException {
        ObservableList<ListaSkladnikowController.Ingredient> list = FXCollections.observableArrayList();
        Integer ilosc;
        String nazwa;
        while(rs.next()){
            nazwa = rs.getString("NAZWA");
            ilosc = rs.getInt("ILOSC");
            ListaSkladnikowController.Ingredient help = new ListaSkladnikowController.Ingredient(nazwa,ilosc);
            list.add(help);
        }
        return list;
    }

    @FXML protected void toUsun(ActionEvent event) throws IOException{
        if (tabelka.getSelectionModel().getSelectedItem()==null){
            popup.showAndWait();
        }
        else {
            ListaSkladnikowController.Ingredient ingredient = (ListaSkladnikowController.Ingredient) tabelka.getSelectionModel().getSelectedItem();
            try {
                CallableStatement statement = connectionSingleton.con.prepareCall("{call Procedury.UsunSkladnik(?)}");
                statement.setString(1,ingredient.getNazwa());
                statement.execute();
                popup1.showAndWait();
                toSelf(event);
            }catch (SQLException e){
                popup2.showAndWait();
            }
        }
    }

    @FXML protected void toSelf(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/lista_skladnikow.fxml"));
        Parent parent = loader.load();
        ListaSkladnikowController controller = loader.getController();
        controller.setID(ID);
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void toDodaj(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/nowy_skladnik.fxml"));
        Parent parent = loader.load();
        NowySkladnikController controller = loader.getController();
        controller.setID(ID);
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void toMenu(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/kierownik_menu.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
