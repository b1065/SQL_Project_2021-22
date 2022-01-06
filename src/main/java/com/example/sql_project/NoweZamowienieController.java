package com.example.sql_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class NoweZamowienieController extends AbstractController {
    @FXML Button buttonConfirm;
    @FXML Button buttonBack;
    @FXML ComboBox menu;
    @FXML TextField czas;
    @FXML TextField cena;
    Integer ID;
    ConnectionSingleton connectionSingleton = ConnectionSingleton.getConnection();
    Alert popup1 = new Alert(Alert.AlertType.INFORMATION);
    Alert popup2 = new Alert(Alert.AlertType.WARNING,"Niestety, w tej chwili wszyscy nasi pracownicy są zajęci. Spróbuj ponownie później.", ButtonType.OK);
    Alert popup3 = new Alert(Alert.AlertType.ERROR, "Niestety, nie jesteśmy w stanie obecnie wykonać tego zamówienia. Spróbuj ponownie później.",ButtonType.OK);
    Alert popupspook = new Alert(Alert.AlertType.NONE, "How The Fuck", ButtonType.OK);

    public NoweZamowienieController() throws SQLException, ClassNotFoundException {
    }

    public void setID(Integer x){
        ID = x;
        try {
            ObservableList dania = FXCollections.observableArrayList();
            ResultSet rs = ConnectionSingleton.con.createStatement().executeQuery("SELECT nazwa FROM DANIE");
            while (rs.next()) {
                dania.add(rs.getString("NAZWA"));
            }
            menu.setItems(null);
            menu.setItems(dania);
        } catch (SQLException ex) {
            System.err.println("Error" + ex);
        }
    }

    @FXML protected void wyborDania(ActionEvent event){
        try {
            String help = (String) menu.getSelectionModel().getSelectedItem();
            PreparedStatement statement = ConnectionSingleton.con.prepareStatement("SELECT czas_przygotowania, cena FROM DANIE WHERE nazwa = ?");
            statement.setString(1,help);
            ResultSet rs = statement.executeQuery();
            rs.next();
            czas.setText(rs.getString("CZAS_PRZYGOTOWANIA").substring(2,rs.getString("CZAS_PRZYGOTOWANIA").length()-2));
            cena.setText(rs.getString("CENA"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML protected void startZamowienia(ActionEvent event){
        String nazwa_help = null;
        Integer ilosc_help = null;
        Integer help, help2, smieciek;
        Boolean help3;
        try{
            PreparedStatement statement = ConnectionSingleton.con.prepareStatement("SELECT id_dania FROM DANIE WHERE nazwa = ?");
            statement.setString(1, (String) menu.getSelectionModel().getSelectedItem());
            ResultSet rs = statement.executeQuery();
            rs.next();
            help = rs.getInt("ID_DANIA");
            statement.close();
            rs.close();
            CallableStatement statement2 = ConnectionSingleton.con.prepareCall("{? = call Funkcje.ZnajdzPracownika");
            statement2.registerOutParameter(1, Types.INTEGER);
            statement2.execute();
            help2 = statement2.getInt(1);
            statement.close();
            if(help2 == 0){
                popup2.showAndWait();
            }
            else{
                help3=true;
                PreparedStatement statement1 = ConnectionSingleton.con.prepareStatement("SELECT nazwa, ilosc FROM SKLADNIK WHERE id_przepisu = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                statement1.setInt(1,help);
                ResultSet rs1 = statement1.executeQuery();
                while(rs1.next()){
                    nazwa_help = rs1.getString("NAZWA");
                    ilosc_help = rs1.getInt("ILOSC");
                    smieciek = sprawdzanieSkladnikow(nazwa_help,ilosc_help);
                    if(smieciek == 1){
                        help3=false;
                        break;
                    }
                    if(smieciek == 2){
                        popupspook.showAndWait();
                    }
                }
                if(help3 == false){
                    popup3.showAndWait();
                }
                else{
                    rs1.beforeFirst();
                    while (rs1.next()){
                        nazwa_help = rs1.getString("NAZWA");
                        ilosc_help = rs1.getInt("ILOSC");
                        zarzadzanieSkladnikami(nazwa_help,ilosc_help);
                    }
                    CallableStatement statement3 = ConnectionSingleton.con.prepareCall("{call Procedury.ZmienStatusPracownika(?,?)}");
                    statement3.setInt(1,help2);
                    statement3.setString(2,"ZAJETY");
                    statement3.execute();
                    statement3.close();
                    String czas_help = "00 "+czas.getText();
                    CallableStatement statement6 = ConnectionSingleton.con.prepareCall("{? = call Funkcje.DodajZamowienie(?,?,?)}");
                    statement6.registerOutParameter(1,Types.INTEGER);
                    statement6.setInt(2,ID);
                    statement6.setDouble(3,Double.parseDouble(cena.getText()));
                    statement6.setString(4,czas_help);
                    statement6.execute();
                    Integer nr_zamowienia = statement6.getInt(1);
                    statement6.close();
                    CallableStatement statement6_2 = ConnectionSingleton.con.prepareCall("{call Procedury.DodajZadanie(?,?,?)}");
                    statement6_2.setInt(1,nr_zamowienia);
                    statement6_2.setInt(2,help);
                    statement6_2.setInt(3,help2);
                    statement6_2.execute();
                    statement6_2.close();
                    popup1.setContentText("Twoje zamówienie jest obecnie przygotowywane.\nNumer twojego zamówienia: "+nr_zamowienia+"\nPrzewidywany czas realizacji: "+czas.getText()+"\n(od momentu zamknięcia tego okna).");
                    popup1.showAndWait();
                    String[] parts = czas.getText().split(":");
                    int time = Integer.parseInt(parts[0])*3600+Integer.parseInt(parts[1])*60+Integer.parseInt(parts[2]);
                    TimeUnit.SECONDS.sleep(time);
                    CallableStatement statement7 = ConnectionSingleton.con.prepareCall("{call Procedury.ZmienStatusPracownika(?,?)}");
                    statement7.setInt(1,help2);
                    statement7.setString(2,"WOLNY");
                    statement7.execute();
                    statement7.close();
                    CallableStatement statement8 = ConnectionSingleton.con.prepareCall("{call Procedury.ZmienStatusZadania(?,?)}");
                    statement8.setInt(1,nr_zamowienia);
                    statement8.setString(2,"ZREALIZOWANE");
                    statement8.execute();
                    statement8.close();
                    CallableStatement statement9 = ConnectionSingleton.con.prepareCall("{call Procedury.ZmienStatusZamowienia(?,?)}");
                    statement9.setInt(1,nr_zamowienia);
                    statement9.setString(2,"ZREALIZOWANE");
                    statement9.execute();
                    statement9.close();
                    popup1.close();
                    popup1.setContentText("Zamowienie zostalo zrealizowane. Smacznego!");
                    popup1.showAndWait();
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
            System.out.println("Problem z czasem");
        }
    }

    @FXML protected Integer sprawdzanieSkladnikow(String nazwa, Integer ilosc) throws SQLException{
        PreparedStatement statement = ConnectionSingleton.con.prepareStatement("SELECT producent,data,cena,ilosc FROM SKRZYNKA WHERE nazwa_produktu = ? FETCH FIRST 1 ROW ONLY");
        statement.setString(1,nazwa);
        ResultSet rs = statement.executeQuery();
        String prod=null;
        Date data=null;
        Double cena=null;
        Integer ilosc_skl=null, pomoc, odp;
        while(rs.next()){
            prod = rs.getString("PRODUCENT");
            data = rs.getDate("DATA");
            cena = rs.getDouble("CENA");
            ilosc_skl = rs.getInt("ILOSC");
        }
        if(ilosc_skl==null){
            return 1;
        }
        pomoc = ilosc_skl - ilosc;
        if(pomoc >= 0){
            return 0;
        }
        else{
            odp = sprawdzanieSkladnikow(nazwa,pomoc);
            switch (odp){
                case 1:
                    return 1;
                case 0:
                    return 0;
            }
        }
        return 2;
    }

    @FXML protected void zarzadzanieSkladnikami(String nazwa, Integer ilosc) throws SQLException{
        PreparedStatement statement = ConnectionSingleton.con.prepareStatement("SELECT producent,data,cena,ilosc FROM SKRZYNKA WHERE nazwa_produktu = ? FETCH FIRST 1 ROW ONLY");
        statement.setString(1,nazwa);
        ResultSet rs = statement.executeQuery();
        String prod=null;
        Date data=null;
        Double cena=null;
        Integer ilosc_skl=null, pomoc;
        while(rs.next()){
            prod = rs.getString("PRODUCENT");
            data = rs.getDate("DATA");
            cena = rs.getDouble("CENA");
            ilosc_skl = rs.getInt("ILOSC");
        }
        pomoc = ilosc_skl - ilosc;
        if(pomoc > 0){
            CallableStatement statement1 = ConnectionSingleton.con.prepareCall("{call Procedury.ModyfikujSkrzynke(?,?,?,?,?)}");
            statement1.setString(1,prod);
            statement1.setString(2,nazwa);
            statement1.setDate(3,data);
            statement1.setInt(4,pomoc);
            statement1.setDouble(5,cena);
            statement1.execute();
            statement1.close();
        }
        else{
            CallableStatement statement2 = ConnectionSingleton.con.prepareCall("{call Procedury.UsunSkrzynke(?,?,?)}");
            statement2.setString(1,prod);
            statement2.setString(2,nazwa);
            statement2.setDate(3,data);
            statement2.execute();
            statement2.close();
            if(pomoc != 0) {
                zarzadzanieSkladnikami(nazwa, -pomoc);
            }
        }
    }

    @FXML protected void toKlient(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sql_project/klient.fxml"));
        Parent parent = loader.load();
        KlientController controller = loader.getController();
        controller.setID(ID);
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
