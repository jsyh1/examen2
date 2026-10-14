package co.edu.poli.aplication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import co.edu.poli.aplication.domain.model.Asegurado;
import co.edu.poli.aplication.domain.model.EstadoSeguro;
import co.edu.poli.aplication.domain.model.Seguro;
import co.edu.poli.aplication.domain.model.SeguroVida;
import co.edu.poli.aplication.port.out.SeguroRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SeguroServiceTest {
    @Test
    void crearRechazaNumeroDePolizaDuplicado() {
        FakeSeguroRepository repository = new FakeSeguroRepository();
        repository.seguros.add(seguro(1, "POL-1"));
        CrearSeguroService service = new CrearSeguroService(repository);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.crearSeguro(seguro(0, "POL-1")));

        assertTrue(exception.getMessage().contains("Ya existe"));
        assertEquals(1, repository.seguros.size());
    }

    @Test
    void actualizarRechazaPolizaDuplicadaEnOtroSeguro() {
        FakeSeguroRepository repository = new FakeSeguroRepository();
        repository.seguros.add(seguro(1, "POL-1"));
        repository.seguros.add(seguro(2, "POL-2"));

        assertThrows(IllegalArgumentException.class,
                () -> new ActualizarSeguroService(repository).actualizarSeguro(seguro(2, "POL-1")));
    }

    @Test
    void actualizarPermiteModificarEstadoComoParteDelCrud() {
        FakeSeguroRepository repository = new FakeSeguroRepository();
        repository.seguros.add(seguro(1, "POL-1", EstadoSeguro.PENDIENTE));

        var actualizado = new ActualizarSeguroService(repository)
                .actualizarSeguro(seguro(1, "POL-1", EstadoSeguro.CANCELADO));

        assertEquals(EstadoSeguro.CANCELADO, actualizado.orElseThrow().getEstado());
    }

    @Test
    void consultaValidaIdAntesDeConsultarElPuerto() {
        ConsultarSeguroPorIdService service = new ConsultarSeguroPorIdService(new FakeSeguroRepository());
        assertThrows(IllegalArgumentException.class, () -> service.consultarPorId(0));
    }

    private SeguroVida seguro(int id, String numero) {
        return seguro(id, numero, EstadoSeguro.PENDIENTE);
    }

    private SeguroVida seguro(int id, String numero, EstadoSeguro estado) {
        Asegurado asegurado = new Asegurado(1, "ID-100", "Ana Pérez", "ana@example.com");
        return new SeguroVida(id, numero, LocalDate.of(2026, 8, 21), BigDecimal.TEN,
            estado, asegurado, "Beneficiario", "Hija");
    }

    private static final class FakeSeguroRepository implements SeguroRepository {
        private final List<Seguro> seguros = new ArrayList<>();

        @Override
        public Seguro guardar(Seguro seguro) {
            seguros.add(seguro);
            return seguro;
        }

        @Override
        public Optional<Seguro> actualizar(Seguro seguro) {
            for (int index = 0; index < seguros.size(); index++) {
                if (seguros.get(index).getId() == seguro.getId()) {
                    seguros.set(index, seguro);
                    return Optional.of(seguro);
                }
            }
            return Optional.empty();
        }

        @Override
        public boolean eliminarPorId(int id) {
            return seguros.removeIf(seguro -> seguro.getId() == id);
        }

        @Override
        public Optional<Seguro> buscarPorId(int id) {
            return seguros.stream().filter(seguro -> seguro.getId() == id).findFirst();
        }

        @Override
        public Optional<Seguro> buscarPorNumeroPoliza(String numeroPoliza) {
            return seguros.stream().filter(seguro -> seguro.getNumeroPoliza().equals(numeroPoliza)).findFirst();
        }

        @Override
        public List<Seguro> buscarTodos() {
            return List.copyOf(seguros);
        }

        @Override
        public List<Asegurado> buscarAsegurados() {
            return List.of();
        }
    }
}
