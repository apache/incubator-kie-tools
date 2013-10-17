package org.uberfire.java.nio.fs.jgit;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.AbstractBasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;

/**
 *
 */
public class JGitBasicAttributeView extends AbstractBasicFileAttributeView<JGitPathImpl> {

    private BasicFileAttributes attrs = null;

    public JGitBasicAttributeView( final JGitPathImpl path ) {
        super( path );
    }

    @Override
    public BasicFileAttributes readAttributes() throws IOException {
        if ( attrs == null ) {
            attrs = JGitUtil.buildBasicAttributes( path.getFileSystem(), path.getRefTree(), path.getPath() );
        }
        return attrs;
    }

    @Override
    public Class<? extends BasicFileAttributeView>[] viewTypes() {
        return new Class[]{ BasicFileAttributeView.class, JGitBasicAttributeView.class };
    }

}
