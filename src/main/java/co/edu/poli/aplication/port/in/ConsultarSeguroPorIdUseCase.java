package co.edu.poli.aplication.port.in;

import co.edu.poli.aplication.domain.model.Seguro;
import java.util.Optional;

/** Caso de uso para consultar una póliza por su identificador. */
public interface ConsultarSeguroPorIdUseCase {
    /**
     * Busca un seguro por su clave primaria.
     * @param id identificador positivo
     * @return póliza encontrada o vacío
     */
    Optional<Seguro> consultarPorId(int id);
}
