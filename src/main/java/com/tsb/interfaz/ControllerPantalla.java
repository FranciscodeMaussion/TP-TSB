package com.tsb.interfaz;

import com.tsb.gestores.Gestor;
import com.tsb.interfaz.helpers.AyudantePantalla;
import com.tsb.negocio.*;
import com.tsb.soporte.Row;
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
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
    private AyudantePantalla ayudantePantalla;
    private Pais pais;


    private ObservableList<Row> listaAgrupaciones = FXCollections.observableArrayList();
    private ObservableList<Row> listaDistrito = FXCollections.observableArrayList();
    private ObservableList<Row> listaSeccion = FXCollections.observableArrayList();
    private ObservableList<Row> listaCircuito = FXCollections.observableArrayList();
    private ObservableList<Row> listaMesa = FXCollections.observableArrayList();

    private Distrito distritoSeleccionado;
    private Seccion seccionSeleccionada;
    private Circuito circuitoSeleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pais = new Pais();
        ayudantePantalla.inicializarTableView(MESSAGE_AGRUPACION, listaAgrupaciones, tableAgrupaciones);

        selDistrito.setItems(listaDistrito);
        selSeccion.setItems(listaSeccion);
        selCircuito.setItems(listaCircuito);
        selMesas.setItems(listaMesa);
    }


    @Autowired
    public void setGestor(Gestor gestor) {
        this.gestor = gestor;
    }

    @Autowired
    public void setAyudantePantalla(AyudantePantalla ayudantePantalla) {
        this.ayudantePantalla = ayudantePantalla;
    }

    //Cuando se selecciona Cargar Archivos y se elije la carpeta con los archivos
    public void cargarArchivos(ActionEvent actionEvent) {
        String path = null;
        DirectoryChooser directoryChooser = new DirectoryChooser();
        btnCargarArchivos.setText(MESSAGE_CARGANDO);
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            path = file.getPath();
            lblPath.setText(path);
        }
        if (path != null) {
            gestor.cargarDescripcionPostulaciones(pais, path);
            gestor.cargarDescripcionRegiones(pais, path);
            gestor.cargarResultados(pais, path);
            initListas();

            ayudantePantalla.popularAgrupaciones(listaAgrupaciones, pais);
            ayudantePantalla.popularSelectorDistritos(listaDistrito, pais, selDistrito);

            mostrarCamposBusqueda();
        }
        btnCargarArchivos.setText(MESSAGE_CARGAR);
    }

    //Cuando se selecciona un elemento de la lista de Distritos
    public void distritoSelected(ActionEvent event) {
        Row row = (Row) selDistrito.getSelectionModel().getSelectedItem();
        if (row == null || row.getId() == null) {
            clearSeccion();
            clearCircuito();
            return;
        }
        distritoSeleccionado = pais.getRegiones().get(row.getId());
        LOG.info("Distrito {} selected", distritoSeleccionado);
        clearSeccion();
        clearCircuito();
        ayudantePantalla.popularSelectorSecciones(listaSeccion, distritoSeleccionado);
    }

    //Cuando se selecciona un elemento de la lista de Secciones
    public void seccionSelected(ActionEvent event) {
        Row row = (Row) selSeccion.getSelectionModel().getSelectedItem();
        if (row == null || row.getId() == null) {
            clearCircuito();
            return;
        }
        seccionSeleccionada = distritoSeleccionado.getSecciones().get(row.getId());
        LOG.info("Seccion {} selected", seccionSeleccionada);
        clearCircuito();

        ayudantePantalla.popularSelectorCircuitos(listaCircuito, seccionSeleccionada);
    }

    //Cuando se selecciona un elemento de la lista de Circuitos
    public void circuitoSelected(ActionEvent event) {
        Row row = (Row) selCircuito.getSelectionModel().getSelectedItem();
        if (row == null || row.getId() == null) {
            clearMesa();
            return;
        }
        circuitoSeleccionado = seccionSeleccionada.getCircuitos().get(row.getId());
        LOG.info("Seccion {} selected", circuitoSeleccionado);
        clearMesa();

        ayudantePantalla.popularSelectorMesas(listaMesa, circuitoSeleccionado);
    }

    //Cuando se preciona el boton "Aplicar Filtros"
    public void filtrar(ActionEvent actionEvent) {
        Row rowDistrito = (Row) selDistrito.getSelectionModel().getSelectedItem();
        Row rowSeccion = (Row) selSeccion.getSelectionModel().getSelectedItem();
        Row rowCircuito = (Row) selCircuito.getSelectionModel().getSelectedItem();
        Row rowMesa = (Row) selMesas.getSelectionModel().getSelectedItem();
        Votable selected = null;
        if (rowDistrito.getId() == null) {
            ayudantePantalla.popularAgrupaciones(listaAgrupaciones, pais);
        } else if (rowSeccion.getId() == null) {
            selected = distritoSeleccionado;
        } else if (rowCircuito.getId() == null) {
            selected = seccionSeleccionada;
        } else if (rowMesa.getId() == null) {
            selected = circuitoSeleccionado;
        } else {
            selected = circuitoSeleccionado.getMesas().get(rowMesa.getId());
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

    //Cuando se preciona el boton "Limpiar Filtros"
    public void limpiar(ActionEvent actionEvent) {
        selDistrito.getSelectionModel().select(0);
        ayudantePantalla.popularAgrupaciones(listaAgrupaciones, pais);
    }


    //Metodos para administrar la parte visual de la pantalla

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

    private void clearMesa() {
        selMesas.getSelectionModel().clearSelection();
        selMesas.getItems().clear();
        listaMesa.add(0, new Row(TODOS, null, null));
        selMesas.getSelectionModel().select(0);
    }

    private void clearDistrito() {
        distritoSeleccionado = null;
        selDistrito.getSelectionModel().clearSelection();
        selDistrito.getItems().clear();
        listaDistrito.add(0, new Row(TODOS, null, null));
        selDistrito.getSelectionModel().select(0);
    }

    private void clearSeccion() {
        seccionSeleccionada = null;
        selSeccion.getSelectionModel().clearSelection();
        selSeccion.getItems().clear();
        listaSeccion.add(0, new Row(TODOS, null, null));
        selSeccion.getSelectionModel().select(0);
    }

    private void clearCircuito() {
        circuitoSeleccionado = null;
        selCircuito.getSelectionModel().clearSelection();
        selCircuito.getItems().clear();
        listaCircuito.add(0, new Row(TODOS, null, null));
        selCircuito.getSelectionModel().select(0);
    }

}
