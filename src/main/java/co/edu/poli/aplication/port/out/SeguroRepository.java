package co.edu.poli.aplication.port.out;

import co.edu.poli.aplication.domain.model.Asegurado;
import co.edu.poli.aplication.domain.model.Seguro;
import java.util.List;
import java.util.Optional;

/** Contrato de persistencia requerido por los casos de uso de seguros. */
public interface SeguroRepository {
    /**
     * Persiste una póliza nueva y devuelve su identificador generado.
     * @param seguro póliza por guardar
     * @return póliza con ID generado
     */
    Seguro guardar(Seguro seguro);
    /**
     * Actualiza una póliza existente.
     * @param seguro póliza con sus datos actualizados
     * @return póliza actualizada o vacío si no existe
     */
    Optional<Seguro> actualizar(Seguro seguro);
    /**
     * Elimina una póliza por identificador.
     * @param id clave primaria
     * @return {@code true} si se eliminó una póliza
     */
    boolean eliminarPorId(int id);
    /**
     * Busca una póliza por identificador.
     * @param id clave primaria
     * @return póliza encontrada o vacío
     */
    Optional<Seguro> buscarPorId(int id);
    /**
     * Busca una póliza por número único.
     * @param numeroPoliza número de póliza
     * @return póliza encontrada o vacío
     */
    Optional<Seguro> buscarPorNumeroPoliza(String numeroPoliza);
    /**
     * Recupera las pólizas ordenadas por clave primaria.
     * @return resultados
     */
    List<Seguro> buscarTodos();
    /**
     * Recupera asegurados precargados para nuevas pólizas.
     * @return catálogo
     */
    List<Asegurado> buscarAsegurados();
}
