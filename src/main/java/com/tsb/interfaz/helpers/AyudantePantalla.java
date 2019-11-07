package com.tsb.interfaz.helpers;

import com.tsb.negocio.*;
import com.tsb.soporte.Row;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

@Service
public class AyudantePantalla {

    private static final Logger LOG = LoggerFactory.getLogger(AyudantePantalla.class);

    public void popularSelectorDistritos(ObservableList<Row> listaDistrito, Pais pais, ChoiceBox selDistrito) {
        for (Map.Entry<String, Distrito> current : pais.getRegiones().entrySet()) {
            Row row = new Row(current.getValue().getDescripcion(), "", current.getKey());
            listaDistrito.add(row);
        }
        selDistrito.getSelectionModel().select(0);
    }

    public void popularSelectorSecciones(ObservableList<Row> listaSeccion, Distrito distrito) {
        for (Map.Entry<String, Seccion> current : distrito.getSecciones().entrySet()) {
            Row row = new Row(current.getValue().getDescripcion(), "", current.getKey());
            listaSeccion.add(row);
        }
    }

    public void popularSelectorCircuitos(ObservableList<Row> listaCircuito, Seccion seccion) {
        for (Map.Entry<String, Circuito> current : seccion.getCircuitos().entrySet()) {
            Row row = new Row(current.getValue().getDescripcion(), "", current.getKey());
            listaCircuito.add(row);
        }
    }

    public void popularSelectorMesas(ObservableList<Row> listaMesa, Circuito circuito) {
        for (Map.Entry<String, Mesa> current : circuito.getMesas().entrySet()) {
            Row row = new Row(current.getKey(), "", current.getKey());
            listaMesa.add(row);
        }
    }

    public void popularAgrupaciones(ObservableList<Row> listaAgrupaciones, Pais pais) {
        listaAgrupaciones.clear();
        Iterator<Map.Entry<String, Agrupacion>> iterator = pais.mostrarResultadosXAgrupacion();
        while (iterator.hasNext()) {
            Map.Entry<String, Agrupacion> entry = iterator.next();
            Agrupacion current = entry.getValue();
            listaAgrupaciones.add(new Row(current.getNombreAgrupacion(), "" + current.getVotos(), entry.getKey()));
            LOG.debug("Agrupacion: {}, recibio: {}", current.getNombreAgrupacion(), current.getVotos());
        }
        listaAgrupaciones.sort((o1, o2) -> {
            Integer first = Integer.parseInt(o1.getVotos());
            Integer second = Integer.parseInt(o2.getVotos());
            return second.compareTo(first);
        });
    }

    public static void inicializarTableView(String nombre, ObservableList<Row> list, TableView<Row> table) {
        table.setItems(list);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Row, String> nameColumn = new TableColumn<>(nombre);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        TableColumn<Row, String> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Row, String> votosColumn = new TableColumn<>("Votos");
        votosColumn.setCellValueFactory(new PropertyValueFactory<>("votos"));
        table.getColumns().clear();
        table.getColumns().addAll(idColumn, nameColumn, votosColumn);
    }

}
