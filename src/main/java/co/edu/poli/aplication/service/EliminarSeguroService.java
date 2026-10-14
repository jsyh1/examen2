package co.edu.poli.aplication.service;

import co.edu.poli.aplication.port.in.EliminarSeguroUseCase;
import co.edu.poli.aplication.port.out.SeguroRepository;
import java.util.Objects;

/** Implementa la eliminación de una póliza por ID. */
public final class EliminarSeguroService implements EliminarSeguroUseCase {
    private final SeguroRepository seguroRepository;

    /**
     * Recibe el puerto de persistencia.
     * @param seguroRepository contrato de eliminación
     */
    public EliminarSeguroService(SeguroRepository seguroRepository) {
        this.seguroRepository = Objects.requireNonNull(seguroRepository);
    }

    @Override
    public boolean eliminarSeguro(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser positivo.");
        }
        return seguroRepository.eliminarPorId(id);
    }
}
