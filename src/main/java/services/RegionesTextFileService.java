package services;

import negocio.Distrito;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
        LOG.info("Lineas: {}", count);

        LOG.info("Distritos: {}, Secciones: {}, Circuitos: {}", table.size());

        return table;
    }

    @Override
    public String toString() {
        return "RegionesTextFileService{" +
                "path='" + path + '\'' +
                '}';
    }
}
