package co.edu.poli.aplication.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/** Póliza vehicular con identificación del vehículo y valor asegurado. */
public final class SeguroVehiculo extends Seguro {
    private final String placa;
    private final String marca;
    private final String modelo;
    private final BigDecimal valorAsegurado;

    /**
     * Construye un seguro vehicular con sus datos específicos.
     * @param id identificador, cero antes de persistir
     * @param numeroPoliza número único de póliza
     * @param nombre nombre comercial
     * @param descripcion descripción opcional
     * @param valorPrima importe de la prima
     * @param fechaInicio inicio de vigencia
     * @param fechaFin fin de vigencia
     * @param estado estado de la póliza
     * @param asegurado titular de la póliza
     * @param placa placa del vehículo
     * @param marca marca del vehículo
     * @param modelo modelo del vehículo
     * @param valorAsegurado importe asegurado
     */
    public SeguroVehiculo(int id, String numeroPoliza, String nombre, String descripcion,
                          BigDecimal valorPrima, LocalDate fechaInicio, LocalDate fechaFin,
                          EstadoSeguro estado, Asegurado asegurado, String placa,
                          String marca, String modelo, BigDecimal valorAsegurado) {
        super(id, numeroPoliza, nombre, descripcion, valorPrima, fechaInicio, fechaFin, estado, asegurado);
        this.placa = obligatorio(placa, "La placa es obligatoria.");
        this.marca = obligatorio(marca, "La marca es obligatoria.");
        this.modelo = obligatorio(modelo, "El modelo es obligatorio.");
        this.valorAsegurado = Objects.requireNonNull(valorAsegurado, "El valor asegurado es obligatorio.");
        if (valorAsegurado.signum() <= 0) {
            throw new IllegalArgumentException("El valor asegurado debe ser mayor que cero.");
        }
    }

    private static String obligatorio(String valor, String mensaje) {
        Objects.requireNonNull(valor, mensaje);
        String normalizado = valor.trim();
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
        return normalizado;
    }

    @Override
    /**
     * Devuelve el discriminador persistido.
     * @return tipo de seguro
     */
    public String getTipoSeguro() { return "VEHICULO"; }
    /**
     * Devuelve la placa del vehículo.
     * @return placa
     */
    public String getPlaca() { return placa; }
    /**
     * Devuelve la marca del vehículo.
     * @return marca
     */
    public String getMarca() { return marca; }
    /**
     * Devuelve el modelo del vehículo.
     * @return modelo
     */
    public String getModelo() { return modelo; }
    /**
     * Devuelve el valor monetario asegurado.
     * @return importe asegurado
     */
    public BigDecimal getValorAsegurado() { return valorAsegurado; }
}
