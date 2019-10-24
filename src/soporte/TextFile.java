package soporte;

import negocio.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;

import static constants.Constants.*;

public class TextFile {
    private String path;

    public TextFile(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "TextFile{" +
                "path='" + path + '\'' +
                '}';
    }

    public Hashtable sumarPorAgrupacion(){
        Hashtable table = new Hashtable();
        Scanner fileReader = null;
        try {
            fileReader = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        while (fileReader.hasNextLine()){
            String line = fileReader.nextLine();
            //System.out.println(line);
            String[] campos = line.split("\\|");

            if(campos[CATEGORIA_MESA].compareTo(PRESIDENTE) == 0){ // valida si es presidente
                Acumulador totalizador = (Acumulador) table.get(campos[AGRUPACION_MESA]);
                int votos = Integer.parseInt(campos[VOTOS_MESA]);
                if(totalizador == null) {
                    totalizador = new Acumulador(0);
                }
                totalizador.sumar(votos);
                table.put(campos[AGRUPACION_MESA], totalizador);
                //System.out.println(votos);
            }
        }
        System.out.println(table.toString());
        return table;
    }

    public Hashtable getPostulaciones() {
        Hashtable table = new Hashtable();
        Scanner fileReader = null;
        try {
            fileReader = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        while (fileReader.hasNextLine()){
            String line = fileReader.nextLine();
            //System.out.println(line);
            String[] campos = line.split("\\|");

            if(campos[CATEGORIA_POSTULACIONES].compareTo(PRESIDENTE) == 0){ // valida si es presidente
                Agrupacion agrupacion = new Agrupacion(
                        campos[CATEGORIA_POSTULACIONES],
                        Integer.parseInt(campos[CODIGO_AGRUPACION_POSTULACIONES]),
                        campos[NOMBRE_AGRUPACION_POSTULACIONES]);
                table.put(campos[CODIGO_AGRUPACION_POSTULACIONES], agrupacion);
            }
        }
        System.out.println(table.toString());
        return table;
    }

    public Hashtable getRegiones() {
        Hashtable table = new Hashtable();
        Scanner fileReader = null;
        try {
            fileReader = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        while (fileReader.hasNextLine()){
            String line = fileReader.nextLine();
            //System.out.println(line);
            String[] campos = line.split("\\|");
            Distrito distrito;
            Seccion seccion;
            Circuito circuito;
            String distritoCode;
            String seccionCode;
            Hashtable distritoTable;
            // TODO No se como mierda achicar esto, hagamos un strategy y un composite
            switch (campos[CODIGO_REGION].length()){
                case 2:
                    distrito = (Distrito) table.get(campos[CODIGO_REGION]);
                    if (distrito == null) {
                        distrito = new Distrito(campos[CODIGO_REGION], campos[NOMBRE_REGION]);
                        table.put(campos[CODIGO_REGION], distrito);
                    }else{
                        distrito.setDescripcion(campos[NOMBRE_REGION]);
                    }
                    break;
                case 5:
                    distritoCode = campos[CODIGO_REGION].substring(0, 1);
                    distrito = (Distrito) table.get(distritoCode);
                    if (distrito == null){
                        distrito = new Distrito(distritoCode, DEFAULT_NAME);
                        table.put(campos[CODIGO_REGION], distrito);
                    }
                    seccion = new Seccion(campos[CODIGO_REGION].substring(2), campos[NOMBRE_REGION]);
                    distritoTable = distrito.getSecciones();
                    distritoTable.put(campos[CODIGO_REGION].substring(2), seccion);
                    distrito.setSecciones(distritoTable);
                    break;
                case 11:
                    distritoCode = campos[CODIGO_REGION].substring(0, 1);
                    seccionCode = campos[CODIGO_REGION].substring(2, 4);
                    distrito = (Distrito) table.get(distritoCode);
                    if (distrito == null){
                        distrito = new Distrito(distritoCode, DEFAULT_NAME);
                        table.put(campos[CODIGO_REGION], distrito);
                        seccion = null;
                    }else{
                        seccion = (Seccion) table.get(seccionCode);
                    }
                    if(seccion == null){
                        seccion = new Seccion(seccionCode, DEFAULT_NAME);
                    }
                    circuito = new Circuito(campos[CODIGO_REGION].substring(5), campos[NOMBRE_REGION]);

                    Hashtable circuitoTable = seccion.getCircuitos();
                    circuitoTable.put(campos[CODIGO_REGION].substring(5), circuito);
                    seccion.setCircuitos(circuitoTable);

                    distritoTable = distrito.getSecciones();
                    distritoTable.put(seccionCode, seccion);
                    distrito.setSecciones(distritoTable);
                    break;
            }
        }
        System.out.println(table.toString());
        return table;
    }

}
