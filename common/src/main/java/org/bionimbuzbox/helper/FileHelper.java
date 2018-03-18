package org.bionimbuzbox.helper;

import java.io.File;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHelper {
  
  private static final Logger log = LoggerFactory.getLogger(FileHelper.class);

  public static String readFile(String pathname) {
    String contents = null;
    File file = new File(pathname);
    
    try (Scanner scanner = new Scanner(file);) {
      StringBuilder fileContents = new StringBuilder((int)file.length());
      
      String lineSeparator = System.getProperty("line.separator");
      while(scanner.hasNextLine()) {
        fileContents.append(scanner.nextLine() + lineSeparator);
      }
      contents = fileContents.toString();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    } 
    return contents;
  }
}
