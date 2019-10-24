package services;

import negocio.Agrupacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;

import static constants.Constants.*;

public class PostulacionesTextFileService {

    private static final Logger LOG = LoggerFactory.getLogger(PostulacionesTextFileService.class);

    private String path;

    public PostulacionesTextFileService(String path) {
        this.path = path;
    }

    public Hashtable getPostulaciones() {
        Hashtable table = new Hashtable();
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

    @Override
    public String toString() {
        return "PostulacionesTextFileService{" +
                "path='" + path + '\'' +
                '}';
    }
}
