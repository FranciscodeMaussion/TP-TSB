package com.tsb.interfaz;

import com.tsb.gestores.Gestor;
import com.tsb.negocio.*;
import com.tsb.soporte.Row;
import com.tsb.soporte.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

@Component
public class ControllerPantalla {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerPantalla.class);

    public Button btnCargarArchivos;
    public Label lblPath;
    public ChoiceBox selDistrito;
    public ChoiceBox selSeccion;
    public ChoiceBox selCircuito;
    public ChoiceBox selMesa;
    public Button btnFiltrar;
    public TableView tableAgrupaciones;

    private Gestor gestor;
    private Pais pais = new Pais();
    private String path;

    private ObservableList<Row> listaAgrupacione = FXCollections.observableArrayList();

    private ObservableList<Row> listaDistrito = FXCollections.observableArrayList();
    private ObservableList<Row> listaSeccion = FXCollections.observableArrayList();
    private ObservableList<Row> listaCircuito = FXCollections.observableArrayList();
    private ObservableList<Row> listaMesa = FXCollections.observableArrayList();

    @Autowired
    public void setGestor(Gestor gestor) {
        this.gestor = gestor;
    }

    public void cargarArchivos(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            path = file.getPath();
            lblPath.setText(path);
        }
        gestor.cargarDescripcionPostulaciones(pais, path);
        gestor.cargarDescripcionRegiones(pais, path);
        gestor.cargarResultados(pais, path);
        btnCargarArchivos.setDisable(true);

        Utils.initTableViewSeccion("Agrupacion", listaAgrupacione, tableAgrupaciones);
        popularAgrupaciones();
        popularSelectorDistritos();
    }

    private void popularSelectorDistritos(){
        for (Map.Entry<String, Distrito> current : pais.getRegiones().entrySet()) {
            Row row = new Row(current.getValue().getDescripcion(), current.getKey(), "");
            listaDistrito.add(row);
        }
        selDistrito.setItems(listaDistrito);
    }

    private void popularSelectorSecciones(Distrito distrito){
        for (Map.Entry<String, Seccion> current : distrito.getSecciones().entrySet()) {
            Row row = new Row(current.getValue().getDescripcion(), current.getKey(), "");
            listaSeccion.add(row);
        }
        selSeccion.setItems(listaSeccion);
    }

    private void popularSelectorCircuitos(Seccion seccion){
        for (Map.Entry<String, Circuito> current : seccion.getCircuitos().entrySet()) {
            Row row = new Row(current.getValue().getDescripcion(), current.getKey(), "");
            listaCircuito.add(row);
        }
        selCircuito.setItems(listaCircuito);
    }

    private void popularSelectorDMesas(Circuito circuito){
        for (Map.Entry<String, Mesa> current : circuito.getMesas().entrySet()) {
            Row row = new Row("", current.getKey(), "");
            listaMesa.add(row);
        }
        selMesa.setItems(listaMesa);
    }


    private void popularAgrupaciones() {
        listaAgrupacione.clear();
        Iterator<Map.Entry<String, Agrupacion>> iterator = pais.mostrarResultadosXAgrupacion();
        while (iterator.hasNext()) {
            Map.Entry<String, Agrupacion> entry = iterator.next();
            Agrupacion current = entry.getValue();
            listaAgrupacione.add(new Row(current.getNombreAgrupacion(), "" + current.getVotos(), entry.getKey()));
            LOG.info("Agrupacion: {}, recibio: {}", current.getNombreAgrupacion(), current.getVotos());
        }
        listaAgrupacione.sort((o1, o2) -> {
            // TODO Consider to ask for order row
            Integer first = Integer.parseInt(o1.getColumn2());
            Integer second = Integer.parseInt(o2.getColumn2());
            return second.compareTo(first);
        });
    }


}
