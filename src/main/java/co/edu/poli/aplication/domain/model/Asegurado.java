package co.edu.poli.aplication.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/** Persona titular de uno o varios seguros. */
public final class Asegurado {
    private static final Pattern CORREO_VALIDO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final int id;
    private final String tipoDocumento;
    private final String numeroDocumento;
    private final String nombre;
    private final String telefono;
    private final String correo;

    /**
     * Construye un asegurado existente en el catálogo de la base de datos.
     *
     * @param id identificador positivo
     * @param tipoDocumento tipo de documento
     * @param numeroDocumento número de documento
     * @param nombre nombre completo
     * @param telefono teléfono de contacto
     * @param correo correo electrónico
     */
    public Asegurado(int id, String tipoDocumento, String numeroDocumento, String nombre,
                     String telefono, String correo) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del asegurado debe ser positivo.");
        }
        this.id = id;
        this.tipoDocumento = obligatorio(tipoDocumento, "El tipo de documento es obligatorio.");
        this.numeroDocumento = obligatorio(numeroDocumento, "El número de documento es obligatorio.");
        this.nombre = obligatorio(nombre, "El nombre del asegurado es obligatorio.");
        this.telefono = obligatorio(telefono, "El teléfono del asegurado es obligatorio.");
        this.correo = obligatorio(correo, "El correo del asegurado es obligatorio.");
        if (!CORREO_VALIDO.matcher(this.correo).matches()) {
            throw new IllegalArgumentException("El correo del asegurado no tiene un formato válido.");
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

    /**
     * Devuelve la clave primaria del asegurado.
     * @return identificador
     */
    public int getId() { return id; }
    /**
     * Devuelve el tipo de documento.
     * @return tipo documental
     */
    public String getTipoDocumento() { return tipoDocumento; }
    /**
     * Devuelve el número de documento.
     * @return número documental
     */
    public String getNumeroDocumento() { return numeroDocumento; }
    /**
     * Devuelve el nombre completo.
     * @return nombre
     */
    public String getNombre() { return nombre; }
    /**
     * Devuelve el teléfono de contacto.
     * @return teléfono
     */
    public String getTelefono() { return telefono; }
    /**
     * Devuelve el correo electrónico validado.
     * @return correo
     */
    public String getCorreo() { return correo; }

    @Override
    public String toString() {
        return nombre + " - " + tipoDocumento + " " + numeroDocumento;
    }
}
