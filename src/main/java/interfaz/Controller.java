package interfaz;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import negocio.Pais;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class Controller {

    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    public Button btnCargarAgrupaciones;
    public Button btnCargarRegiones;
    public Button btnCargarVotos;
    public Label lblPath;
    public Label lblAgrupaciones;
    public Label lblRegiones;
    private Pais pais;

    public void cargarAgrupaciones(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            String path = file.getPath();
            lblPath.setText(path);
            pais = new Pais(path);
            pais.cargarDescripcionPostulaciones();
            btnCargarAgrupaciones.setDisable(true);
            btnCargarRegiones.setDisable(false);
            lblAgrupaciones.setText("" + pais.getAgrupacionesCargadas());
        }
    }

    public void cargarRegiones(ActionEvent actionEvent) {
        pais.cargarDescripcionRegiones();
        lblRegiones.setText("" + pais.getRegionesCargadas());
        btnCargarRegiones.setDisable(true);
        btnCargarVotos.setDisable(false);
    }

    public void cargarVotos(ActionEvent actionEvent) {
        pais.cargarResultados();
        btnCargarVotos.setDisable(true);
    }
}
