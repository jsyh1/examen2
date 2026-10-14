package co.edu.poli.aplication.port.in;

import co.edu.poli.aplication.domain.model.Seguro;
import java.util.Optional;

/** Caso de uso para consultar una póliza por su número. */
public interface ConsultarSeguroPorPolizaUseCase {
    /**
     * Busca un seguro por el número único de póliza.
     * @param numeroPoliza número de póliza
     * @return póliza encontrada o vacío
     */
    Optional<Seguro> consultarPorNumeroPoliza(String numeroPoliza);
}
