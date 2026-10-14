package co.edu.poli.aplication.port.in;

/** Caso de uso para eliminar una póliza. */
public interface EliminarSeguroUseCase {
    /**
     * Elimina una póliza por su ID.
     * @param id identificador positivo
     * @return {@code true} si se eliminó una fila
     */
    boolean eliminarSeguro(int id);
}
