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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/** Pruebas CRUD reales; se ejecutan con -Dmysql.integration=true y el esquema instalado. */
@EnabledIfSystemProperty(named = "mysql.integration", matches = "true")
class MysqlSeguroRepositoryIT {
    @Test
    void creaConsultaActualizaYEliminaAmbosSubtipos() {
        MysqlSeguroRepository repository = new MysqlSeguroRepository();
        Asegurado asegurado = repository.buscarAsegurados().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("El catálogo MySQL requiere al menos un asegurado."));
        String numero = "IT-" + UUID.randomUUID();
        int id = 0;
        try {
            SeguroVida vida = new SeguroVida(0, numero, "Vida integración", "Prueba", BigDecimal.TEN,
                    LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), EstadoSeguro.PENDIENTE,
                    asegurado, "Beneficiario de prueba", BigDecimal.valueOf(50000));
            Seguro creado = repository.guardar(vida);
            id = creado.getId();
            assertTrue(id > 0);
            assertInstanceOf(SeguroVida.class, repository.buscarPorId(id).orElseThrow());

            SeguroVehiculo actualizado = new SeguroVehiculo(id, numero, "Vehículo integración", "Actualizada",
                    BigDecimal.valueOf(25), LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1),
                    EstadoSeguro.PENDIENTE, asegurado, "TST123", "Marca", "Modelo", BigDecimal.valueOf(9000));
            assertTrue(repository.actualizar(actualizado).isPresent());
            Seguro recuperado = repository.buscarPorNumeroPoliza(numero).orElseThrow();
            assertInstanceOf(SeguroVehiculo.class, recuperado);
            assertEquals("TST123", ((SeguroVehiculo) recuperado).getPlaca());
            assertNotNull(recuperado.getAsegurado());
            assertTrue(repository.buscarTodos().stream().anyMatch(seguro -> seguro.getId() == recuperado.getId()));
        } finally {
            if (id > 0) {
                repository.eliminarPorId(id);
            }
        }
    }
}
