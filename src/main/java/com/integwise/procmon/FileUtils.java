package com.integwise.procmon;

import java.util.*;
import java.io.*;

public class FileUtils {
    public static List<String> getFileContents(File file) throws FileNotFoundException {
        Scanner scanner;
        try {
            scanner = new Scanner(file);
            List<String> fileContents = new ArrayList<String>();

            while (scanner.hasNextLine()) {
                fileContents.add(scanner.nextLine());
            }
            scanner.close();
            return fileContents;
        } catch (FileNotFoundException e) {
            throw e;
        }
    }
}