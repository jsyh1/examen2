package co.edu.poli.aplication.service;

import co.edu.poli.aplication.domain.model.Seguro;
import co.edu.poli.aplication.domain.model.EstadoSeguro;
import co.edu.poli.aplication.port.in.CrearSeguroUseCase;
import co.edu.poli.aplication.port.out.SeguroRepository;
import java.util.Objects;

/** Orquesta la creación de pólizas sin depender del adaptador de persistencia. */
public final class CrearSeguroService implements CrearSeguroUseCase {
    private final SeguroRepository seguroRepository;

    /**
     * Recibe el puerto requerido para guardar las pólizas.
     * @param seguroRepository contrato de persistencia
     */
    public CrearSeguroService(SeguroRepository seguroRepository) {
        this.seguroRepository = Objects.requireNonNull(seguroRepository);
    }

    @Override
    public Seguro crearSeguro(Seguro seguro) {
        Objects.requireNonNull(seguro, "El seguro es obligatorio.");
        if (seguro.getId() != 0) {
            throw new IllegalArgumentException("Un seguro nuevo no debe tener un ID asignado.");
        }
        if (seguro.getEstado() != EstadoSeguro.PENDIENTE && seguro.getEstado() != EstadoSeguro.ACTIVO) {
            throw new IllegalArgumentException("Un seguro nuevo debe iniciar pendiente o activo.");
        }
        if (seguroRepository.buscarPorNumeroPoliza(seguro.getNumeroPoliza()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una póliza con ese número.");
        }
        return seguroRepository.guardar(seguro);
    }
}
