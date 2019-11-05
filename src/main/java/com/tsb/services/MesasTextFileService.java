package com.tsb.services;

import com.tsb.negocio.Agrupacion;
import com.tsb.negocio.Distrito;

import java.util.Map;

public interface MesasTextFileService {

    int sumarVotos(String path, Map<String, Distrito> regiones, Map<String, Agrupacion> postulaciones);

}
