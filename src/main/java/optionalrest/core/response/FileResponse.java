package optionalrest.core.response;

import optionalrest.core.RestException;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

public class FileResponse extends BasicResponse {

    private File file;
    private boolean senseType = true;

    public FileResponse() {
    }

    public FileResponse(File file) {
        this.file = file;
    }

    public FileResponse(File file, boolean senseType) {
        this.file = file;
        this.senseType = senseType;
    }

    public FileResponse file(File file) {
        this.file = file;
        if (senseType) {
            try {
                String fileType = Files.probeContentType(file.toPath());
                fileType(fileType);
            } catch (IOException e) {
                throw new RestException("Encountered error while reading file type of " + file);
            }
        }
        return this;
    }

    public FileResponse file(String filename) {
        return file(new File(filename));
    }

    public FileResponse resource(String filename, Class from) {
        try {
            URL resource = from.getResource(filename);
            if (resource == null) {
                throw new RestException("Could not find resource " + filename + " from " + from);
            }
            URI uri = resource.toURI();
            file(new File(uri));
        } catch (URISyntaxException e) {
            throw new RestException("Encountered error while loading resource file: " + filename, e);
        }
        return this;
    }

    public FileResponse resource(String filename) {
        return resource(filename, FileResponse.class);
    }

    public FileResponse fileType(String fileType) {
        getHeaders().add("Content-type", fileType);
        senseType(false);
        return this;
    }

    public FileResponse senseType(boolean senseType) {
        this.senseType = senseType;
        return this;
    }

    @Override
    public InputStream toStream() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RestException("Encountered error returning file as stream: " + file, e);
        }
    }
}
