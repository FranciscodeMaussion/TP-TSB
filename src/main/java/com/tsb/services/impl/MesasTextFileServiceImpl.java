package com.tsb.services.impl;

import com.tsb.constants.Constants;
import com.tsb.negocio.Agrupacion;
import com.tsb.negocio.Circuito;
import com.tsb.negocio.Distrito;
import com.tsb.negocio.Seccion;
import com.tsb.services.MesasTextFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tsb.soporte.Acumulador;
import com.tsb.soporte.TSBHashtableDA;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

import static com.tsb.constants.Constants.*;

@Service
public class MesasTextFileServiceImpl implements MesasTextFileService {
    private static final Logger LOG = LoggerFactory.getLogger(MesasTextFileServiceImpl.class);

    public Map<String, Acumulador> sumarVotos(String path, Map<String, Distrito> regiones, Map<String, Agrupacion> postulaciones) {
        Map<String, Acumulador> mesasTable = new TSBHashtableDA<>();

        Scanner fileReader;
        try {
            fileReader = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            LOG.error("Error loading file", e);
            return null;
        }
        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();
            LOG.debug(line);
            String[] campos = line.split(Constants.SEPARATOR);

            if (campos[CATEGORIA_MESA].compareTo(PRESIDENTE) == 0) { // valida si es presidente
                int votos = Integer.parseInt(campos[VOTOS_MESA]);

                // Suma por mesa
                // TODO considerar crear mesa como un subobjeto de circuito
                Acumulador mesa = mesasTable.get(campos[MESA]);
                if(mesa == null){
                    mesa = new Acumulador(0);
                }
                mesa.sumar(votos);
                mesasTable.put(campos[MESA], mesa);

                // Suma por agrupacion
                // TODO considerar votable como generalizacion de estos elementos
                Agrupacion agrupacion = postulaciones.get(campos[AGRUPACION_MESA]);
                if(agrupacion == null){
                    agrupacion = new Agrupacion(campos[CATEGORIA_MESA], Integer.parseInt(campos[AGRUPACION_MESA]), DEFAULT_NAME);
                }
                agrupacion.getAcumulador().sumar(votos);
                postulaciones.put(campos[AGRUPACION_MESA], agrupacion);

                // Suma por Distrito, Seccion, Circuito
                String distritoCode = campos[CIRCUITO_MESA].substring(0, LENGTH_DISTRITO);
                Distrito distrito = regiones.get(distritoCode);
                if(distrito == null){
                    distrito = new Distrito(distritoCode, DEFAULT_NAME);
                }
                distrito.getAcumulador().sumar(votos);

                String seccionCode = campos[CIRCUITO_MESA].substring(LENGTH_DISTRITO, LENGTH_SECCION);
                Map<String, Seccion> secciones = distrito.getChilds();
                Seccion seccion = secciones.get(seccionCode);
                if(seccion == null){
                    seccion = new Seccion(seccionCode, DEFAULT_NAME);
                }
                seccion.getAcumulador().sumar(votos);

                String circuitoCode = campos[CIRCUITO_MESA].substring(LENGTH_SECCION);
                Map<String, Circuito> circuitos = seccion.getChilds();
                Circuito circuito = circuitos.get(circuitoCode);
                if(circuito == null){
                    circuito = new Circuito(circuitoCode, DEFAULT_NAME);
                }
                circuito.getAcumulador().sumar(votos);

                circuitos.put(circuitoCode, circuito);
                secciones.put(seccionCode, seccion);
                regiones.put(distritoCode, distrito);

                LOG.debug("Votos: {}", votos);
            }
        }
        LOG.info("Mesas: {}", mesasTable);
        LOG.info("Postulaciones: {}", postulaciones);
        LOG.info("Distritos: {}", regiones);
        return mesasTable;
    }
}
