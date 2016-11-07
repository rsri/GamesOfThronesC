package com.gotc.util;

import com.gotc.nodes.GOTNode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

    public static String getBaseName(String filename) {
        int index = filename.lastIndexOf(File.pathSeparator);
        String name = filename.substring(index + 1);
        return removeExtension(name);
    }

    private static String removeExtension(String filename) {
        int index = filename.lastIndexOf(".");
        return index == -1 ? filename : filename.substring(0, index);
    }

    public static String capitalize(String str) {
        return String.valueOf(Character.toTitleCase(str.charAt(0))) +
                str.substring(1);
    }
}
