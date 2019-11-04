package com.tsb.services;

import com.tsb.negocio.Agrupacion;
import com.tsb.negocio.Distrito;
import com.tsb.soporte.Acumulador;

import java.util.Map;

public interface MesasTextFileService {

    Map<String, Acumulador> sumarVotos(String path, Map<String, Distrito> regiones, Map<String, Agrupacion> postulaciones);

}
