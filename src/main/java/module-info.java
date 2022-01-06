module com.example.sql_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.sql_project to javafx.fxml;
    exports com.example.sql_project;
}