package com.example.sql_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class NowySkladnikController extends AbstractController{
    @FXML TextField nazwa;
    @FXML TextField ilosc;
    @FXML Button buttonDodaj;
    @FXML Button buttonPowrot;
    Integer ID;
    Exception zero;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();
    Alert popup = new Alert(Alert.AlertType.CONFIRMATION,"Skladnik został dodany poprawnie.", ButtonType.OK);
    Alert popup1 = new Alert(Alert.AlertType.WARNING,"Proszę podać nazwę oraz ilość składnika.", ButtonType.OK);
    Alert popup2 = new Alert(Alert.AlertType.ERROR,"Coś poszło nie tak! Składnik nie został dodany.", ButtonType.OK);
    Alert popup3 = new Alert(Alert.AlertType.WARNING, "Proszę podać poprawną ilość składnika.", ButtonType.OK);
    Alert popup4 = new Alert(Alert.AlertType.CONFIRMATION, "Podany składnik już istnieje, dodano do niego wprowadzoną ilość.", ButtonType.OK);

    public NowySkladnikController() throws SQLException, ClassNotFoundException {
    }

    public void setID(Integer x) {
        ID = x;
    }

    @FXML protected void toSkladnik(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/lista_skladnikow.fxml"));
        Parent parent = loader.load();
        ListaSkladnikowController controller = loader.getController();
        controller.setID(ID);
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void toSelf(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/nowy_skladnik.fxml"));
        Parent parent = loader.load();
        NowySkladnikController controller = loader.getController();
        controller.setID(ID);
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML protected void dodawanieSkladnika(ActionEvent event) throws IOException {
        if(nazwa.getText()==null||nazwa.getText().trim().isEmpty()||ilosc.getText()==null ||ilosc.getText().trim().isEmpty()){
            popup1.showAndWait();
        }
        else {
            try {
                if(Integer.parseInt(ilosc.getText())<=0){
                    throw zero;
                }
                PreparedStatement search = connectionSingleton.con.prepareStatement("SELECT nazwa FROM SKLADNIK WHERE nazwa = ? AND id_przepisu = ?");
                search.setString(1,nazwa.getText());
                search.setInt(2,ID);
                ResultSet rs = search.executeQuery();
                if(rs.next()==true)
                {
                    CallableStatement statement_help = connectionSingleton.con.prepareCall("{call Procedury.ZmienIloscSkladnika(?,?)}");
                    statement_help.setString(1,nazwa.getText());
                    statement_help.setInt(2, Integer.parseInt(ilosc.getText()));
                    statement_help.execute();
                    statement_help.close();
                    popup4.showAndWait();
                    toSelf(event);
                }
                else {
                    CallableStatement statement = connectionSingleton.con.prepareCall("{call Procedury.DodajSkladnik(?,?,?)}");
                    statement.setString(1, nazwa.getText());
                    statement.setInt(2, ID);
                    statement.setInt(3, Integer.parseInt(ilosc.getText()));
                    statement.execute();
                    statement.close();
                    popup.showAndWait();
                    toSelf(event);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                popup2.showAndWait();
            } catch (NumberFormatException e) {
                popup3.showAndWait();
            } catch (Exception e){
                popup3.showAndWait();
            }
        }
    }
}
