package com.tsb.interfaz;

import com.tsb.gestores.Gestor;
import com.tsb.negocio.*;
import com.tsb.soporte.Row;
import com.tsb.soporte.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

@Component
public class ControllerPantalla {

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
    public TextField txtMesas;
    public Button btnFiltrar;
    public TableView tableAgrupaciones;
    public Button btnLimpiar;
    public Label lblMesa;

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
        btnCargarArchivos.setText(MESSAGE_CARGANDO);
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            path = file.getPath();
            lblPath.setText(path);
        }
        gestor.cargarDescripcionPostulaciones(pais, path);
        gestor.cargarDescripcionRegiones(pais, path);
        gestor.cargarResultados(pais, path);
        btnCargarArchivos.setText(MESSAGE_CARGAR);

        Utils.initTableViewSeccion(MESSAGE_AGRUPACION, listaAgrupacione, tableAgrupaciones);

        selDistrito.setItems(listaDistrito);
        selSeccion.setItems(listaSeccion);
        selCircuito.setItems(listaCircuito);

        initListas();
        popularAgrupaciones();
        popularSelectorDistritos();

        mostrarCamposBusqueda();
    }

    private void mostrarCamposBusqueda(){
        selDistrito.setDisable(false);
        selSeccion.setDisable(false);
        selCircuito.setDisable(false);

        btnFiltrar.setDisable(false);
        btnLimpiar.setDisable(false);
    }

    private void initListas(){
        clearDistrito();
        clearSeccion();
        clearCircuito();
    }

    private void popularSelectorDistritos(){
        for (Map.Entry<String, Distrito> current : pais.getRegiones().entrySet()) {
            Row row = new Row(current.getValue().getDescripcion(), current.getKey(), "");
            listaDistrito.add(row);
        }
        selDistrito.getSelectionModel().select(0);
    }

    public void distritoSelected(ActionEvent event) {
        Row row = (Row) selDistrito.getSelectionModel().getSelectedItem();
        if (row == null || row.getColumn1().equals(TODOS)){
            clearSeccion();
            clearCircuito();
            return;
        }
        selectedDistrito = pais.getRegiones().get(row.getColumn2());
        LOG.info("Distrito {} selected", selectedDistrito);
        clearSeccion();
        clearCircuito();
        popularSelectorSecciones(selectedDistrito);
    }

    private void popularSelectorSecciones(Distrito distrito){
        for (Map.Entry<String, Seccion> current : distrito.getSecciones().entrySet()) {
            Row row = new Row(current.getValue().getDescripcion(), current.getKey(), "");
            listaSeccion.add(row);
        }
    }

    public void seccionSelected(ActionEvent event) {
        Row row = (Row) selSeccion.getSelectionModel().getSelectedItem();
        if (row == null || row.getColumn1().equals(TODOS)){
            clearCircuito();
            return;
        }
        selectedSeccion = selectedDistrito.getSecciones().get(row.getColumn2());
        LOG.info("Seccion {} selected", selectedSeccion);
        clearCircuito();

        popularSelectorCircuitos(selectedSeccion);
    }

    private void popularSelectorCircuitos(Seccion seccion){
        for (Map.Entry<String, Circuito> current : seccion.getCircuitos().entrySet()) {
            Row row = new Row(current.getValue().getDescripcion(), current.getKey(), "");
            listaCircuito.add(row);
        }
    }

    public void circuitoSelected(ActionEvent event) {
        Row row = (Row) selCircuito.getSelectionModel().getSelectedItem();
        if (row == null || row.getColumn1().equals(TODOS)){
            txtMesas.setText("");
            txtMesas.setDisable(true);
            txtMesas.setStyle("-fx-text-inner-color: black;");
            lblMesa.setTextFill(Color.BLACK);
            return;
        }
        selectedCircuito = selectedSeccion.getCircuitos().get(row.getColumn2());
        txtMesas.setDisable(false);
        LOG.info("Seccion {} selected", selectedCircuito);

    }

    private void clearDistrito(){
        selectedDistrito = null;
        selDistrito.getSelectionModel().clearSelection();
        selDistrito.getItems().clear();
        listaDistrito.add(0, new Row(TODOS, null, null));
        selDistrito.getSelectionModel().select(0);
    }

    private void clearSeccion(){
        selectedSeccion = null;
        selSeccion.getSelectionModel().clearSelection();
        selSeccion.getItems().clear();
        listaSeccion.add(0, new Row(TODOS, null, null));
        selSeccion.getSelectionModel().select(0);
    }

    private void clearCircuito(){
        selectedCircuito = null;
        selCircuito.getSelectionModel().clearSelection();
        selCircuito.getItems().clear();
        listaCircuito.add(0, new Row(TODOS, null, null));
        selCircuito.getSelectionModel().select(0);
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


    public void filtrar(ActionEvent actionEvent) {
        Row rowDistrito = (Row) selDistrito.getSelectionModel().getSelectedItem();
        Row rowSeccion = (Row) selSeccion.getSelectionModel().getSelectedItem();
        Row rowCircuito = (Row) selCircuito.getSelectionModel().getSelectedItem();
        Votable selected = null;
        if (rowDistrito.getColumn1().equals(TODOS)){
            popularAgrupaciones();
        } else if (rowSeccion.getColumn1().equals(TODOS)) {
            selected = selectedDistrito;
        } else if (rowCircuito.getColumn1().equals(TODOS)) {
            selected = selectedSeccion;
        } else {
            selected = selectedCircuito;
        }


        String idMesa = txtMesas.getText();
        if(StringUtils.hasText(idMesa)){
            Map<String, Mesa> mesas = selectedCircuito.getMesas();
            selected = mesas.get(idMesa);
        }


        if(selected == null){
            txtMesas.setStyle("-fx-text-inner-color: red;");
            lblMesa.setTextFill(Color.RED);
            return;
        }
        txtMesas.setStyle("-fx-text-inner-color: black;");
        lblMesa.setTextFill(Color.BLACK);

        for (Row row: listaAgrupacione ) {
            int votosAgrupacion = selected.getVotosAgrupacion(row.getColumn3());
            row.setColumn2("" + votosAgrupacion);
        }
        tableAgrupaciones.refresh();
    }

    public void limpiar(ActionEvent actionEvent) {
        selDistrito.getSelectionModel().select(0);
        popularAgrupaciones();
    }
}
