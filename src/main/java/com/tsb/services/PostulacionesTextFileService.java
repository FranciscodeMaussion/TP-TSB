package com.tsb.services;

import com.tsb.negocio.Agrupacion;

import java.util.Map;

public interface PostulacionesTextFileService {

    Map<String, Agrupacion> getPostulaciones(String path);

}
