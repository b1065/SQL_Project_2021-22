package com.example.sql_project;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
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

public class UzupenienieController extends AbstractController implements Initializable {
    @FXML Button buttonWstecz;
    @FXML Button buttonUzupelnij;
    @FXML Button buttonAnuluj;
    @FXML TableView tabelka;
    @FXML TableColumn data;
    @FXML TableColumn nazwa;
    @FXML TableColumn ilosc;
    @FXML TableColumn stan;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();
    Alert popup1 = new Alert(Alert.AlertType.ERROR, "Nie wybrano uzupełnienia.", ButtonType.OK);
    Alert popup2 = new Alert(Alert.AlertType.ERROR, "Coś poszło nie tak! Status uzupełnienia nie zostało zmienione.", ButtonType.OK);
    Alert popup3 = new Alert(Alert.AlertType.WARNING,"Wybrane uzupelnienie zostalo juz wykonane lub anulowane.",ButtonType.OK);
    Alert popup4 = new Alert(Alert.AlertType.INFORMATION, "Pomyślnie anulowano wybrane uzupełnienie.", ButtonType.OK);

    public UzupenienieController() throws SQLException, ClassNotFoundException {
    }

    public static class Supply {
        private final ObjectProperty<Timestamp> data;
        private final SimpleStringProperty nazwa;
        private final SimpleIntegerProperty ilosc;
        private final SimpleStringProperty stan;

        private Supply(Timestamp pdata, String pnazwa, Integer pilosc, String pstan) {
            this.data = new SimpleObjectProperty<>(pdata);
            this.nazwa = new SimpleStringProperty(pnazwa);
            this.ilosc = new SimpleIntegerProperty(pilosc);
            this.stan = new SimpleStringProperty(pstan);
        }

        public Integer getIlosc() {
            return ilosc.get();
        }

        public void setIlosc(Integer pilosc) {
            this.ilosc.set(pilosc);
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

        public String getNazwa() {
            return nazwa.get();
        }

        public void setNazwa(String pnazwa) {
            this.nazwa.set(pnazwa);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Statement statement = connectionSingleton.con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM UZUPELNIENIE ORDER BY data");
            data.setCellValueFactory(new PropertyValueFactory<UzupenienieController.Supply, Timestamp>("data"));
            nazwa.setCellValueFactory(new PropertyValueFactory<UzupenienieController.Supply, String>("nazwa"));
            ilosc.setCellValueFactory(new PropertyValueFactory<UzupenienieController.Supply, Integer>("ilosc"));
            stan.setCellValueFactory(new PropertyValueFactory<UzupenienieController.Supply, String>("stan"));
            tabelka.setItems(parseOrderList(rs));
            //tabelka.getColumns().addAll(id, imie, nazwisko, adres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<UzupenienieController.Supply> parseOrderList(ResultSet rs) throws SQLException {
        ObservableList<UzupenienieController.Supply> list = FXCollections.observableArrayList();
        Integer ilosc;
        String nazwa;
        Timestamp data;
        String stan;
        while (rs.next()) {
            nazwa = rs.getString("NAZWA");
            ilosc = rs.getInt("ILOSC");
            data = rs.getTimestamp("DATA");
            stan = rs.getString("STAN");
            UzupenienieController.Supply help = new UzupenienieController.Supply(data,nazwa,ilosc,stan);
            list.add(help);
        }
        return list;
    }

    @FXML
    protected void toMain(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/kierownik_magazyn.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void toSelf(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/example/sql_project/uzupelnienia.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void toSkrzynka(ActionEvent event) throws IOException{
        if (tabelka.getSelectionModel().getSelectedItem()==null){
            popup1.showAndWait();
        }
        else {
            UzupenienieController.Supply supply = (UzupenienieController.Supply) tabelka.getSelectionModel().getSelectedItem();
            if(supply.getStan().equals("ANULOWANE") || supply.getStan().equals("ODNOTOWANE")){
                popup3.showAndWait();
            }
            else {
                try {
                    CallableStatement statement = ConnectionSingleton.con.prepareCall("{call Procedury.ZmienStatusUzupelnienia(?,?,?)}");
                    statement.setString(1, supply.getNazwa());
                    statement.setTimestamp(2,supply.getData());
                    statement.setString(3,"ODNOTOWANE");
                    statement.execute();
                    statement.close();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/nowa_skrzynka.fxml"));
                    Parent parent = loader.load();
                    NowaSkrzynkaController controller = loader.getController();
                    controller.setSkladnik(supply.getNazwa(), supply.getIlosc());
                    Scene scene = new Scene(parent);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                }catch (SQLException e){
                    popup2.showAndWait();
                }
            }
        }
    }

    @FXML protected void anulujUzupelnienie(ActionEvent event) throws IOException {
        if (tabelka.getSelectionModel().getSelectedItem()==null){
            popup1.showAndWait();
        }
        else {
            UzupenienieController.Supply supply = (UzupenienieController.Supply) tabelka.getSelectionModel().getSelectedItem();
            if (supply.getStan().equals("ANULOWANE") || supply.getStan().equals("ODNOTOWANE")) {
                popup3.showAndWait();
            } else {
                try {
                CallableStatement statement = ConnectionSingleton.con.prepareCall("{call Procedury.ZmienStatusUzupelnienia(?,?,?)}");
                statement.setString(1,supply.getNazwa());
                statement.setTimestamp(2,supply.getData());
                statement.setString(3,"ANULOWANE");
                statement.execute();
                statement.close();
                popup4.showAndWait();
                toSelf(event);
                } catch (SQLException e) {
                    popup2.showAndWait();
                }
            }
        }
    }
}
