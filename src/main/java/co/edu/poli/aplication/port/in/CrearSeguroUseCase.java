package co.edu.poli.aplication.port.in;

import co.edu.poli.aplication.domain.model.Seguro;

/** Caso de uso para registrar una póliza. */
public interface CrearSeguroUseCase {
    /**
     * Crea la póliza y devuelve la instancia con su ID persistido.
     * @param seguro póliza válida sin identificador persistido
     * @return póliza creada
     */
    Seguro crearSeguro(Seguro seguro);
}
