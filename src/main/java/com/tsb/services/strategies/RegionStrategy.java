package com.tsb.services.strategies;

import com.tsb.negocio.Distrito;

import java.util.Map;

public interface RegionStrategy {

    void process(String[] campos, Map<String, Distrito> table);

}
