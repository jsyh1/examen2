package co.edu.poli.aplication.port.in;

import co.edu.poli.aplication.domain.model.Seguro;
import java.util.List;

/** Caso de uso para listar las pólizas persistidas. */
public interface ListarSegurosUseCase {
    /**
     * Lista las pólizas persistidas.
     * @return resultados de consulta
     */
    List<Seguro> listarSeguros();
}
