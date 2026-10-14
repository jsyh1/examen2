package co.edu.poli.infrastructure.ui;

import co.edu.poli.aplication.domain.model.Asegurado;
import co.edu.poli.aplication.domain.model.EstadoSeguro;
import co.edu.poli.aplication.domain.model.Seguro;
import co.edu.poli.aplication.domain.model.SeguroVehiculo;
import co.edu.poli.aplication.domain.model.SeguroVida;
import co.edu.poli.aplication.port.in.ActualizarSeguroUseCase;
import co.edu.poli.aplication.port.in.ConsultarSeguroPorIdUseCase;
import co.edu.poli.aplication.port.in.ConsultarSeguroPorPolizaUseCase;
import co.edu.poli.aplication.port.in.CrearSeguroUseCase;
import co.edu.poli.aplication.port.in.EliminarSeguroUseCase;
import co.edu.poli.aplication.port.in.ListarAseguradosUseCase;
import co.edu.poli.aplication.port.in.ListarSegurosUseCase;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TableCell;
import javafx.beans.property.ReadOnlyStringWrapper;

/** Adaptador JavaFX: traduce acciones de la vista a casos de uso. */
public final class Controller {
    @FXML private TextField txtId;
    @FXML private TextField txtNumeroPoliza;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtPrima;
    @FXML private TextField txtFechaInicio;
    @FXML private TextField txtFechaFin;
    @FXML private TextField txtBeneficiario;
    @FXML private TextField txtCobertura;
    @FXML private TextField txtPlaca;
    @FXML private TextField txtMarca;
    @FXML private TextField txtModelo;
    @FXML private TextField txtValorAsegurado;
    @FXML private TextField txtBusqueda;
    @FXML private ChoiceBox<String> cbTipo;
    @FXML private ChoiceBox<EstadoSeguro> cbEstado;
    @FXML private ChoiceBox<String> cbCriterio;
    @FXML private ComboBox<Asegurado> cbAsegurado;
    @FXML private GridPane camposVida;
    @FXML private GridPane camposVehiculo;
    @FXML private Label lblResultado;
    @FXML private TableView<Seguro> tblSeguros;
    @FXML private TableColumn<Seguro, Integer> colId;
    @FXML private TableColumn<Seguro, String> colPoliza;
    @FXML private TableColumn<Seguro, String> colTipo;
    @FXML private TableColumn<Seguro, String> colAsegurado;
    @FXML private TableColumn<Seguro, String> colEstado;
    @FXML private TableColumn<Seguro, String> colVigencia;

    private final CrearSeguroUseCase crearSeguro;
    private final ConsultarSeguroPorIdUseCase consultarPorId;
    private final ConsultarSeguroPorPolizaUseCase consultarPorPoliza;
    private final ListarSegurosUseCase listarSeguros;
    private final ActualizarSeguroUseCase actualizarSeguro;
    private final EliminarSeguroUseCase eliminarSeguro;
    private final ListarAseguradosUseCase listarAsegurados;

    /** Recibe los contratos de entrada necesarios para las acciones visibles. */
    public Controller(CrearSeguroUseCase crearSeguro, ConsultarSeguroPorIdUseCase consultarPorId,
                      ConsultarSeguroPorPolizaUseCase consultarPorPoliza, ListarSegurosUseCase listarSeguros,
                      ActualizarSeguroUseCase actualizarSeguro, EliminarSeguroUseCase eliminarSeguro,
                      ListarAseguradosUseCase listarAsegurados) {
        this.crearSeguro = crearSeguro;
        this.consultarPorId = consultarPorId;
        this.consultarPorPoliza = consultarPorPoliza;
        this.listarSeguros = listarSeguros;
        this.actualizarSeguro = actualizarSeguro;
        this.eliminarSeguro = eliminarSeguro;
        this.listarAsegurados = listarAsegurados;
    }

