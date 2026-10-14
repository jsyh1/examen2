package co.edu.poli.aplication.service;

import co.edu.poli.aplication.domain.model.Asegurado;
import co.edu.poli.aplication.port.in.ListarAseguradosUseCase;
import co.edu.poli.aplication.port.out.SeguroRepository;
import java.util.List;
import java.util.Objects;

/** Consulta los asegurados precargados disponibles para pólizas. */
public final class ListarAseguradosService implements ListarAseguradosUseCase {
    private final SeguroRepository seguroRepository;

    /**
     * Recibe el puerto que expone el catálogo de asegurados.
     * @param seguroRepository contrato de consulta
     */
    public ListarAseguradosService(SeguroRepository seguroRepository) {
        this.seguroRepository = Objects.requireNonNull(seguroRepository);
    }

    @Override
    public List<Asegurado> listarAsegurados() {
        return seguroRepository.buscarAsegurados();
    }
}
