package co.edu.poli.aplication.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/** Información y reglas compartidas por los distintos tipos de seguro. */
public abstract class Seguro {
    private final int id;
    private final String numeroPoliza;
    private final LocalDate fechaExpedicion;
    private final BigDecimal valor;
    private final EstadoSeguro estado;
    private final Asegurado asegurado;

    /**
     * Construye una póliza y valida sus invariantes comunes.
     * @param id identificador, cero para una póliza nueva
     * @param numeroPoliza número único de póliza
     * @param fechaExpedicion fecha de expedición
     * @param valor valor monetario del seguro
     * @param estado estado de la póliza
     * @param asegurado titular de la póliza
     */
    protected Seguro(int id, String numeroPoliza, LocalDate fechaExpedicion,
                     BigDecimal valor, EstadoSeguro estado, Asegurado asegurado) {
        if (id < 0) {
            throw new IllegalArgumentException("El ID del seguro no puede ser negativo.");
        }
        this.id = id;
        this.numeroPoliza = obligatorio(numeroPoliza, "El número de póliza es obligatorio.");
        this.fechaExpedicion = Objects.requireNonNull(fechaExpedicion, "La fecha de expedición es obligatoria.");
        this.valor = Objects.requireNonNull(valor, "El valor del seguro es obligatorio.");
        if (valor.signum() < 0) {
            throw new IllegalArgumentException("El valor del seguro no puede ser negativo.");
        }
        this.estado = Objects.requireNonNull(estado, "El estado del seguro es obligatorio.");
        this.asegurado = Objects.requireNonNull(asegurado, "Debe seleccionar un asegurado.");
    }

    private static String obligatorio(String valor, String mensaje) {
        Objects.requireNonNull(valor, mensaje);
        String normalizado = valor.trim();
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
        return normalizado;
    }

    /**
     * Devuelve el tipo concreto para presentación y persistencia.
     * @return tipo de seguro
     */
    public abstract String getTipoSeguro();

    /**
     * Devuelve el identificador, cero antes de persistir.
     * @return ID
     */
    public int getId() { return id; }
    /**
     * Devuelve el número único de póliza.
     * @return número de póliza
     */
    public String getNumeroPoliza() { return numeroPoliza; }
    /**
     * Devuelve la fecha en que se expidió la póliza.
     * @return fecha de expedición
     */
    public LocalDate getFechaExpedicion() { return fechaExpedicion; }
    /**
     * Devuelve el valor monetario del seguro.
     * @return valor
     */
    public BigDecimal getValor() { return valor; }
    /**
     * Devuelve el estado actual.
     * @return estado
     */
    public EstadoSeguro getEstado() { return estado; }
    /**
     * Devuelve el titular de la póliza.
     * @return asegurado
     */
    public Asegurado getAsegurado() { return asegurado; }
}
