package com.tsb.soporte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tsb.services.strategies.RegionStrategy;

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
}
