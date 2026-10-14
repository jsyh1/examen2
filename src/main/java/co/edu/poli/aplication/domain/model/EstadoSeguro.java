package co.edu.poli.aplication.domain.model;

/** Estados disponibles para una póliza. */
public enum EstadoSeguro {
    /** Póliza vigente y habilitada. */
    ACTIVO,
    /** Póliza cuya vigencia terminó. */
    VENCIDO,
    /** Póliza cancelada de forma definitiva. */
    CANCELADO,
    /** Póliza pendiente de activación. */
    PENDIENTE
}
