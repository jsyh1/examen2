package co.edu.poli.aplication.port.in;

import co.edu.poli.aplication.domain.model.Seguro;
import java.util.Optional;

/** Caso de uso para actualizar una póliza existente. */
public interface ActualizarSeguroUseCase {
    /**
     * Actualiza una póliza y aplica sus reglas de negocio.
     * @param seguro datos actualizados con ID existente
     * @return póliza actualizada o vacío si no existe
     */
    Optional<Seguro> actualizarSeguro(Seguro seguro);
}
