package com.tsb.services.impl;

import com.tsb.negocio.Circuito;
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
import java.util.*;

import static com.tsb.constants.Constants.*;

@Service
public class RegionesTextFileServiceImpl implements RegionesTextFileService {

    private static final Logger LOG = LoggerFactory.getLogger(RegionesTextFileServiceImpl.class);

    private HashMap<Integer, RegionStrategy> strategies;

    public RegionesTextFileServiceImpl() {
        strategies = new HashMap<>();
        strategies.put(LENGTH_DISTRITO, RegionDistritoStrategy.getInstance());
        strategies.put(LENGTH_SECCION, RegionSeccionStrategy.getInstance());
        strategies.put(LENGTH_CIRCUITO, RegionCircuitoStrategy.getInstance());
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

            RegionStrategy estrategia = Utils.obtenerEstrategia(campos[CODIGO_REGION], strategies);

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

    //TODO necesitamos esto?
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
                    circuitosIndex++;
                }
                seccionesIndex++;
            }
            index++;
        }

        LOG.info("Distritos: {}, Secciones: {}, Circuitos: {}, Total: {}", index, seccionesIndex, circuitosIndex, index + seccionesIndex + circuitosIndex);

    }
}
