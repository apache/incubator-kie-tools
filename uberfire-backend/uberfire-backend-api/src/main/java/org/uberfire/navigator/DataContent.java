package org.uberfire.navigator;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class DataContent {

    private boolean isDirectory;
    private String lastMessage;
    private String lastCommiter;
    private String lastCommiterEmail;
    private String age;
    private Path path;

    public DataContent() {
    }

    public DataContent( boolean isDirectory,
                        String lastMessage,
                        String lastCommiter,
                        String lastCommiterEmail,
                        String age,
                        Path path ) {
        this.isDirectory = isDirectory;
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
