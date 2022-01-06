package com.example.sql_project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSingleton {
    private static ConnectionSingleton connection;
    public static Connection con;

    private ConnectionSingleton() throws ClassNotFoundException, SQLException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        con = DriverManager.getConnection("jdbc:oracle:thin:@//admlab2.cs.put.poznan.pl:1521/dblab02_students.cs.put.poznan.pl","login","password");
    }

    public static ConnectionSingleton getConnection() throws SQLException, ClassNotFoundException {
        if(connection == null){
            connection = new ConnectionSingleton();
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        con.close();
    }
}
