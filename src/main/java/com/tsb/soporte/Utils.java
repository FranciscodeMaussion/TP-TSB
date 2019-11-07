package com.tsb.soporte;

import com.tsb.services.strategies.RegionStrategy;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class Utils {

    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    private Utils() {
        //utils class
    }

    public static RegionStrategy obtenerEstrategia(String id, HashMap<Integer, RegionStrategy> strategies) {
        int estrategiaElegida = id.length();
        LOG.info("Estrategia: {}, por valor: {}", estrategiaElegida, id);
        return strategies.get(estrategiaElegida);
    }

    public static void initTableViewSeccion(String nombre, ObservableList<Row> list, TableView<Row> table) {
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
