package team.JavaTeens.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import team.JavaTeens.Account.Account;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static File findFile(String filename, File directory) throws FileNotFoundException {

        File[] list = directory.listFiles();
        if (list != null) {
            for (File fil : list) {
                if (fil.isDirectory()) {
                    findFile(filename, fil);
                } else if (filename.equalsIgnoreCase(fil.getName())) {
                    return fil;
                }
            }
        }
        throw new FileNotFoundException("File does not exist");
    }

    public static Object parseJson(String content, Class classType) throws JsonProcessingException { // JSON -> Object
        return new ObjectMapper().readValue(content, classType);
    }

    public static void parseToJson(File file, Object content) throws IOException { // Object -> Json
        new ObjectMapper().writeValue(file, content);
    }

    public static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }
}
