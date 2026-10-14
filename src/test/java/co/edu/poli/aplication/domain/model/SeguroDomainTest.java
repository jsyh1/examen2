package co.edu.poli.aplication.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SeguroDomainTest {
    private final Asegurado asegurado = new Asegurado(1, "ID-100", "Ana Pérez", "ana@example.com");

    @Test
    void conservaCamposComunesConSusTipos() {
    Seguro vida = crearVida(new BigDecimal("1250.50"));
    assertEquals("POL-VIDA", vida.getNumeroPoliza());
    assertEquals(LocalDate.of(2026, 8, 21), vida.getFechaExpedicion());
    assertEquals(new BigDecimal("1250.50"), vida.getValor());
    assertEquals(EstadoSeguro.ACTIVO, vida.getEstado());
    assertEquals(asegurado.getId(), vida.getAsegurado().getId());
    }

    @Test
    void rechazaIdentificacionNombreOCorreoInvalidos() {
    assertThrows(IllegalArgumentException.class,
        () -> new Asegurado(2, " ", "Ana", "ana@example.com"));
    assertThrows(IllegalArgumentException.class,
        () -> new Asegurado(2, "ID-2", " ", "ana@example.com"));
    assertThrows(IllegalArgumentException.class,
        () -> new Asegurado(2, "ID-2", "Ana", "correo-invalido"));
    }

    @Test
    void rechazaNumeroFechaYValorInvalidos() {
    assertThrows(IllegalArgumentException.class, () -> new SeguroVehiculo(0, " ",
        LocalDate.of(2026, 8, 21), BigDecimal.ONE, EstadoSeguro.ACTIVO, asegurado, "Marca"));
    assertThrows(NullPointerException.class, () -> new SeguroVehiculo(0, "POL-1", null,
        BigDecimal.ONE, EstadoSeguro.ACTIVO, asegurado, "Marca"));
    assertThrows(IllegalArgumentException.class, () -> new SeguroVehiculo(0, "POL-1",
        LocalDate.of(2026, 8, 21), new BigDecimal("-0.01"), EstadoSeguro.ACTIVO, asegurado, "Marca"));
    }

    @Test
    void validaCamposEspecificosDeVida() {
    assertThrows(IllegalArgumentException.class, () -> new SeguroVida(0, "POL-1",
        LocalDate.of(2026, 8, 21), BigDecimal.TEN, EstadoSeguro.PENDIENTE,
        asegurado, " ", "Hija"));
    assertThrows(IllegalArgumentException.class, () -> new SeguroVida(0, "POL-1",
        LocalDate.of(2026, 8, 21), BigDecimal.TEN, EstadoSeguro.PENDIENTE,
        asegurado, "Ana", " "));
    }

    @Test
    void conservaHerenciaYValidaMarcaVehicular() {
    Seguro vehiculo = new SeguroVehiculo(0, "POL-AUTO", LocalDate.of(2026, 8, 21),
        new BigDecimal("5000"), EstadoSeguro.PENDIENTE, asegurado, "Marca");
        assertEquals("VEHICULO", vehiculo.getTipoSeguro());
    assertThrows(IllegalArgumentException.class, () -> new SeguroVehiculo(0, "POL-AUTO",
        LocalDate.of(2026, 8, 21), BigDecimal.ONE, EstadoSeguro.PENDIENTE, asegurado, " "));
    }

    private SeguroVida crearVida(BigDecimal valor) {
    return new SeguroVida(0, "POL-VIDA", LocalDate.of(2026, 8, 21), valor,
        EstadoSeguro.ACTIVO, asegurado, "Beneficiario", "Hija");
    }
}
