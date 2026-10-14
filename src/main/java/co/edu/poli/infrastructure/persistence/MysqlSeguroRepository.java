package co.edu.poli.infrastructure.persistence;

import co.edu.poli.aplication.domain.model.Asegurado;
import co.edu.poli.aplication.domain.model.EstadoSeguro;
import co.edu.poli.aplication.domain.model.Seguro;
import co.edu.poli.aplication.domain.model.SeguroVehiculo;
import co.edu.poli.aplication.domain.model.SeguroVida;
import co.edu.poli.aplication.port.out.SeguroRepository;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Adaptador JDBC que persiste pólizas y lee el catálogo MySQL de asegurados. */
public final class MysqlSeguroRepository implements SeguroRepository {
        private static final String SELECT_SEGUROS = "SELECT s.id, s.numero_poliza, s.fecha_expedicion, s.valor, "
            + "s.estado, s.tipo, a.id AS asegurado_id, a.identificacion, "
            + "a.nombre AS asegurado_nombre, a.correo_electronico, "
            + "sv.nombre_beneficiario, sv.parentesco, svh.marca "
            + "FROM seguros s JOIN asegurados a ON a.id = s.asegurado_id "
            + "LEFT JOIN seguros_vida sv ON sv.seguro_id = s.id "
            + "LEFT JOIN seguros_vehiculo svh ON svh.seguro_id = s.id ";

    private final ConexionDB conexionDB;

    /** Crea el adaptador con la configuración JDBC predeterminada. */
    public MysqlSeguroRepository() {
        this(new ConexionDB());
    }

    MysqlSeguroRepository(ConexionDB conexionDB) {
        this.conexionDB = conexionDB;
    }

