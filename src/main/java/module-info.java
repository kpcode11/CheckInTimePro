module com.example.studentmanagerpro {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome6;
    requires org.kordamp.ikonli.core;
    requires org.controlsfx.controls;
    requires java.sql;
    requires com.zaxxer.hikari;
    requires org.postgresql.jdbc;
    requires io.github.cdimascio.dotenv.java;
    requires jbcrypt;
    requires org.apache.pdfbox;

    opens com.example.studentmanager to javafx.fxml;
    exports com.example.studentmanager;
}
