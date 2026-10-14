package co.edu.poli.aplication.service;

import co.edu.poli.aplication.domain.model.Seguro;
import co.edu.poli.aplication.port.in.ConsultarSeguroPorPolizaUseCase;
import co.edu.poli.aplication.port.out.SeguroRepository;
import java.util.Objects;
import java.util.Optional;

/** Implementa la consulta de póliza por su número. */
public final class ConsultarSeguroPorPolizaService implements ConsultarSeguroPorPolizaUseCase {
    private final SeguroRepository seguroRepository;

    /**
     * Recibe el puerto de persistencia.
     * @param seguroRepository contrato de consulta
     */
    public ConsultarSeguroPorPolizaService(SeguroRepository seguroRepository) {
        this.seguroRepository = Objects.requireNonNull(seguroRepository);
    }

    @Override
    public Optional<Seguro> consultarPorNumeroPoliza(String numeroPoliza) {
        if (numeroPoliza == null || numeroPoliza.isBlank()) {
            throw new IllegalArgumentException("El número de póliza es obligatorio.");
        }
        return seguroRepository.buscarPorNumeroPoliza(numeroPoliza.trim());
    }
}
