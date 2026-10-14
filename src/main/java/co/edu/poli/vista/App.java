package co.edu.poli.vista;

import co.edu.poli.aplication.port.out.SeguroRepository;
import co.edu.poli.aplication.service.ActualizarSeguroService;
import co.edu.poli.aplication.service.ConsultarSeguroPorIdService;
import co.edu.poli.aplication.service.ConsultarSeguroPorPolizaService;
import co.edu.poli.aplication.service.CrearSeguroService;
import co.edu.poli.aplication.service.EliminarSeguroService;
import co.edu.poli.aplication.service.ListarAseguradosService;
import co.edu.poli.aplication.service.ListarSegurosService;
import co.edu.poli.infrastructure.persistence.MysqlSeguroRepository;
import co.edu.poli.infrastructure.ui.Controller;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Punto de entrada JavaFX y composición de los adaptadores de la aplicación. */
public final class App extends Application {
    /** Construye el punto de entrada JavaFX. */
    public App() {
    }

    @Override
    public void start(Stage stage) throws IOException {
        SeguroRepository repository = new MysqlSeguroRepository();
        Controller controller = new Controller(new CrearSeguroService(repository),
                new ConsultarSeguroPorIdService(repository), new ConsultarSeguroPorPolizaService(repository),
                new ListarSegurosService(repository), new ActualizarSeguroService(repository),
                new EliminarSeguroService(repository), new ListarAseguradosService(repository));
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/co/edu/poli/vista/vista.fxml"));
        loader.setControllerFactory(type -> controller);
        Scene scene = new Scene(loader.load(), 1120, 800);
        stage.setTitle("Gestión de Seguros");
        stage.setMinWidth(920);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Inicia el ciclo de vida de JavaFX.
     * @param args argumentos de ejecución
     */
    public static void main(String[] args) {
        launch(args);
    }
}
