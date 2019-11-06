package com.tsb.interfaz;

import com.tsb.gestores.Gestor;
import com.tsb.negocio.*;
import com.tsb.soporte.Row;
import com.tsb.soporte.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

@Component
public class ControllerPantalla implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerPantalla.class);

    private static final String MESSAGE_CARGANDO = "Cargando...";
    private static final String MESSAGE_CARGAR = "Cargar Archivos";

    private static final String MESSAGE_AGRUPACION = "Agrupacion";

    private static final String TODOS = "Todos";

    public Button btnCargarArchivos;
    public Label lblPath;
    public ChoiceBox selDistrito;
    public ChoiceBox selSeccion;
    public ChoiceBox selCircuito;
    public Button btnFiltrar;
    public TableView tableAgrupaciones;
    public Button btnLimpiar;
    public ChoiceBox selMesas;

    private Gestor gestor;
    private Pais pais = new Pais();
    private String path;

    private ObservableList<Row> listaAgrupaciones = FXCollections.observableArrayList();

    private ObservableList<Row> listaDistrito = FXCollections.observableArrayList();
    private ObservableList<Row> listaSeccion = FXCollections.observableArrayList();
    private ObservableList<Row> listaCircuito = FXCollections.observableArrayList();
    private ObservableList<Row> listaMesa = FXCollections.observableArrayList();

    private Distrito selectedDistrito;
    private Seccion selectedSeccion;
    private Circuito selectedCircuito;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Utils.initTableViewSeccion(MESSAGE_AGRUPACION, listaAgrupaciones, tableAgrupaciones);

        selDistrito.setItems(listaDistrito);
        selSeccion.setItems(listaSeccion);
        selCircuito.setItems(listaCircuito);
        selMesas.setItems(listaMesa);
    }


    @Autowired
    public void setGestor(Gestor gestor) {
        this.gestor = gestor;
    }

    public void cargarArchivos(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        btnCargarArchivos.setText(MESSAGE_CARGANDO);
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            path = file.getPath();
            lblPath.setText(path);
        }
        if (path != null){
            gestor.cargarDescripcionPostulaciones(pais, path);
            gestor.cargarDescripcionRegiones(pais, path);
            gestor.cargarResultados(pais, path);
            initListas();

            popularAgrupaciones();
            popularSelectorDistritos();

            mostrarCamposBusqueda();
        }
        btnCargarArchivos.setText(MESSAGE_CARGAR);
    }

    private void mostrarCamposBusqueda() {
        selDistrito.setDisable(false);
        selSeccion.setDisable(false);
        selCircuito.setDisable(false);
        selMesas.setDisable(false);

        btnFiltrar.setDisable(false);
        btnLimpiar.setDisable(false);
    }

    private void initListas() {
        clearDistrito();
        clearSeccion();
        clearCircuito();
    }

    private void popularSelectorDistritos() {
        for (Map.Entry<String, Distrito> current : pais.getRegiones().entrySet()) {
            Row row = new Row(current.getValue().getDescripcion(), "", current.getKey());
            listaDistrito.add(row);
        }
        selDistrito.getSelectionModel().select(0);
    }

    public void distritoSelected(ActionEvent event) {
        Row row = (Row) selDistrito.getSelectionModel().getSelectedItem();
        if (row == null || row.getId() == null) {
            clearSeccion();
            clearCircuito();
            return;
        }
        selectedDistrito = pais.getRegiones().get(row.getId());
        LOG.info("Distrito {} selected", selectedDistrito);
        clearSeccion();
        clearCircuito();
        popularSelectorSecciones(selectedDistrito);
    }

    private void popularSelectorSecciones(Distrito distrito) {
        for (Map.Entry<String, Seccion> current : distrito.getSecciones().entrySet()) {
            Row row = new Row(current.getValue().getDescripcion(), "", current.getKey());
            listaSeccion.add(row);
        }
    }

    public void seccionSelected(ActionEvent event) {
        Row row = (Row) selSeccion.getSelectionModel().getSelectedItem();
        if (row == null || row.getId() == null) {
            clearCircuito();
            return;
        }
        selectedSeccion = selectedDistrito.getSecciones().get(row.getId());
        LOG.info("Seccion {} selected", selectedSeccion);
        clearCircuito();

        popularSelectorCircuitos(selectedSeccion);
    }

    private void popularSelectorCircuitos(Seccion seccion) {
        for (Map.Entry<String, Circuito> current : seccion.getCircuitos().entrySet()) {
            Row row = new Row(current.getValue().getDescripcion(), "", current.getKey());
            listaCircuito.add(row);
        }
    }

    public void circuitoSelected(ActionEvent event) {
        Row row = (Row) selCircuito.getSelectionModel().getSelectedItem();
        if (row == null || row.getId() == null) {
            clearMesa();
            return;
        }
        selectedCircuito = selectedSeccion.getCircuitos().get(row.getId());
        LOG.info("Seccion {} selected", selectedCircuito);
        clearMesa();

        popularSelectorMesas(selectedCircuito);
    }

    private void popularSelectorMesas(Circuito circuito) {
        for (Map.Entry<String, Mesa> current : circuito.getMesas().entrySet()) {
            Row row = new Row(current.getKey(), "", current.getKey());
            listaMesa.add(row);
        }
    }

    private void clearMesa() {
        selMesas.getSelectionModel().clearSelection();
        selMesas.getItems().clear();
        listaMesa.add(0, new Row(TODOS, null, null));
        selMesas.getSelectionModel().select(0);
    }

    private void clearDistrito() {
        selectedDistrito = null;
        selDistrito.getSelectionModel().clearSelection();
        selDistrito.getItems().clear();
        listaDistrito.add(0, new Row(TODOS, null, null));
        selDistrito.getSelectionModel().select(0);
    }

    private void clearSeccion() {
        selectedSeccion = null;
        selSeccion.getSelectionModel().clearSelection();
        selSeccion.getItems().clear();
        listaSeccion.add(0, new Row(TODOS, null, null));
        selSeccion.getSelectionModel().select(0);
    }

    private void clearCircuito() {
        selectedCircuito = null;
        selCircuito.getSelectionModel().clearSelection();
        selCircuito.getItems().clear();
        listaCircuito.add(0, new Row(TODOS, null, null));
        selCircuito.getSelectionModel().select(0);
    }

    private void popularAgrupaciones() {
        listaAgrupaciones.clear();
        Iterator<Map.Entry<String, Agrupacion>> iterator = pais.mostrarResultadosXAgrupacion();
        while (iterator.hasNext()) {
            Map.Entry<String, Agrupacion> entry = iterator.next();
            Agrupacion current = entry.getValue();
            listaAgrupaciones.add(new Row(current.getNombreAgrupacion(), "" + current.getVotos(), entry.getKey()));
            LOG.info("Agrupacion: {}, recibio: {}", current.getNombreAgrupacion(), current.getVotos());
        }
        listaAgrupaciones.sort((o1, o2) -> {
            Integer first = Integer.parseInt(o1.getVotos());
            Integer second = Integer.parseInt(o2.getVotos());
            return second.compareTo(first);
        });
    }


    public void filtrar(ActionEvent actionEvent) {
        Row rowDistrito = (Row) selDistrito.getSelectionModel().getSelectedItem();
        Row rowSeccion = (Row) selSeccion.getSelectionModel().getSelectedItem();
        Row rowCircuito = (Row) selCircuito.getSelectionModel().getSelectedItem();
        Row rowMesa = (Row) selMesas.getSelectionModel().getSelectedItem();
        Votable selected = null;
        if (rowDistrito.getId() == null) {
            popularAgrupaciones();
        } else if (rowSeccion.getId() == null) {
            selected = selectedDistrito;
        } else if (rowCircuito.getId() == null) {
            selected = selectedSeccion;
        } else if (rowMesa.getId() == null) {
            selected = selectedCircuito;
        } else {
            selected = selectedCircuito.getMesas().get(rowMesa.getId());
        }
        if (selected == null) {
            return;
        }
        for (Row row : listaAgrupaciones) {
            int votosAgrupacion = selected.getVotosAgrupacion(row.getId());
            row.setVotos("" + votosAgrupacion);
        }
        tableAgrupaciones.refresh();
    }

    public void limpiar(ActionEvent actionEvent) {
        selDistrito.getSelectionModel().select(0);
        popularAgrupaciones();
    }
}
