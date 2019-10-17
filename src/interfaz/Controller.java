package interfaz;

import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import negocio.Pais;

import java.io.File;


public class Controller {


    public void cargar(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Pais pais = new Pais(file.getParent());
            pais.cargarResultados();
        }
    }
}
