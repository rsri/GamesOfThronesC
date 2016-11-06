package com.gotc.util;

import java.io.*;

/**
 * Created by srikaram on 06-Nov-16.
 */
public class Util {

    public static String parseFile(File inputFile) throws IOException {
        StringWriter contentWriter = new StringWriter();
        FileReader input = new FileReader(inputFile);
        BufferedReader bufRead = new BufferedReader(input);
        String singleLine;
        while ((singleLine = bufRead.readLine()) != null) {
            contentWriter.write(singleLine);
            contentWriter.write("\n");
        }
        return contentWriter.toString();
    }
}
