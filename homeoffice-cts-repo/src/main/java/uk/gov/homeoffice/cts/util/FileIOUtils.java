package uk.gov.homeoffice.cts.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileIOUtils {
    public static void writeToFile(InputStream is, File f) throws IOException {
        FileOutputStream fos = new FileOutputStream(f);
        byte[] buf = new byte[4096];
        int bytesRead;
        while ((bytesRead = is.read(buf)) != -1) {
            fos.write(buf, 0, bytesRead);
        }
        fos.close();
    }
}
