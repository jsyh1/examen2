package co.edu.poli.infrastructure.persistence;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Crea conexiones JDBC a partir de variables de entorno o un archivo .env local. */
public final class ConexionDB {
    private final Dotenv dotenv;

    /** Inicializa la lectura opcional de configuración local. */
    public ConexionDB() {
        dotenv = Dotenv.configure().ignoreIfMissing().load();
    }

    /**
     * Abre una conexión nueva a MySQL.
     *
     * @return conexión que debe cerrarse con try-with-resources
     * @throws SQLException si falta configuración o no se puede conectar
     */
    public Connection getConnection() throws SQLException {
        String host = valor("DB_HOST", "localhost");
        String puerto = valor("DB_PORT", "3307");
        String baseDatos = valor("DB_NAME", "seguros");
        String usuario = valor("DB_USER", "root");
        String contrasena = valor("DB_PASSWORD", "jsyhXD7$");
        if (usuario == null || usuario.isBlank()) {
            throw new SQLException("La variable DB_USER no está configurada.");
        }
        String url = "jdbc:mysql://" + host + ":" + puerto + "/" + baseDatos
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    private String valor(String nombre, String predeterminado) {
        String valor = System.getenv(nombre);
        if (valor == null || valor.isBlank()) {
            valor = dotenv.get(nombre);
        }
        return valor == null || valor.isBlank() ? predeterminado : valor;
    }
}
