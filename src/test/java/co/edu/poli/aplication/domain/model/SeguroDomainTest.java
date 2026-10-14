package co.edu.poli.aplication.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SeguroDomainTest {
    private final Asegurado asegurado = new Asegurado(1, "CC", "100", "Ana Pérez", "3001234567", "ana@example.com");

    @Test
    void rechazaPrimaNegativa() {
        assertThrows(IllegalArgumentException.class, () -> crearVida(new BigDecimal("-1"),
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1)));
    }

    @Test
    void rechazaFechasInvertidas() {
        assertThrows(IllegalArgumentException.class, () -> crearVida(BigDecimal.ONE,
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1)));
    }

    @Test
    void rechazaCorreoDeAseguradoInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> new Asegurado(2, "CC", "200", "Luis", "3000000000", "correo-invalido"));
    }

    @Test
    void transicionesDeEstadoRespetanReglas() {
        Seguro vida = crearVida(new BigDecimal("100.00"), LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1));
        vida.cambiarEstado(EstadoSeguro.VENCIDO);
        assertEquals(EstadoSeguro.VENCIDO, vida.getEstado());
        assertThrows(IllegalArgumentException.class, () -> vida.cambiarEstado(EstadoSeguro.ACTIVO));
    }

    @Test
    void conservaElSubtipoYValidaImportesEspecificos() {
        Seguro vehiculo = new SeguroVehiculo(0, "P-1", "Auto", "", new BigDecimal("20"),
                LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), EstadoSeguro.PENDIENTE,
                asegurado, "ABC123", "Marca", "Modelo", new BigDecimal("5000"));
        assertEquals("VEHICULO", vehiculo.getTipoSeguro());
        assertThrows(IllegalArgumentException.class, () -> new SeguroVida(0, "P-2", "Vida", "",
                BigDecimal.ZERO, LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1),
                EstadoSeguro.PENDIENTE, asegurado, "Beneficiario", BigDecimal.ZERO));
    }

    private SeguroVida crearVida(BigDecimal prima, LocalDate inicio, LocalDate fin) {
        return new SeguroVida(0, "P-1", "Vida", "", prima, inicio, fin, EstadoSeguro.ACTIVO,
                asegurado, "Beneficiario", new BigDecimal("1000"));
    }
}
