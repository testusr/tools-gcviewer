package com.smeo.tools.gc.newparser;

import java.io.*;

/**
 * Created by joachim on 25.12.13.
 */
public class VeryNewGcParser {
    public static void main(String[] args) {
        AllEventsParser allEventsParser = new AllEventsParser();
        String filename = args[0];
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(filename)));

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    allEventsParser.parseLine(line);
                }
                System.out.println("done");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
