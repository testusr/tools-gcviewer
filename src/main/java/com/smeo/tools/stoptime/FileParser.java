package com.smeo.tools.stoptime;

import java.io.*;

public abstract class FileParser {

    public boolean parseFile(String filename){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(filename)));

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    if (!parseLine(line)){
                        break;
                    };
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println("file '"+filename+" could not be found");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param line
     * @return false if no more lines should be parsed / parser should stop
     */
    abstract boolean parseLine(String line);
}
