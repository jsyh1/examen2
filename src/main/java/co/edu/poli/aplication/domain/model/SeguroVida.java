package co.edu.poli.aplication.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/** Póliza de vida con beneficiario y valor de cobertura. */
public final class SeguroVida extends Seguro {
    private final String beneficiario;
    private final BigDecimal valorCobertura;

    /**
     * Construye un seguro de vida con sus datos específicos.
     * @param id identificador, cero antes de persistir
     * @param numeroPoliza número único de póliza
     * @param nombre nombre comercial
     * @param descripcion descripción opcional
     * @param valorPrima importe de la prima
     * @param fechaInicio inicio de vigencia
     * @param fechaFin fin de vigencia
     * @param estado estado de la póliza
     * @param asegurado titular de la póliza
     * @param beneficiario persona beneficiaria
     * @param valorCobertura importe cubierto
     */
    public SeguroVida(int id, String numeroPoliza, String nombre, String descripcion,
                      BigDecimal valorPrima, LocalDate fechaInicio, LocalDate fechaFin,
                      EstadoSeguro estado, Asegurado asegurado, String beneficiario,
                      BigDecimal valorCobertura) {
        super(id, numeroPoliza, nombre, descripcion, valorPrima, fechaInicio, fechaFin, estado, asegurado);
        Objects.requireNonNull(beneficiario, "El beneficiario es obligatorio.");
        this.beneficiario = beneficiario.trim();
        if (this.beneficiario.isEmpty()) {
            throw new IllegalArgumentException("El beneficiario es obligatorio.");
        }
        this.valorCobertura = Objects.requireNonNull(valorCobertura, "El valor de cobertura es obligatorio.");
        if (valorCobertura.signum() <= 0) {
            throw new IllegalArgumentException("El valor de cobertura debe ser mayor que cero.");
        }
    }

    @Override
    /**
     * Devuelve el discriminador persistido.
     * @return tipo de seguro
     */
    public String getTipoSeguro() { return "VIDA"; }
    /**
     * Devuelve el nombre del beneficiario.
     * @return beneficiario
     */
    public String getBeneficiario() { return beneficiario; }
    /**
     * Devuelve el importe cubierto.
     * @return cobertura
     */
    public BigDecimal getValorCobertura() { return valorCobertura; }
}
