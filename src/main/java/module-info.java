/** Módulo de escritorio para la gestión de seguros. */
module co.edu.poli {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires io.github.cdimascio.dotenv.java;

    opens co.edu.poli.vista to javafx.fxml;
    opens co.edu.poli.infrastructure.ui to javafx.fxml;
    opens co.edu.poli.aplication.domain.model to javafx.base;

    exports co.edu.poli.vista;
    exports co.edu.poli.aplication.domain.model;
    exports co.edu.poli.aplication.port.in;
    exports co.edu.poli.aplication.port.out;
    exports co.edu.poli.aplication.service;
}
