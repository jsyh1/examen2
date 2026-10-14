package co.edu.poli.aplication.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/** Persona titular de uno o varios seguros. */
public final class Asegurado {
    private static final Pattern CORREO_VALIDO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final int id;
    private final String identificacion;
    private final String nombre;
    private final String correoElectronico;

    /**
     * Construye un asegurado existente en el catálogo de la base de datos.
     *
     * @param id identificador positivo
     * @param identificacion identificación única
     * @param nombre nombre completo
     * @param correoElectronico correo electrónico
     */
    public Asegurado(int id, String identificacion, String nombre, String correoElectronico) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del asegurado debe ser positivo.");
        }
        this.id = id;
        this.identificacion = obligatorio(identificacion, "La identificación del asegurado es obligatoria.");
        this.nombre = obligatorio(nombre, "El nombre del asegurado es obligatorio.");
        this.correoElectronico = obligatorio(correoElectronico, "El correo electrónico es obligatorio.");
        if (!CORREO_VALIDO.matcher(this.correoElectronico).matches()) {
            throw new IllegalArgumentException("El correo electrónico no tiene un formato válido.");
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
    public String getIdentificacion() { return identificacion; }
    /**
     * Devuelve el nombre completo.
     * @return nombre
     */
    public String getNombre() { return nombre; }
    /**
     * Devuelve el teléfono de contacto.
     * @return teléfono
     */
    public String getCorreoElectronico() { return correoElectronico; }
    @Override
    public String toString() {
        return nombre + " - " + identificacion;
    }
}
