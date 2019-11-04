package interfaz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import negocio.Agrupacion;
import negocio.Distrito;
import negocio.Pais;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soporte.Row;

import java.io.File;
import java.util.Iterator;
import java.util.Map;


public class Controller {

    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    public Button btnCargarAgrupaciones;
    public Button btnCargarRegiones;
    public Button btnCargarVotos;
    public Label lblPath;
    public Label lblAgrupaciones;
    public Label lblRegiones;
    public TabPane tabResultados;
    public TableView<Row> tableVotosAgrupacion;
    public TableView<Row> tableVotosDistrito;

    private Pais pais;
    private ObservableList<Row> listAgrupacion;
    private ObservableList<Row> listDistritos;

    public void cargarAgrupaciones(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            String path = file.getPath();
            lblPath.setText(path);
            pais = new Pais(path);
            pais.cargarDescripcionPostulaciones();
            btnCargarAgrupaciones.setDisable(true);
            btnCargarRegiones.setDisable(false);
            lblAgrupaciones.setText("" + pais.getAgrupacionesCargadas());
        }
        initTableViewAgrupacion();
        popularAgrupaciones();
    }

    private void initTableViewAgrupacion(){
        listAgrupacion = FXCollections.observableArrayList();
        tableVotosAgrupacion.setItems(listAgrupacion);
        tableVotosAgrupacion.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Row, String> nameColumn = new TableColumn<>("Agrupacion");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("column1"));

        TableColumn<Row, String> votosColumn = new TableColumn<>("Votos");
        votosColumn.setCellValueFactory(new PropertyValueFactory<>("column2"));
        tableVotosAgrupacion.getColumns().clear();
        tableVotosAgrupacion.getColumns().addAll(nameColumn, votosColumn);
    }



    private void popularAgrupaciones() {
        listAgrupacion.clear();
        Iterator<Map.Entry<String, Agrupacion>> iterator = pais.mostrarResultadosXAgrupacion();
        while (iterator.hasNext()) {
            Map.Entry<String, Agrupacion> entry = iterator.next();
            Agrupacion current = entry.getValue();
            listAgrupacion.add(new Row(current.getNombreAgrupacion(), "" + current.getAcumulador().getCantidad()));
            LOG.info("Agrupacion: {}, recibio: {}", current.getNombreAgrupacion(), current.getAcumulador().getCantidad());
        }
        listAgrupacion.sort((o1, o2) -> {
            Integer first = Integer.parseInt(o1.getColumn2());
            Integer second = Integer.parseInt(o2.getColumn2());
            return second.compareTo(first);
        });
    }

    public void cargarRegiones(ActionEvent actionEvent) {
        pais.cargarDescripcionRegiones();
        lblRegiones.setText("" + pais.getRegionesCargadas());
        btnCargarRegiones.setDisable(true);
        btnCargarVotos.setDisable(false);
        initTableViewDistrito();
        popularDistritos();
    }

    private void initTableViewDistrito(){
        listDistritos = FXCollections.observableArrayList();
        tableVotosDistrito.setItems(listDistritos);
        tableVotosDistrito.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Row, String> nameColumn = new TableColumn<>("Distrito");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("column1"));

        TableColumn<Row, String> votosColumn = new TableColumn<>("Votos");
        votosColumn.setCellValueFactory(new PropertyValueFactory<>("column2"));
        tableVotosDistrito.getColumns().clear();
        tableVotosDistrito.getColumns().addAll(nameColumn, votosColumn);
    }

    private void popularDistritos() {
        listDistritos.clear();
        Iterator<Map.Entry<String, Distrito>> iterator = pais.mostrarResultadosXDistrito();
        while (iterator.hasNext()) {
            Map.Entry<String, Distrito> entry = iterator.next();
            Distrito current = entry.getValue();
            listDistritos.add(new Row(current.getDescripcion(), "" + current.getAcumulador().getCantidad()));
            LOG.info("Distrito: {}, recibio: {}", current.getDescripcion(), current.getAcumulador().getCantidad());
        }
        listDistritos.sort((o1, o2) -> {
            Integer first = Integer.parseInt(o1.getColumn2());
            Integer second = Integer.parseInt(o2.getColumn2());
            return second.compareTo(first);
        });
    }

    public void cargarVotos(ActionEvent actionEvent) {
        pais.cargarResultados();
        btnCargarVotos.setDisable(true);
        popularAgrupaciones();
        popularDistritos();

    }
}
