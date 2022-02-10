package hw4;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class Helpers {
    public static String encodeImageToBase64(File file) {
        byte[] byteArr = new byte[0];
        try {
            byteArr = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(byteArr);
    }
}
