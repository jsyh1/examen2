package co.edu.poli.aplication.domain.model;

import java.time.LocalDate;
import java.util.Objects;

/** Póliza vehicular con su marca. */
public final class SeguroVehiculo extends Seguro {
    private final String marca;

    /**
     * Construye un seguro vehicular con su marca.
     * @param id identificador, cero antes de persistir
     * @param numeroPoliza número único de póliza
     * @param fechaExpedicion fecha de expedición
     * @param valor valor monetario del seguro
     * @param estado estado de la póliza
     * @param asegurado titular de la póliza
     * @param marca marca del vehículo
     */
    public SeguroVehiculo(int id, String numeroPoliza, LocalDate fechaExpedicion,
                          java.math.BigDecimal valor, EstadoSeguro estado,
                          Asegurado asegurado, String marca) {
        super(id, numeroPoliza, fechaExpedicion, valor, estado, asegurado);
        this.marca = obligatorio(marca, "La marca del vehículo es obligatoria.");
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
    * Devuelve la marca del vehículo.
    * @return marca
     */
    /**
     * Devuelve la marca del vehículo.
     * @return marca
     */
    public String getMarca() { return marca; }
}
