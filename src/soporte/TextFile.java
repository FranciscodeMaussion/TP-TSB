package soporte;

import java.util.Scanner;

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

    public TSBHashTable sumarPorAgrupacion(){
        TSBHashTable table = new TSBHashTable();
        Scanner fileReader = new Scanner(path);
        while (fileReader.hasNextLine()){
            String line = fileReader.nextLine();
            System.out.println(line);
        }
        return table;
    }
}
