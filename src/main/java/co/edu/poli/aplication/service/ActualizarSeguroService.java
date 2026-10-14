package co.edu.poli.aplication.service;

import co.edu.poli.aplication.domain.model.Seguro;
import co.edu.poli.aplication.port.in.ActualizarSeguroUseCase;
import co.edu.poli.aplication.port.out.SeguroRepository;
import java.util.Objects;
import java.util.Optional;

/** Orquesta la actualización de una póliza existente. */
public final class ActualizarSeguroService implements ActualizarSeguroUseCase {
    private final SeguroRepository seguroRepository;

    /**
     * Recibe el puerto de persistencia de seguros.
     * @param seguroRepository contrato de persistencia
     */
    public ActualizarSeguroService(SeguroRepository seguroRepository) {
        this.seguroRepository = Objects.requireNonNull(seguroRepository);
    }

    @Override
    public Optional<Seguro> actualizarSeguro(Seguro seguro) {
        Objects.requireNonNull(seguro, "El seguro es obligatorio.");
        if (seguro.getId() <= 0) {
            throw new IllegalArgumentException("Selecciona un seguro existente para actualizar.");
        }
        Optional<Seguro> coincidencia = seguroRepository.buscarPorNumeroPoliza(seguro.getNumeroPoliza());
        if (coincidencia.isPresent() && coincidencia.get().getId() != seguro.getId()) {
            throw new IllegalArgumentException("Ya existe otra póliza con ese número.");
        }
        return seguroRepository.actualizar(seguro);
    }
}
