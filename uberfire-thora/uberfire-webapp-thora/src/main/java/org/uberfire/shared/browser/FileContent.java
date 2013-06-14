package org.uberfire.shared.browser;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class FileContent {

    private boolean isDirectory;
    private String lastMessage;
    private String lastCommiter;
    private String lastCommiterEmail;
    private String age;
    private Path path;

    public FileContent() {
    }

    public FileContent( boolean directory,
                        String lastMessage,
                        String lastCommiter,
                        String lastCommiterEmail,
                        String age,
                        Path path ) {
        isDirectory = directory;
        this.lastMessage = lastMessage;
        this.lastCommiter = lastCommiter;
        this.lastCommiterEmail = lastCommiterEmail;
        this.age = age;
        this.path = path;
    }

    public String getLastCommiterEmail() {
        return lastCommiterEmail;
    }

    public String getAge() {
        return age;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastCommiter() {
        return lastCommiter;
    }

    public Path getPath() {
        return path;
    }
}
