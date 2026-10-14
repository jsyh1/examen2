package co.edu.poli.aplication.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/** Información y reglas compartidas por los distintos tipos de seguro. */
public abstract class Seguro {
    private final int id;
    private final String numeroPoliza;
    private final String nombre;
    private final String descripcion;
    private final BigDecimal valorPrima;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private EstadoSeguro estado;
    private final Asegurado asegurado;

    /**
     * Construye una póliza y valida sus invariantes comunes.
     * @param id identificador, cero para una póliza nueva
     * @param numeroPoliza número único de póliza
     * @param nombre nombre comercial
     * @param descripcion descripción opcional
     * @param valorPrima importe de la prima
     * @param fechaInicio inicio de vigencia
     * @param fechaFin fin de vigencia
     * @param estado estado inicial o actual
     * @param asegurado titular de la póliza
     */
    protected Seguro(int id, String numeroPoliza, String nombre, String descripcion,
                     BigDecimal valorPrima, LocalDate fechaInicio, LocalDate fechaFin,
                     EstadoSeguro estado, Asegurado asegurado) {
        if (id < 0) {
            throw new IllegalArgumentException("El ID del seguro no puede ser negativo.");
        }
        this.id = id;
        this.numeroPoliza = obligatorio(numeroPoliza, "El número de póliza es obligatorio.");
        this.nombre = obligatorio(nombre, "El nombre del seguro es obligatorio.");
        this.descripcion = descripcion == null ? "" : descripcion.trim();
        this.valorPrima = Objects.requireNonNull(valorPrima, "El valor de la prima es obligatorio.");
        if (valorPrima.signum() < 0) {
            throw new IllegalArgumentException("La prima no puede ser negativa.");
        }
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "La fecha de inicio es obligatoria.");
        this.fechaFin = Objects.requireNonNull(fechaFin, "La fecha de fin es obligatoria.");
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
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
     * Cambia el estado respetando las transiciones del dominio.
     * @param nuevoEstado estado de destino permitido
     */
    public void cambiarEstado(EstadoSeguro nuevoEstado) {
        if (!estado.permiteTransicionA(nuevoEstado)) {
            throw new IllegalArgumentException("Transición de estado no permitida: " + estado + " a " + nuevoEstado + ".");
        }
        estado = nuevoEstado;
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
     * Devuelve el nombre comercial.
     * @return nombre
     */
    public String getNombre() { return nombre; }
    /**
     * Devuelve la descripción.
     * @return descripción
     */
    public String getDescripcion() { return descripcion; }
    /**
     * Devuelve el importe de la prima.
     * @return prima
     */
    public BigDecimal getValorPrima() { return valorPrima; }
    /**
     * Devuelve el inicio de vigencia.
     * @return fecha inicial
     */
    public LocalDate getFechaInicio() { return fechaInicio; }
    /**
     * Devuelve el fin de vigencia.
     * @return fecha final
     */
    public LocalDate getFechaFin() { return fechaFin; }
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
