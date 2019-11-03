package services;

import negocio.Circuito;
import negocio.Distrito;
import negocio.Seccion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.strategies.RegionStrategy;
import services.strategies.impl.RegionCircuitoStrategy;
import services.strategies.impl.RegionDistritoStrategy;
import services.strategies.impl.RegionSeccionStrategy;
import soporte.TSBHashtableDA;
import soporte.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static constants.Constants.*;

public class RegionesTextFileService {

    private static final Logger LOG = LoggerFactory.getLogger(RegionesTextFileService.class);
    private String path;
    private HashMap<Integer, RegionStrategy> strategies;

    public RegionesTextFileService(String path) {
        this.path = path;
        strategies = new HashMap<>();
        strategies.put(LENGTH_DISTRITO, RegionDistritoStrategy.getInstance());
        strategies.put(LENGTH_SECCION, RegionSeccionStrategy.getInstance());
        strategies.put(LENGTH_CIRCUITO, RegionCircuitoStrategy.getInstance());
    }


    public Map getRegiones() {
        Map<Integer, Distrito> table = new TSBHashtableDA<>();
        Scanner fileReader;
        int count = 0;
        try {
            fileReader = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            LOG.error("No se puede leer el archivo", e);
            return null;
        }
        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();
            LOG.debug(line);
            String[] campos = line.split(SEPARATOR);

            RegionStrategy estrategia = Utils.obtenerEstrategia(campos[CODIGO_REGION], strategies);

            if (estrategia != null) {
                count++;
                estrategia.process(campos, table);
            } else {
                LOG.info("Fallo busqueda para codigo: {}", campos[CODIGO_REGION]);
            }

        }
        LOG.info("Table: {}", table);
        validate(count, table);
        return table;
    }

    public void validate(int count, Map table) {
        LOG.info("Lineas: {}", count);

        Collection<Distrito> distritos = table.values();
        Iterator<Distrito> iterator = distritos.iterator();
        int index = 0;
        int seccionesIndex = 0;
        int circuitosIndex = 0;
        while (iterator.hasNext()) {
            Distrito distrito = iterator.next();
            Collection<Seccion> secciones = distrito.getChilds().values();
            Iterator<Seccion> seccionesIterator = secciones.iterator();
            while (seccionesIterator.hasNext()) {
                Seccion seccion = seccionesIterator.next();
                Collection<Circuito> circuitos = seccion.getChilds().values();
                Iterator<Circuito> circuitosIterator = circuitos.iterator();
                while (circuitosIterator.hasNext()) {
                    Circuito circuito = circuitosIterator.next();
                    circuitosIndex++;
                }
                seccionesIndex++;
            }
            index++;
        }

        LOG.info("Distritos: {}, Secciones: {}, Circuitos: {}, Total: {}", index, seccionesIndex, circuitosIndex, index + seccionesIndex + circuitosIndex);

    }

    @Override
    public String toString() {
        return "RegionesTextFileService{" +
                "path='" + path + '\'' +
                '}';
    }
}
