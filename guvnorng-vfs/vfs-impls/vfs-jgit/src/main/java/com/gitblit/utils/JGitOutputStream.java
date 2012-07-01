package com.gitblit.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;
import org.drools.java.nio.fs.file.JGitFileSystemProvider;

public class JGitOutputStream extends FilterOutputStream {
    private JGitFileSystemProvider jGitFileSystemProvider;

    public JGitOutputStream(OutputStream stream, JGitFileSystemProvider j) {
        super(stream);
        jGitFileSystemProvider = j;
     }
    
    public void flush() throws IOException {
        super.flush();
        jGitFileSystemProvider.commitAndPush("from jgit");
    }
}




