package services;

import constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soporte.Acumulador;
import soporte.OAHashtable;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

import static constants.Constants.*;

public class MesasTextFileService {
    private static final Logger LOG = LoggerFactory.getLogger(MesasTextFileService.class);

    private String path;

    public MesasTextFileService(String path) {
        this.path = path;
    }

    public Map sumarPorAgrupacion() {
        Map table = new OAHashtable();
        Scanner fileReader;
        try {
            fileReader = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            LOG.error("Error loading file", e);
            return null;
        }
        fileReader.nextLine(); // Salteamos el header
        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();
            LOG.debug(line);
            String[] campos = line.split(Constants.SEPARATOR);

            if (campos[CATEGORIA_MESA].compareTo(PRESIDENTE) == 0) { // valida si es presidente
                int votos = Integer.parseInt(campos[VOTOS_MESA]);
                sumarVotos(votos, table, campos[AGRUPACION_MESA]);
                LOG.debug("Votos: {}", votos);
            }
        }
        LOG.info("Table: {}", table);
        return table;
    }

    private void sumarVotos(int votos, Map table, String id) {
        Acumulador totalizador = (Acumulador) table.get(id);
        if (totalizador == null) {
            totalizador = new Acumulador(0);
        }
        totalizador.sumar(votos);
        table.put(id, totalizador);
    }

    @Override
    public String toString() {
        return "MesasTextFileService{" +
                "path='" + path + '\'' +
                '}';
    }
}
