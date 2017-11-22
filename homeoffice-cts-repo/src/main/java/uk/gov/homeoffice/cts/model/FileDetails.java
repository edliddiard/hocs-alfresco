package uk.gov.homeoffice.cts.model;

import java.io.File;

public class FileDetails {
    private File file;
    private String mimeType;
    private String name;

    public FileDetails(File file, String name, String mimeType) {
        this.file = file;
        this.name = name;
        this.mimeType = mimeType;
    }

    public File getFile() {
        return file;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getName() {
        return name;
    }
}
