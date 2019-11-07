package com.tsb.services.impl;

import com.tsb.negocio.Distrito;
import com.tsb.negocio.Seccion;
import com.tsb.services.RegionesTextFileService;
import com.tsb.services.strategies.RegionStrategy;
import com.tsb.services.strategies.impl.RegionCircuitoStrategy;
import com.tsb.services.strategies.impl.RegionDistritoStrategy;
import com.tsb.services.strategies.impl.RegionSeccionStrategy;
import com.tsb.soporte.TSBHashtableDA;
import com.tsb.soporte.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static com.tsb.constants.Constants.*;

@Service
public class RegionesTextFileServiceImpl implements RegionesTextFileService {

    private static final Logger LOG = LoggerFactory.getLogger(RegionesTextFileServiceImpl.class);

    private HashMap<Integer, RegionStrategy> estrategias;

    public RegionesTextFileServiceImpl() {
        estrategias = new HashMap<>();
        estrategias.put(LENGTH_DISTRITO, RegionDistritoStrategy.getInstance());
        estrategias.put(LENGTH_SECCION, RegionSeccionStrategy.getInstance());
        estrategias.put(LENGTH_CIRCUITO, RegionCircuitoStrategy.getInstance());
    }


    public Map<String, Distrito> getRegiones(String path) {
        Map<String, Distrito> table = new TSBHashtableDA<>();
        Scanner fileReader;
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

            RegionStrategy estrategia = Utils.obtenerEstrategia(campos[CODIGO_REGION], estrategias);

            if (estrategia != null) {
                estrategia.process(campos, table);
            } else {
                LOG.info("Fallo busqueda para codigo: {}", campos[CODIGO_REGION]);
            }

        }
        LOG.info("Table: {}", table);
        //validate(count, table);
        return table;
    }

    @Override
    public int[] countRegiones(Map<String, Distrito> table) {
        int[] indexCounts = new int[4];

        Collection<Distrito> distritos = table.values();
        int index = 0;
        int seccionesIndex = 0;
        int circuitosIndex = 0;
        for (Distrito distrito : distritos) {
            Collection<Seccion> secciones = distrito.getSecciones().values();
            for (Seccion seccion : secciones) {
                circuitosIndex += seccion.getCircuitos().size();
                seccionesIndex++;
            }
            index++;
        }
        indexCounts[0] = index;
        indexCounts[1] = seccionesIndex;
        indexCounts[2] = circuitosIndex;
        indexCounts[3] = 0;
        return indexCounts;
    }

    private void validate(int count, Map<String, Distrito> table) {
        LOG.info("Lineas: {}", count);
        int[] indexCounts = countRegiones(table);
        LOG.info("Distritos: {}, Secciones: {}, Circuitos: {}, Total: {}", indexCounts[0], indexCounts[1], indexCounts[2], indexCounts[0] + indexCounts[1] + indexCounts[2]);
    }
}
