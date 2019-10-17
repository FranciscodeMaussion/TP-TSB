package soporte;

import negocio.Acumulador;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;

import static constants.Constants.AGRUPACION;
import static constants.Constants.CATEGORIA;

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
            String campos[] = line.split("\\|");

            if(campos[CATEGORIA].compareTo("000100000000000") == 0){
                Acumulador totalizador = (Acumulador) table.get(campos[AGRUPACION]);
                if(totalizador == null){
                    //table.put(campos[AGRUPACION], )
                }

            }
        }
        return table;
    }
}
