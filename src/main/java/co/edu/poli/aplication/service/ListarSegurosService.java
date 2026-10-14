package co.edu.poli.aplication.service;

import co.edu.poli.aplication.domain.model.Seguro;
import co.edu.poli.aplication.port.in.ListarSegurosUseCase;
import co.edu.poli.aplication.port.out.SeguroRepository;
import java.util.List;
import java.util.Objects;

/** Implementa la consulta de todas las pólizas. */
public final class ListarSegurosService implements ListarSegurosUseCase {
    private final SeguroRepository seguroRepository;

    /**
     * Recibe el puerto de persistencia.
     * @param seguroRepository contrato de consulta
     */
    public ListarSegurosService(SeguroRepository seguroRepository) {
        this.seguroRepository = Objects.requireNonNull(seguroRepository);
    }

    @Override
    public List<Seguro> listarSeguros() {
        return seguroRepository.buscarTodos();
    }
}