    @FXML
    private void initialize() {
        txtId.setEditable(false);
        cbTipo.setItems(FXCollections.observableArrayList("VIDA", "VEHICULO"));
        cbTipo.setValue("VIDA");
        cbEstado.setItems(FXCollections.observableArrayList(EstadoSeguro.values()));
        cbEstado.setValue(EstadoSeguro.PENDIENTE);
        cbCriterio.setItems(FXCollections.observableArrayList("Póliza", "ID"));
        cbCriterio.setValue("Póliza");
        cbTipo.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, actual) -> mostrarCamposTipo(actual));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPoliza.setCellValueFactory(new PropertyValueFactory<>("numeroPoliza"));
        colTipo.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getTipoSeguro()));
        colAsegurado.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getAsegurado().getNombre()));
        colEstado.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getEstado().name()));
        colVigencia.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().getFechaInicio() + " a " + cell.getValue().getFechaFin()));
        tblSeguros.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionado) -> mostrarSeguro(seleccionado));
        mostrarCamposTipo("VIDA");
        cargarAsegurados();
        cargarSeguros();
    }

    @FXML
    private void nuevoSeguro() {
        limpiarFormulario();
        tblSeguros.getSelectionModel().clearSelection();
        lblResultado.setText("Nueva póliza");
    }

    @FXML
    private void guardarSeguro() {
        try {
            Seguro creado = crearSeguro.crearSeguro(leerSeguro(0));
            cargarSeguros();
            limpiarFormulario();
            informar("Póliza creada", "Se registró la póliza " + creado.getNumeroPoliza() + ".");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            mostrarError("No se pudo guardar la póliza", exception.getMessage());
        }
    }

    @FXML
    private void actualizarSeguro() {
        try {
            Seguro actualizado = leerSeguro(leerId());
            Optional<Seguro> resultado = actualizarSeguro.actualizarSeguro(actualizado);
            if (resultado.isEmpty()) {
                mostrarError("Póliza no encontrada", "La póliza seleccionada ya no existe.");
                return;
            }
            cargarSeguros();
            informar("Póliza actualizada", "Los cambios se guardaron correctamente.");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            mostrarError("No se pudo actualizar la póliza", exception.getMessage());
        }
    }

    @FXML
    private void eliminarSeguro() {
        try {
            int id = leerId();
            if (!eliminarSeguro.eliminarSeguro(id)) {
                mostrarError("Póliza no encontrada", "No existe una póliza con el ID indicado.");
                return;
            }
            cargarSeguros();
            limpiarFormulario();
            informar("Póliza eliminada", "La póliza se eliminó correctamente.");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            mostrarError("No se pudo eliminar la póliza", exception.getMessage());
        }
    }

    @FXML
    private void buscarSeguro() {
        try {
            String busqueda = txtBusqueda.getText().trim();
            Optional<Seguro> encontrado;
            if ("ID".equals(cbCriterio.getValue())) {
                encontrado = consultarPorId.consultarPorId(Integer.parseInt(busqueda));
            } else {
                encontrado = consultarPorPoliza.consultarPorNumeroPoliza(busqueda);
            }
            if (encontrado.isEmpty()) {
                tblSeguros.setItems(FXCollections.observableArrayList());
                mostrarError("Sin resultados", "No se encontró una póliza con ese criterio.");
                return;
            }
            tblSeguros.setItems(FXCollections.observableArrayList(encontrado.orElseThrow()));
            tblSeguros.getSelectionModel().selectFirst();
        } catch (NumberFormatException exception) {
            mostrarError("Búsqueda inválida", "Ingresa un ID numérico válido.");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            mostrarError("No se pudo realizar la búsqueda", exception.getMessage());
        }
    }

    @FXML
    private void listarTodos() {
        cargarSeguros();
        txtBusqueda.clear();
    }

    private void cargarSeguros() {
        try {
            List<Seguro> seguros = listarSeguros.listarSeguros();
            tblSeguros.setItems(FXCollections.observableArrayList(seguros));
            lblResultado.setText(seguros.size() + " pólizas");
        } catch (IllegalStateException exception) {
            mostrarError("No se pudieron cargar las pólizas", exception.getMessage());
        }
    }

    private void cargarAsegurados() {
        try {
            cbAsegurado.setItems(FXCollections.observableArrayList(listarAsegurados.listarAsegurados()));
            if (!cbAsegurado.getItems().isEmpty()) {
                cbAsegurado.getSelectionModel().selectFirst();
            }
        } catch (IllegalStateException exception) {
            mostrarError("No se pudo cargar el catálogo de asegurados", exception.getMessage());
        }
    }

    private Seguro leerSeguro(int id) {
        String tipo = cbTipo.getValue();
        if (tipo == null) {
            throw new IllegalArgumentException("Selecciona el tipo de seguro.");
        }
        Asegurado asegurado = cbAsegurado.getValue();
        if (asegurado == null) {
            throw new IllegalArgumentException("No hay asegurados disponibles en la base de datos.");
        }
        String numero = txtNumeroPoliza.getText().trim();
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText();
        BigDecimal prima = decimal(txtPrima.getText(), "La prima");
        LocalDate inicio = fecha(txtFechaInicio.getText(), "La fecha de inicio");
        LocalDate fin = fecha(txtFechaFin.getText(), "La fecha de fin");
        EstadoSeguro estado = cbEstado.getValue();
        if ("VIDA".equals(tipo)) {
            return new SeguroVida(id, numero, nombre, descripcion, prima, inicio, fin, estado, asegurado,
                    txtBeneficiario.getText(), decimal(txtCobertura.getText(), "La cobertura"));
        }
        return new SeguroVehiculo(id, numero, nombre, descripcion, prima, inicio, fin, estado, asegurado,
                txtPlaca.getText(), txtMarca.getText(), txtModelo.getText(),
                decimal(txtValorAsegurado.getText(), "El valor asegurado"));
    }

    private BigDecimal decimal(String texto, String campo) {
        try {
            return new BigDecimal(texto.trim());
        } catch (NumberFormatException | NullPointerException exception) {
            throw new IllegalArgumentException(campo + " debe ser un importe válido, por ejemplo 1250.00.", exception);
        }
    }

    private LocalDate fecha(String texto, String campo) {
        try {
            return LocalDate.parse(texto.trim());
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException(campo + " debe usar el formato AAAA-MM-DD.", exception);
        }
    }

    private int leerId() {
        try {
            return Integer.parseInt(txtId.getText().trim());
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Selecciona una póliza de la tabla.", exception);
        }
    }

    private void mostrarSeguro(Seguro seguro) {
        if (seguro == null) {
            return;
        }
        txtId.setText(String.valueOf(seguro.getId()));
        txtNumeroPoliza.setText(seguro.getNumeroPoliza());
        txtNombre.setText(seguro.getNombre());
        txtDescripcion.setText(seguro.getDescripcion());
        txtPrima.setText(seguro.getValorPrima().toPlainString());
        txtFechaInicio.setText(seguro.getFechaInicio().toString());
        txtFechaFin.setText(seguro.getFechaFin().toString());
        cbEstado.setValue(seguro.getEstado());
        cbTipo.setValue(seguro.getTipoSeguro());
        cbAsegurado.getSelectionModel().select(seguro.getAsegurado());
        if (seguro instanceof SeguroVida vida) {
            txtBeneficiario.setText(vida.getBeneficiario());
            txtCobertura.setText(vida.getValorCobertura().toPlainString());
        } else if (seguro instanceof SeguroVehiculo vehiculo) {
            txtPlaca.setText(vehiculo.getPlaca());
            txtMarca.setText(vehiculo.getMarca());
            txtModelo.setText(vehiculo.getModelo());
            txtValorAsegurado.setText(vehiculo.getValorAsegurado().toPlainString());
        }
        lblResultado.setText("Editando póliza " + seguro.getNumeroPoliza());
    }

    private void mostrarCamposTipo(String tipo) {
        boolean vida = "VIDA".equals(tipo);
        camposVida.setVisible(vida);
        camposVida.setManaged(vida);
        camposVehiculo.setVisible(!vida);
        camposVehiculo.setManaged(!vida);
    }

    private void limpiarFormulario() {
        txtId.clear();
        txtNumeroPoliza.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrima.clear();
        txtFechaInicio.clear();
        txtFechaFin.clear();
        txtBeneficiario.clear();
        txtCobertura.clear();
        txtPlaca.clear();
        txtMarca.clear();
        txtModelo.clear();
        txtValorAsegurado.clear();
        cbEstado.setValue(EstadoSeguro.PENDIENTE);
    }

    private void informar(String encabezado, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Gestión de seguros");
        alerta.setHeaderText(encabezado);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String encabezado, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Gestión de seguros");
        alerta.setHeaderText(encabezado);
        alerta.setContentText(mensaje == null ? "Ocurrió un error inesperado." : mensaje);
        alerta.showAndWait();
    }
}
