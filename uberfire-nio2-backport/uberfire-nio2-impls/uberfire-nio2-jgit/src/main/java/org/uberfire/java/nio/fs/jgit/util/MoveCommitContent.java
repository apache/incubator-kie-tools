package org.uberfire.java.nio.fs.jgit.util;

import java.util.Map;

public class MoveCommitContent implements CommitContent {

    private final Map<String, String> content;

    public MoveCommitContent( Map<String, String> content ) {
        this.content = content;
    }

    public Map<String, String> getContent() {
        return content;
    }
}
