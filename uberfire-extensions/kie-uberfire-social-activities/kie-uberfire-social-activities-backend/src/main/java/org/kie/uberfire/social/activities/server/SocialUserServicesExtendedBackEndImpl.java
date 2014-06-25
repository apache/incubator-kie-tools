package org.kie.uberfire.social.activities.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.backend.server.io.SystemFS;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.AbstractPath;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class SocialUserServicesExtendedBackEndImpl {

    @Inject
    @Named("configIO")
    private IOService ioService;

    @Inject
    @SystemFS
    private FileSystem fileSystem;

    public List<String> getAllBranches() {
        List<String> branches = new ArrayList<String>();
        for ( Iterator it = fileSystem.getRootDirectories().iterator(); it.hasNext(); ) {
            AbstractPath path = (AbstractPath) it.next();
            branches.add( path.getHost() );
        }
        return branches;
    }

    public Path buildPath( final String serviceType,
                           final String relativePath ) {

        if ( relativePath != null && !"".equals( relativePath ) ) {
            return fileSystem.getPath( "social", serviceType, relativePath );
        } else {
            return fileSystem.getPath( "social", serviceType );
        }
    }
}
