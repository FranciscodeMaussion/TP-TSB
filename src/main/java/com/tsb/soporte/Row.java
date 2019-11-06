package com.tsb.soporte;

import org.springframework.util.StringUtils;

public class Row {
    private String id;
    private String descripcion;
    private String votos;

    public Row(String descripcion, String votos, String id) {
        this.descripcion = descripcion;
        this.votos = votos;
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getVotos() {
        return votos;
    }

    public void setVotos(String votos) {
        this.votos = votos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        String result = descripcion;
        if (StringUtils.hasText(votos)) {
            result += ", " + votos;
        }
        return result;
    }
}
