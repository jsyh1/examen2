package co.edu.poli.aplication.domain.model;

/** Estados posibles de una póliza y transiciones permitidas entre ellos. */
public enum EstadoSeguro {
    /** Póliza vigente y habilitada. */
    ACTIVO,
    /** Póliza cuya vigencia terminó. */
    VENCIDO,
    /** Póliza cancelada de forma definitiva. */
    CANCELADO,
    /** Póliza pendiente de activación. */
    PENDIENTE;

    /**
     * Indica si este estado puede cambiar al estado indicado.
     *
     * @param nuevoEstado estado de destino
     * @return {@code true} si la transición está permitida
     */
    public boolean permiteTransicionA(EstadoSeguro nuevoEstado) {
        if (nuevoEstado == null) {
            return false;
        }
        if (this == nuevoEstado) {
            return true;
        }
        return switch (this) {
            case PENDIENTE -> nuevoEstado == ACTIVO || nuevoEstado == CANCELADO;
            case ACTIVO -> nuevoEstado == VENCIDO || nuevoEstado == CANCELADO;
            case VENCIDO, CANCELADO -> false;
        };
    }
}
