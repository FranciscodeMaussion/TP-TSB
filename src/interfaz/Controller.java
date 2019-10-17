package interfaz;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;


public class Controller {

    public Label lblArchivo;

    public void cargar(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if(file != null){
            lblArchivo.setText(file.getPath());
        }
    }
}
