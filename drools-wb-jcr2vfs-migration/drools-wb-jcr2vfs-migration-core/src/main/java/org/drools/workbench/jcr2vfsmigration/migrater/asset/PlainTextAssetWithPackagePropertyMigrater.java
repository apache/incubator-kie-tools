package org.drools.workbench.jcr2vfsmigration.migrater.asset;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.repository.AssetItem;
import org.drools.workbench.jcr2vfsmigration.migrater.PackageImportHelper;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.drools.workbench.screens.drltext.service.DRLTextEditorService;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class PlainTextAssetWithPackagePropertyMigrater {

    protected static final Logger logger = LoggerFactory.getLogger( PlainTextAssetWithPackagePropertyMigrater.class );

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    private Paths paths;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    protected MigrationPathManager migrationPathManager;

    @Inject
    DRLTextEditorService drlTextEditorServiceImpl;

    @Inject
    PackageImportHelper packageImportHelper;

    public void migrate( Module jcrModule,
                         AssetItem jcrAssetItem ) {
        Path path = migrationPathManager.generatePathForAsset( jcrModule,
                                                               jcrAssetItem );
        final org.kie.commons.java.nio.file.Path nioPath = paths.convert( path );
        if ( !Files.exists( nioPath ) ) {
            ioService.createFile( nioPath );
        }

        StringBuilder sb = new StringBuilder();

        if ( AssetFormats.DRL.equals( jcrAssetItem.getFormat() ) ) {
            sb.append( "rule '" + jcrAssetItem.getName() + "'" );
            sb.append( "\n" );
            sb.append( "\n" );
        } else if ( AssetFormats.FUNCTION.equals( jcrAssetItem.getFormat() ) ) {
            sb.append( "function '" + jcrAssetItem.getName() + "'" );
            sb.append( "\n" );
            sb.append( "\n" );
        }
        sb.append( jcrAssetItem.getContent() );
        sb.append( "\n" );
        sb.append( "\n" );
        sb.append( "end" );

        //Support for # has been removed from Drools Expert
        String content = sb.toString().replaceAll( "#",
                                                   "//" );

        String sourceWithImport = drlTextEditorServiceImpl.assertPackageName( content,
                                                                              path );
        sourceWithImport = packageImportHelper.assertPackageImportDRL( sourceWithImport,
                                                                       path );

        ioService.write( nioPath,
                         sourceWithImport,
                         new CommentedOption( jcrAssetItem.getLastContributor(),
                                              null,
                                              jcrAssetItem.getCheckinComment(),
                                              jcrAssetItem.getLastModified().getTime() ) );
    }

}
