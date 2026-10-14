package co.edu.poli.aplication.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/** Póliza de vida con beneficiario y parentesco. */
public final class SeguroVida extends Seguro {
    private final String nombreBeneficiario;
    private final String parentesco;

    /**
     * Construye un seguro de vida con sus datos específicos.
     * @param id identificador, cero antes de persistir
     * @param numeroPoliza número único de póliza
     * @param fechaExpedicion fecha de expedición
     * @param valor valor monetario del seguro
     * @param estado estado de la póliza
     * @param asegurado titular de la póliza
     * @param nombreBeneficiario nombre de la persona beneficiaria
     * @param parentesco relación del beneficiario con el asegurado
     */
    public SeguroVida(int id, String numeroPoliza, LocalDate fechaExpedicion, BigDecimal valor,
                      EstadoSeguro estado, Asegurado asegurado, String nombreBeneficiario, String parentesco) {
        super(id, numeroPoliza, fechaExpedicion, valor, estado, asegurado);
        this.nombreBeneficiario = obligatorio(nombreBeneficiario, "El nombre del beneficiario es obligatorio.");
        this.parentesco = obligatorio(parentesco, "El parentesco del beneficiario es obligatorio.");
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
    public String getTipoSeguro() { return "VIDA"; }
    /**
     * Devuelve el nombre del beneficiario.
     * @return beneficiario
     */
    public String getNombreBeneficiario() { return nombreBeneficiario; }
    /**
     * Devuelve el parentesco del beneficiario.
     * @return parentesco
     */
    public String getParentesco() { return parentesco; }
}