    @Override
    public Seguro guardar(Seguro seguro) {
        return enTransaccion(connection -> {
                String sql = "INSERT INTO seguros (numero_poliza, fecha_expedicion, valor, estado, tipo, asegurado_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            int id;
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                asignarCamposComunes(statement, seguro);
                statement.setInt(6, seguro.getAsegurado().getId());
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("MySQL no devolvió el ID de la póliza creada.");
                    }
                    id = keys.getInt(1);
                }
            }
            insertarDetalle(connection, id, seguro);
            return conId(seguro, id);
        });
    }

    @Override
    public Optional<Seguro> actualizar(Seguro seguro) {
        return enTransaccion(connection -> {
                String sql = "UPDATE seguros SET numero_poliza = ?, fecha_expedicion = ?, valor = ?, "
                    + "estado = ?, tipo = ?, asegurado_id = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                asignarCamposComunes(statement, seguro);
                statement.setInt(6, seguro.getAsegurado().getId());
                statement.setInt(7, seguro.getId());
                if (statement.executeUpdate() == 0) {
                    return Optional.empty();
                }
            }
            try (PreparedStatement vida = connection.prepareStatement("DELETE FROM seguros_vida WHERE seguro_id = ?");
                 PreparedStatement vehiculo = connection.prepareStatement("DELETE FROM seguros_vehiculo WHERE seguro_id = ?")) {
                vida.setInt(1, seguro.getId());
                vida.executeUpdate();
                vehiculo.setInt(1, seguro.getId());
                vehiculo.executeUpdate();
            }
            insertarDetalle(connection, seguro.getId(), seguro);
            return Optional.of(conId(seguro, seguro.getId()));
        });
    }

    @Override
    public boolean eliminarPorId(int id) {
        return enTransaccion(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM seguros WHERE id = ?")) {
                statement.setInt(1, id);
                return statement.executeUpdate() > 0;
            }
        });
    }

    @Override
    public Optional<Seguro> buscarPorId(int id) {
        return buscarUno(SELECT_SEGUROS + "WHERE s.id = ?", id);
    }

    @Override
    public Optional<Seguro> buscarPorNumeroPoliza(String numeroPoliza) {
        try (Connection connection = conexionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_SEGUROS + "WHERE s.numero_poliza = ?")) {
            statement.setString(1, numeroPoliza);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapearSeguro(result)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw errorPersistencia("consultar la póliza", exception);
        }
    }

    @Override
    public List<Seguro> buscarTodos() {
        List<Seguro> seguros = new ArrayList<>();
        try (Connection connection = conexionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_SEGUROS + "ORDER BY s.id");
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                seguros.add(mapearSeguro(result));
            }
            return List.copyOf(seguros);
        } catch (SQLException exception) {
            throw errorPersistencia("listar las pólizas", exception);
        }
    }

    @Override
    public List<Asegurado> buscarAsegurados() {
        String sql = "SELECT id, identificacion, nombre, correo_electronico "
                + "FROM asegurados ORDER BY nombre";
        List<Asegurado> asegurados = new ArrayList<>();
        try (Connection connection = conexionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                asegurados.add(new Asegurado(result.getInt("id"), result.getString("identificacion"),
                    result.getString("nombre"), result.getString("correo_electronico")));
            }
            return List.copyOf(asegurados);
        } catch (SQLException exception) {
            throw errorPersistencia("listar los asegurados", exception);
        }
    }

    private Optional<Seguro> buscarUno(String sql, int id) {
        try (Connection connection = conexionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapearSeguro(result)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw errorPersistencia("consultar la póliza", exception);
        }
    }

    private void asignarCamposComunes(PreparedStatement statement, Seguro seguro) throws SQLException {
        statement.setString(1, seguro.getNumeroPoliza());
        statement.setDate(2, Date.valueOf(seguro.getFechaExpedicion()));
        statement.setBigDecimal(3, seguro.getValor());
        statement.setString(4, seguro.getEstado().name());
        statement.setString(5, seguro.getTipoSeguro());
    }

    private void insertarDetalle(Connection connection, int id, Seguro seguro) throws SQLException {
        if (seguro instanceof SeguroVida vida) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO seguros_vida (seguro_id, nombre_beneficiario, parentesco) VALUES (?, ?, ?)")) {
                statement.setInt(1, id);
                statement.setString(2, vida.getNombreBeneficiario());
                statement.setString(3, vida.getParentesco());
                statement.executeUpdate();
            }
        } else if (seguro instanceof SeguroVehiculo vehiculo) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO seguros_vehiculo (seguro_id, marca) VALUES (?, ?)")) {
                statement.setInt(1, id);
                statement.setString(2, vehiculo.getMarca());
                statement.executeUpdate();
            }
        } else {
            throw new IllegalArgumentException("Tipo de seguro no soportado.");
        }
    }

    private Seguro mapearSeguro(ResultSet result) throws SQLException {
        Asegurado asegurado = new Asegurado(result.getInt("asegurado_id"),
            result.getString("identificacion"), result.getString("asegurado_nombre"),
            result.getString("correo_electronico"));
        int id = result.getInt("id");
        String numero = result.getString("numero_poliza");
        var fechaExpedicion = result.getDate("fecha_expedicion").toLocalDate();
        var valor = result.getBigDecimal("valor");
        EstadoSeguro estado = EstadoSeguro.valueOf(result.getString("estado"));
        return switch (result.getString("tipo")) {
            case "VIDA" -> new SeguroVida(id, numero, fechaExpedicion, valor, estado, asegurado,
                result.getString("nombre_beneficiario"), result.getString("parentesco"));
            case "VEHICULO" -> new SeguroVehiculo(id, numero, fechaExpedicion, valor, estado,
                asegurado, result.getString("marca"));
            default -> throw new SQLException("Tipo de seguro desconocido en la base de datos.");
        };
    }

    private Seguro conId(Seguro seguro, int id) {
        if (seguro instanceof SeguroVida vida) {
            return new SeguroVida(id, vida.getNumeroPoliza(), vida.getFechaExpedicion(), vida.getValor(),
                vida.getEstado(), vida.getAsegurado(), vida.getNombreBeneficiario(), vida.getParentesco());
        }
        SeguroVehiculo vehiculo = (SeguroVehiculo) seguro;
        return new SeguroVehiculo(id, vehiculo.getNumeroPoliza(), vehiculo.getFechaExpedicion(), vehiculo.getValor(),
            vehiculo.getEstado(), vehiculo.getAsegurado(), vehiculo.getMarca());
    }

    private <T> T enTransaccion(Operacion<T> operacion) {
        try (Connection connection = conexionDB.getConnection()) {
            connection.setAutoCommit(false);
            try {
                T resultado = operacion.ejecutar(connection);
                connection.commit();
                return resultado;
            } catch (SQLException exception) {
                rollback(connection, exception);
                throw errorPersistencia("guardar cambios de la póliza", exception);
            } catch (RuntimeException exception) {
                rollback(connection, exception);
                throw exception;
            }
        } catch (SQLException exception) {
            throw errorPersistencia("iniciar la operación de persistencia", exception);
        }
    }

    private void rollback(Connection connection, Exception causa) {
        try {
            connection.rollback();
        } catch (SQLException rollbackError) {
            causa.addSuppressed(rollbackError);
        }
    }

    private IllegalStateException errorPersistencia(String operacion, SQLException causa) {
        if (causa.getSQLState() != null && causa.getSQLState().startsWith("23")) {
            return new IllegalStateException("La póliza o relación indicada infringe una restricción de datos.", causa);
        }
        return new IllegalStateException("No se pudo " + operacion + ". Verifica la conexión y la base de datos.", causa);
    }

    @FunctionalInterface
    private interface Operacion<T> {
        T ejecutar(Connection connection) throws SQLException;
    }
}
