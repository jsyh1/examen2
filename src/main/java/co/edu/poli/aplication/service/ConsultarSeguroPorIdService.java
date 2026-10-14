package co.edu.poli.aplication.service;

import co.edu.poli.aplication.domain.model.Seguro;
import co.edu.poli.aplication.port.in.ConsultarSeguroPorIdUseCase;
import co.edu.poli.aplication.port.out.SeguroRepository;
import java.util.Objects;
import java.util.Optional;

/** Implementa la consulta de póliza por ID. */
public final class ConsultarSeguroPorIdService implements ConsultarSeguroPorIdUseCase {
    private final SeguroRepository seguroRepository;

    /**
     * Recibe el puerto de persistencia.
     * @param seguroRepository contrato de consulta
     */
    public ConsultarSeguroPorIdService(SeguroRepository seguroRepository) {
        this.seguroRepository = Objects.requireNonNull(seguroRepository);
    }

    @Override
    public Optional<Seguro> consultarPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser positivo.");
        }
        return seguroRepository.buscarPorId(id);
    }
}
