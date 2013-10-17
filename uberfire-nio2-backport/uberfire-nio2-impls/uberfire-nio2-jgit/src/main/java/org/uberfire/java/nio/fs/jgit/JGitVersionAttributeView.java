package org.uberfire.java.nio.fs.jgit;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.base.version.VersionAttributes;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;

/**
 *
 */
public class JGitVersionAttributeView extends VersionAttributeView<JGitPathImpl> {

    private VersionAttributes attrs = null;

    public JGitVersionAttributeView( final JGitPathImpl path ) {
        super( path );
    }

    @Override
    public VersionAttributes readAttributes() throws IOException {
        if ( attrs == null ) {
            attrs = JGitUtil.buildVersionAttributes( path.getFileSystem(), path.getRefTree(), path.getPath() );
        }
        return attrs;
    }

    @Override
    public Class<? extends BasicFileAttributeView>[] viewTypes() {
        return new Class[]{ VersionAttributeView.class, JGitVersionAttributeView.class };
    }

}
