package com.tsb.services.impl;

import com.tsb.constants.Constants;
import com.tsb.negocio.*;
import com.tsb.services.MesasTextFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

import static com.tsb.constants.Constants.*;

@Service
public class MesasTextFileServiceImpl implements MesasTextFileService {
    private static final Logger LOG = LoggerFactory.getLogger(MesasTextFileServiceImpl.class);

    public int sumarVotos(String path, Map<String, Distrito> regiones, Map<String, Agrupacion> postulaciones) {
        int mesasCount = 0;
        Scanner fileReader;
        try {
            fileReader = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            LOG.error("Error loading file", e);
            return 0;
        }
        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();
            LOG.debug(line);
            String[] campos = line.split(Constants.SEPARATOR);

            if (campos[CATEGORIA_MESA].compareTo(PRESIDENTE) == 0) { // valida si es presidente
                int votos = Integer.parseInt(campos[VOTOS_MESA]);

                // Suma por agrupacion
                Agrupacion agrupacion = postulaciones.get(campos[AGRUPACION_MESA]);
                if (agrupacion == null) {
                    agrupacion = new Agrupacion(campos[CATEGORIA_MESA], Integer.parseInt(campos[AGRUPACION_MESA]), DEFAULT_NAME);
                }
                agrupacion.sumarVotos(votos);
                postulaciones.put(campos[AGRUPACION_MESA], agrupacion);

                // Suma por Distrito, Seccion, Circuito
                String distritoCode = campos[CIRCUITO_MESA].substring(0, LENGTH_DISTRITO);
                Distrito distrito = regiones.get(distritoCode);
                if (distrito == null) {
                    distrito = new Distrito(distritoCode, DEFAULT_NAME);
                }
                distrito.sumarVotosAgrupacion(votos, campos[AGRUPACION_MESA]);

                String seccionCode = campos[CIRCUITO_MESA].substring(LENGTH_DISTRITO, LENGTH_SECCION);
                Map<String, Seccion> secciones = distrito.getSecciones();
                Seccion seccion = secciones.get(seccionCode);
                if (seccion == null) {
                    seccion = new Seccion(seccionCode, DEFAULT_NAME);
                }
                seccion.sumarVotosAgrupacion(votos, campos[AGRUPACION_MESA]);

                String circuitoCode = campos[CIRCUITO_MESA].substring(LENGTH_SECCION);
                Map<String, Circuito> circuitos = seccion.getCircuitos();
                Circuito circuito = circuitos.get(circuitoCode);
                if (circuito == null) {
                    circuito = new Circuito(circuitoCode, DEFAULT_NAME);
                }
                circuito.sumarVotosAgrupacion(votos, campos[AGRUPACION_MESA]);

                // Suma por mesa
                Map<String, Mesa> mesasTable = circuito.getMesas();
                Mesa mesa = mesasTable.get(campos[MESA]);
                if (mesa == null) {
                    mesa = new Mesa(campos[MESA]);
                }
                mesa.sumarVotosAgrupacion(votos, campos[AGRUPACION_MESA]);
                mesasCount++;

                mesasTable.put(campos[MESA], mesa);
                circuitos.put(circuitoCode, circuito);
                secciones.put(seccionCode, seccion);
                regiones.put(distritoCode, distrito);

                LOG.debug("Votos: {}", votos);
            }
        }
        LOG.info("Postulaciones: {}", postulaciones);
        LOG.info("Distritos: {}", regiones);
        return mesasCount;
    }

}
