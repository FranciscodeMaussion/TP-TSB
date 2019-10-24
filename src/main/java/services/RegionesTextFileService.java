package services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.strategies.RegionStrategy;
import services.strategies.impl.RegionCircuitoStrategy;
import services.strategies.impl.RegionDistritoStrategy;
import services.strategies.impl.RegionSeccionStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Scanner;

import static constants.Constants.CODIGO_REGION;
import static constants.Constants.SEPARATOR;

public class RegionesTextFileService {

    private static final Logger LOG = LoggerFactory.getLogger(RegionesTextFileService.class);
    private String path;
    private HashMap<Integer, RegionStrategy> strategies;

    public RegionesTextFileService(String path) {
        this.path = path;
        strategies = new HashMap<>();
        strategies.put(2, RegionDistritoStrategy.getInstance());
        strategies.put(5, RegionSeccionStrategy.getInstance());
        strategies.put(11, RegionCircuitoStrategy.getInstance());
    }


    public Hashtable getRegiones() {
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

            strategies.get(campos[CODIGO_REGION].length()).process(campos, table);
        }
        LOG.info("Table: {}", table);
        return table;
    }

    @Override
    public String toString() {
        return "RegionesTextFileService{" +
                "path='" + path + '\'' +
                '}';
    }
}
