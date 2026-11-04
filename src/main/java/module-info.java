module com.example.proyectohibernateg3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.logging;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;


    opens com.example.proyectohibernateg3 to javafx.fxml;
    opens com.example.proyectohibernateg3.model to org.hibernate.orm.core;
    exports com.example.proyectohibernateg3;
}
