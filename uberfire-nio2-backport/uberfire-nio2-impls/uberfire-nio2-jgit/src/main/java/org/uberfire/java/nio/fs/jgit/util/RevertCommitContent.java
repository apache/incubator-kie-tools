package org.uberfire.java.nio.fs.jgit.util;

public class RevertCommitContent implements CommitContent {

    private final String refTree;

    public RevertCommitContent( final String refTree ) {
        this.refTree = refTree;
    }

    public String getRefTree() {
        return refTree;
    }
}
