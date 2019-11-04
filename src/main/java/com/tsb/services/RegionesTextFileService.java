package com.tsb.services;

import com.tsb.negocio.Distrito;

import java.util.Map;

public interface RegionesTextFileService {

    Map<String, Distrito> getRegiones(String path);

}
