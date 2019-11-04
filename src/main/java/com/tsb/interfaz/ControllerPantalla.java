package com.tsb.interfaz;

import com.tsb.gestores.Gestor;
import com.tsb.negocio.*;
import com.tsb.soporte.Row;
import com.tsb.soporte.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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
    public Button btnCargarAgrupaciones;
    public Button btnCargarRegiones;
    public Button btnCargarVotos;
    public Label lblPath;
    public Label lblAgrupaciones;
    public TabPane tabResultados;
    public TableView<Row> tableVotosAgrupacion;
    public TableView<Row> tableVotosDistrito;
    public Label lblMesas;
    public Label lblDistritos;
    public Label lblSecciones;
    public Label lblCircuitos;
    public TableView<Row> tableVotosSeccion;
    public TableView<Row> tableVotosCircuito;
    public Label lblTextHint;
    public Tab tbAgrupaciones;
    public Tab tbDistritos;
    public Tab tbSecciones;
    public Tab tbCircuitos;
    private Gestor gestor;
    private Pais pais = new Pais();
    private String path;
    private ObservableList<Row> listAgrupacion = FXCollections.observableArrayList();
    private ObservableList<Row> listDistritos = FXCollections.observableArrayList();
    private ObservableList<Row> listSecciones = FXCollections.observableArrayList();
    private ObservableList<Row> listCircuitos = FXCollections.observableArrayList();

    private Distrito distritoSeleccionado;
    private Seccion seccionSeleccionada;

    public void cargarAgrupaciones(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            path = file.getPath();
            lblPath.setText(path);
        }

        gestor.cargarDescripcionPostulaciones(pais, path);
        lblTextHint.setVisible(false);

        btnCargarAgrupaciones.setDisable(true);
        tbAgrupaciones.setDisable(false);
        btnCargarRegiones.setDisable(false);
        lblAgrupaciones.setText(lblAgrupaciones.getText() + pais.getAgrupacionesSize());
        Utils.initTableViewSeccion("Agrupacion", listAgrupacion, tableVotosAgrupacion);
        popularAgrupaciones();
    }

    private void popularAgrupaciones() {
        listAgrupacion.clear();
        Iterator<Map.Entry<String, Agrupacion>> iterator = pais.mostrarResultadosXAgrupacion();
        while (iterator.hasNext()) {
            Map.Entry<String, Agrupacion> entry = iterator.next();
            Agrupacion current = entry.getValue();
            listAgrupacion.add(new Row(current.getNombreAgrupacion(), "" + current.getAcumulador().getCantidad(), entry.getKey()));
            LOG.info("Agrupacion: {}, recibio: {}", current.getNombreAgrupacion(), current.getAcumulador().getCantidad());
        }
        listAgrupacion.sort((o1, o2) -> {
            // TODO Consider to ask for order row
            Integer first = Integer.parseInt(o1.getColumn2());
            Integer second = Integer.parseInt(o2.getColumn2());
            return second.compareTo(first);
        });
    }

    public void cargarRegiones(ActionEvent actionEvent) {

        gestor.cargarDescripcionRegiones(pais, path);

        lblDistritos.setText(lblDistritos.getText() + pais.getDistritosSize());
        lblSecciones.setText(lblSecciones.getText() + pais.getSeccionesSize());
        lblCircuitos.setText(lblCircuitos.getText() + pais.getCircuitosSize());
        btnCargarRegiones.setDisable(true);
        tbDistritos.setDisable(false);
        btnCargarVotos.setDisable(false);
        //initTableViewDistrito();
        Utils.initTableViewSeccion("Distrito", listDistritos, tableVotosDistrito);
        popularDistritos();
    }

    private void popularDistritos() {
        listDistritos.clear();
        Iterator<Map.Entry<String, Distrito>> iterator = pais.mostrarResultadosXDistrito();
        while (iterator.hasNext()) {
            Map.Entry<String, Distrito> entry = iterator.next();
            Distrito current = entry.getValue();
            listDistritos.add(new Row(current.getDescripcion(), "" + current.getAcumulador().getCantidad(), entry.getKey()));
            LOG.info("Distrito: {}, recibio: {}", current.getDescripcion(), current.getAcumulador().getCantidad());
        }
        listDistritos.sort((o1, o2) -> {
            Integer first = Integer.parseInt(o1.getColumn2());
            Integer second = Integer.parseInt(o2.getColumn2());
            return second.compareTo(first);
        });
    }

    public void cargarVotos(ActionEvent actionEvent) {
        gestor.cargarResultados(pais, path);
        lblMesas.setText(lblMesas.getText() + pais.getMesasSize());
        btnCargarVotos.setDisable(true);
        popularAgrupaciones();
        popularDistritos();

    }

    @Autowired
    public void setGestor(Gestor gestor) {
        this.gestor = gestor;
    }

    public void rowDistritoSelected(MouseEvent mouseEvent) {
        Node node = ((Node) mouseEvent.getTarget()).getParent();
        TableRow row;
        if (mouseEvent.getClickCount() == 2) {
            if (node instanceof TableRow) {
                row = (TableRow) node;
            } else {
                row = (TableRow) node.getParent();
            }
            Row rowItem = (Row) row.getItem();
            distritoSeleccionado = pais.getRegiones().get(rowItem.getColumn3());
            Map<String, Seccion> secciones = distritoSeleccionado.getChilds();
            tbSecciones.setDisable(false);

            Utils.initTableViewSeccion("Seccion", listSecciones, tableVotosSeccion);
            popularSecciones(secciones.entrySet().iterator());
            SingleSelectionModel<Tab> selectionModel = tabResultados.getSelectionModel();
            selectionModel.select(2);
        }
    }

    private void popularSecciones(Iterator<Map.Entry<String, Seccion>> iterator) {
        listSecciones.clear();
        while (iterator.hasNext()) {
            Map.Entry<String, Seccion> entry = iterator.next();
            Seccion current = entry.getValue();
            listSecciones.add(new Row(current.getDescripcion(), "" + current.getAcumulador().getCantidad(), entry.getKey()));
            LOG.info("Seccion: {}, recibio: {}", current.getDescripcion(), current.getAcumulador().getCantidad());
        }
        listSecciones.sort((o1, o2) -> {
            Integer first = Integer.parseInt(o1.getColumn2());
            Integer second = Integer.parseInt(o2.getColumn2());
            return second.compareTo(first);
        });
    }

    public void rowSeccionSelected(MouseEvent mouseEvent) {
        Node node = ((Node) mouseEvent.getTarget()).getParent();
        TableRow row;
        if (mouseEvent.getClickCount() == 2) {
            if (node instanceof TableRow) {
                row = (TableRow) node;
            } else {
                row = (TableRow) node.getParent();
            }
            Row rowItem = (Row) row.getItem();
            Seccion seccionSeleccionada = distritoSeleccionado.getChilds().get(rowItem.getColumn3());
            Map<String, Circuito> secciones = seccionSeleccionada.getChilds();
            tbCircuitos.setDisable(false);

            Utils.initTableViewSeccion("Circuitos", listCircuitos, tableVotosCircuito);
            popularCircuitos(secciones.entrySet().iterator());
            SingleSelectionModel<Tab> selectionModel = tabResultados.getSelectionModel();
            selectionModel.select(3);
        }
    }

    private void popularCircuitos(Iterator<Map.Entry<String, Circuito>> iterator) {
        listCircuitos.clear();
        while (iterator.hasNext()) {
            Map.Entry<String, Circuito> entry = iterator.next();
            Circuito current = entry.getValue();
            listCircuitos.add(new Row(current.getDescripcion(), "" + current.getAcumulador().getCantidad(), entry.getKey()));
            LOG.info("Seccion: {}, recibio: {}", current.getDescripcion(), current.getAcumulador().getCantidad());
        }
        listCircuitos.sort((o1, o2) -> {
            Integer first = Integer.parseInt(o1.getColumn2());
            Integer second = Integer.parseInt(o2.getColumn2());
            return second.compareTo(first);
        });
    }

    public void tabAgrupacionSelected() {
        try {
            lblTextHint.setVisible(false);
        } catch (Exception ignored) {
        }
    }

    public void tabRegionSelected() {
        try {
            lblTextHint.setVisible(true);
        } catch (Exception ignored) {
        }
    }
}
