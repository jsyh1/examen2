package co.edu.poli.aplication.port.in;

import co.edu.poli.aplication.domain.model.Asegurado;
import java.util.List;

/** Caso de uso de consulta del catálogo de asegurados disponibles. */
public interface ListarAseguradosUseCase {
    /**
     * Lista los asegurados disponibles en el catálogo.
     * @return resultados
     */
    List<Asegurado> listarAsegurados();
}
