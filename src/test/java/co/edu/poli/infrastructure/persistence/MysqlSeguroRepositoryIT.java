package co.edu.poli.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import co.edu.poli.aplication.domain.model.Asegurado;
import co.edu.poli.aplication.domain.model.EstadoSeguro;
import co.edu.poli.aplication.domain.model.Seguro;
import co.edu.poli.aplication.domain.model.SeguroVehiculo;
import co.edu.poli.aplication.domain.model.SeguroVida;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/** Pruebas CRUD reales; se ejecutan con -Dmysql.integration=true y el esquema instalado. */
@EnabledIfSystemProperty(named = "mysql.integration", matches = "true")
class MysqlSeguroRepositoryIT {
    @Test
    void conservaSubtiposRelacionesYDetalleEnElCrud() throws SQLException {
        MysqlSeguroRepository repository = new MysqlSeguroRepository();
        Asegurado asegurado = repository.buscarAsegurados().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("El catálogo MySQL requiere al menos un asegurado."));
        String numeroVida = "IT-V-" + UUID.randomUUID();
        String numeroVehiculo = "IT-A-" + UUID.randomUUID();
        int vidaId = 0;
        int vehiculoId = 0;
        try {
            SeguroVida vida = new SeguroVida(0, numeroVida, LocalDate.of(2026, 8, 21), BigDecimal.TEN,
                    EstadoSeguro.PENDIENTE, asegurado, "Beneficiario de prueba", "Hija");
            Seguro creadoVida = repository.guardar(vida);
            vidaId = creadoVida.getId();
            assertTrue(vidaId > 0);
            SeguroVida vidaRecuperada = assertInstanceOf(SeguroVida.class,
                    repository.buscarPorId(vidaId).orElseThrow());
            assertEquals("Beneficiario de prueba", vidaRecuperada.getNombreBeneficiario());
            assertEquals("Hija", vidaRecuperada.getParentesco());
            assertEquals(asegurado.getId(), vidaRecuperada.getAsegurado().getId());

            SeguroVehiculo segundoSeguro = new SeguroVehiculo(0, numeroVehiculo, LocalDate.of(2026, 8, 22),
                    BigDecimal.valueOf(25), EstadoSeguro.ACTIVO, asegurado, "Marca segunda");
            vehiculoId = repository.guardar(segundoSeguro).getId();
            assertEquals(2, repository.buscarTodos().stream()
                    .filter(seguro -> seguro.getAsegurado().getId() == asegurado.getId()).count());

            SeguroVehiculo actualizado = new SeguroVehiculo(vidaId, numeroVida, LocalDate.of(2026, 8, 23),
                    BigDecimal.valueOf(30), EstadoSeguro.CANCELADO, asegurado, "Marca actualizada");
            assertTrue(repository.actualizar(actualizado).isPresent());
            Seguro recuperado = repository.buscarPorNumeroPoliza(numeroVida).orElseThrow();
            assertInstanceOf(SeguroVehiculo.class, recuperado);
            assertEquals("Marca actualizada", ((SeguroVehiculo) recuperado).getMarca());
            assertEquals(asegurado.getId(), recuperado.getAsegurado().getId());
            assertEquals(0, contarDetalle("seguros_vida", vidaId));
            assertEquals(1, contarDetalle("seguros_vehiculo", vidaId));

            assertTrue(repository.eliminarPorId(vidaId));
            assertTrue(repository.buscarPorId(vidaId).isEmpty());
            assertNotNull(repository.buscarPorId(vehiculoId).orElse(null));
            assertTrue(repository.buscarAsegurados().stream()
                    .anyMatch(registro -> registro.getId() == asegurado.getId()));
        } finally {
            if (vidaId > 0) {
                repository.eliminarPorId(vidaId);
            }
            if (vehiculoId > 0) {
                repository.eliminarPorId(vehiculoId);
            }
        }
    }

    private int contarDetalle(String tabla, int seguroId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tabla + " WHERE seguro_id = ?";
        try (Connection connection = new ConexionDB().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, seguroId);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getInt(1);
            }
        }
    }
}
