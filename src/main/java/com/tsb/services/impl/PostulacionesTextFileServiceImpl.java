package com.tsb.services.impl;

import com.tsb.negocio.Agrupacion;
import com.tsb.services.PostulacionesTextFileService;
import com.tsb.soporte.TSBHashtableDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

import static com.tsb.constants.Constants.*;

@Service
public class PostulacionesTextFileServiceImpl implements PostulacionesTextFileService {

    private static final Logger LOG = LoggerFactory.getLogger(PostulacionesTextFileServiceImpl.class);

    public Map<String, Agrupacion> getPostulaciones(String path) {
        Map<String, Agrupacion> table = new TSBHashtableDA<>();
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

            if (campos[CATEGORIA_POSTULACIONES].compareTo(PRESIDENTE) == 0) { // valida si es presidente
                Agrupacion agrupacion = new Agrupacion(
                        campos[CATEGORIA_POSTULACIONES],
                        Integer.parseInt(campos[CODIGO_AGRUPACION_POSTULACIONES]),
                        campos[NOMBRE_AGRUPACION_POSTULACIONES]);
                table.put(campos[CODIGO_AGRUPACION_POSTULACIONES], agrupacion);
            }
        }
        LOG.info(table.toString());
        return table;
    }
}
