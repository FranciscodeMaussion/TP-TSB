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
    //public ChoiceBox selMesa;
    public Button btnFiltrar;
    public TableView tableAgrupaciones;
    public TextField lblMesas;

    private Gestor gestor;
    private Pais pais = new Pais();
    private String path;

    private ObservableList<Row> listaAgrupacione = FXCollections.observableArrayList();

    private ObservableList<Row> listaDistrito = FXCollections.observableArrayList();
    private ObservableList<Row> listaSeccion = FXCollections.observableArrayList();
    private ObservableList<Row> listaCircuito = FXCollections.observableArrayList();
    //private ObservableList<Row> listaMesa = FXCollections.observableArrayList();

    private Distrito selectedDistrito;
    private Seccion selectedSeccion;
    private Circuito selectedCircuito;

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
        selDistrito.setItems(listaDistrito);
        selSeccion.setItems(listaSeccion);
        selCircuito.setItems(listaCircuito);

        popularAgrupaciones();
        popularSelectorDistritos();
    }

    private void popularSelectorDistritos(){
        selDistrito.setDisable(false);
        for (Map.Entry<String, Distrito> current : pais.getRegiones().entrySet()) {
            Row row = new Row(current.getValue().getDescripcion(), current.getKey(), "");
            listaDistrito.add(row);
        }
    }

    public void distritoSelected(ActionEvent event) {
        Row row = (Row) selDistrito.getSelectionModel().getSelectedItem();
        if (row == null){
            return;
        }
        selectedDistrito = pais.getRegiones().get(row.getColumn2());
        LOG.info("Distrito " + selectedDistrito + " selected");
        clearSeccion();
        clearCircuito();
        selCircuito.setDisable(true);
        lblMesas.setDisable(true);
        popularSelectorSecciones(selectedDistrito);
    }

    private void popularSelectorSecciones(Distrito distrito){
        selSeccion.setDisable(false);
        for (Map.Entry<String, Seccion> current : distrito.getSecciones().entrySet()) {
            Row row = new Row(current.getValue().getDescripcion(), current.getKey(), "");
            listaSeccion.add(row);
        }
    }

    public void seccionSelected(ActionEvent event) {
        Row row = (Row) selSeccion.getSelectionModel().getSelectedItem();
        if (row == null){
            return;
        }
        selectedSeccion = selectedDistrito.getSecciones().get(row.getColumn2());
        LOG.info("Seccion " + selectedSeccion + " selected");
        clearCircuito();
        lblMesas.setDisable(true);
        popularSelectorCircuitos(selectedSeccion);
    }

    private void popularSelectorCircuitos(Seccion seccion){
        selCircuito.setDisable(false);
        for (Map.Entry<String, Circuito> current : seccion.getCircuitos().entrySet()) {
            Row row = new Row(current.getValue().getDescripcion(), current.getKey(), "");
            listaCircuito.add(row);
        }
    }

    public void circuitoSelected(ActionEvent event) {
        Row row = (Row) selCircuito.getSelectionModel().getSelectedItem();
        if (row == null){
            return;
        }
        selectedCircuito = selectedSeccion.getCircuitos().get(row.getColumn2());
        LOG.info("Seccion " + selectedCircuito + " selected");
        lblMesas.setDisable(false);
        //popularSelectorDMesas(Circuito);
    }

    /*private void popularSelectorDMesas(Circuito circuito){
        selMesa.setDisable(false);
        for (Map.Entry<String, Mesa> current : circuito.getMesas().entrySet()) {
            Row row = new Row("", current.getKey(), "");
            listaMesa.add(row);
        }
        selMesa.setItems(listaMesa);
    }*/


    private void clearSeccion(){
        selectedSeccion = null;
        selSeccion.getSelectionModel().clearSelection();
        selSeccion.getItems().clear();
    }

    private void clearCircuito(){
        selectedCircuito = null;
        selCircuito.getSelectionModel().clearSelection();
        selCircuito.getItems().clear();
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
