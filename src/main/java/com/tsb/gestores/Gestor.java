package com.tsb.gestores;

import com.tsb.constants.Constants;
import com.tsb.negocio.Agrupacion;
import com.tsb.negocio.Distrito;
import com.tsb.negocio.Pais;
import com.tsb.services.MesasTextFileService;
import com.tsb.services.PostulacionesTextFileService;
import com.tsb.services.RegionesTextFileService;
import com.tsb.soporte.Acumulador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Gestor {

    private Map<String, Agrupacion> postulaciones;
    private Map<String, Distrito> regiones;

    private MesasTextFileService mesasTextFileService;
    private PostulacionesTextFileService postulacionesTextFileService;
    private RegionesTextFileService regionesTextFileService;

    @Autowired
    public Gestor(MesasTextFileService mesasTextFileService, PostulacionesTextFileService postulacionesTextFileService, RegionesTextFileService regionesTextFileService) {
        this.mesasTextFileService = mesasTextFileService;
        this.postulacionesTextFileService = postulacionesTextFileService;
        this.regionesTextFileService = regionesTextFileService;
    }

    public void cargarDescripcionPostulaciones(Pais pais, String path) {
        postulaciones = postulacionesTextFileService.getPostulaciones(path + Constants.FILE_POSTULACIONES);
        pais.setPostulaciones(postulaciones);
    }

    public void cargarDescripcionRegiones(Pais pais, String path) {
        regiones = regionesTextFileService.getRegiones(path + Constants.FILE_REGIONES);
        pais.setRegiones(regiones);
        pais.setRegionesCount(regionesTextFileService.countRegiones(regiones));
    }

    public void cargarResultados(Pais pais, String path) {
        Map<String, Acumulador> resultados = mesasTextFileService.sumarVotos(path + Constants.FILE_MESAS, regiones, postulaciones);
        pais.setResultados(resultados);
    }
}
