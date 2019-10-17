package interfaz;

import javafx.event.ActionEvent;
import javafx.stage.DirectoryChooser;
import negocio.Pais;

import java.io.File;


public class Controller {


    public void cargar(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            Pais pais = new Pais(file.getPath());
            pais.cargarResultados();
        }
    }
}
