package org.drools.workbench.jcr2vfsmigration.migrater.asset;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.repository.AssetItem;
import org.drools.workbench.jcr2vfsmigration.migrater.GlobalParser;
import org.drools.workbench.jcr2vfsmigration.migrater.PackageHeaderInfo;
import org.drools.workbench.jcr2vfsmigration.migrater.PackageImportHelper;
import org.drools.workbench.jcr2vfsmigration.migrater.util.DRLMigrationUtils;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.StandardCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class GlobalMigrater extends BaseAssetMigrater {

    protected static final Logger logger = LoggerFactory.getLogger( GlobalMigrater.class );
    private static final String GLOBAL_KEYWORD = "global ";

    @Inject
    private Paths paths;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private PackageHeaderInfo packageHeaderInfo;
    
    @Inject
    PackageImportHelper packageImportHelper;
    
    public Path migrate( Module jcrModule,
                         List<String> globals) {
        if(globals.size() == 0) {
            return null;
        }
        
        Path path = migrationPathManager.generatePathForGlobal( jcrModule );
        final org.uberfire.java.nio.file.Path nioPath = paths.convert( path );
        
        StringBuffer content = new StringBuffer();
        for(String global : globals) {
            content.append(GLOBAL_KEYWORD);
            content.append(global);
            content.append("\n");
        }
        String contentWithImport = packageImportHelper.assertPackageImportDRL( content.toString(), path );

        String contentWithPackage = packageImportHelper.assertPackageName(contentWithImport, null);
        ioService.write( nioPath,
                         contentWithPackage,
                         new CommentedOption( jcrModule.getLastContributor(),
                                              null,
                                              jcrModule.getCheckinComment(),
                                              jcrModule.getLastModified() ) );
        
        return path;
    }

}
