package codeu.chat.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * This class is used to read in json objects and write json objects to a text file
 * Created by hv58535 on 6/23/17.
 */
public class Json {

    //When running on your own machine change this file path to where you are storing your textfiles.
    //It is best to save them in the project folder
    private static final String STORAGE_FILES_PATH = "/Users/hv58535/CodeU-Summer-2017/";

    public String read(String fileName) {
        try {
            String fullFilePath = STORAGE_FILES_PATH + fileName;

            File savedData = new File(fullFilePath);
            FileReader fr = new FileReader(savedData);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }

            String jsonData = sb.toString();

            System.out.println(jsonData);

            fr.close();
            br.close();

            return jsonData;
        }
        catch (Exception ex) {
            System.out.println(ex);
        }

        return "";
    }

    public void write(String fileName, String fileContents) {
        try {
            String fullFilePath = STORAGE_FILES_PATH + fileName;
            FileWriter fu = new FileWriter(fullFilePath);
            fu.write(fileContents);
            fu.close();
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
