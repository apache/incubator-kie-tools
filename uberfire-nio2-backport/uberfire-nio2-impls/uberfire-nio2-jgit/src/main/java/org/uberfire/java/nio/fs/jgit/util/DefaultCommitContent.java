package org.uberfire.java.nio.fs.jgit.util;

import java.io.File;
import java.util.Map;

public class DefaultCommitContent implements CommitContent {

    private final Map<String, File> content;

    public DefaultCommitContent( Map<String, File> content ) {
        this.content = content;
    }

    public Map<String, File> getContent() {
        return content;
    }
}
