package org.drools.workbench.jcr2vfsmigration.migrater.asset;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.workbench.jcr2vfsmigration.migrater.PackageImportHelper;
import org.drools.workbench.jcr2vfsmigration.migrater.util.DRLMigrationUtils;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.drools.workbench.screens.drltext.service.DRLTextEditorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.StandardCopyOption;

@ApplicationScoped
public class PlainTextAssetWithPackagePropertyMigrater extends BaseAssetMigrater {

    protected static final Logger logger = LoggerFactory.getLogger( PlainTextAssetWithPackagePropertyMigrater.class );

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    DRLTextEditorService drlTextEditorServiceImpl;

    @Inject
    PackageImportHelper packageImportHelper;

    public Path migrate( Module jcrModule,
                         AssetItem jcrAssetItem,
                         Path previousVersionPath) {
        Path path = migrationPathManager.generatePathForAsset( jcrModule,
                                                               jcrAssetItem );
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert( path );
        //The asset was renamed in this version. We move this asset first.
        if(previousVersionPath != null && !previousVersionPath.equals(path)) {
            ioService.move(Paths.convert( previousVersionPath ), nioPath, StandardCopyOption.REPLACE_EXISTING);
        }

        StringBuilder sb = new StringBuilder();

        if ( AssetFormats.DRL.equals( jcrAssetItem.getFormat() ) && jcrAssetItem.getContent().toLowerCase().indexOf("rule ")==-1 ) {
            sb.append( "rule \"" + jcrAssetItem.getName() + "\"" );
            sb.append( getExtendExpression(jcrModule,jcrAssetItem,"") );
            sb.append( "\n" );
            sb.append( "\n" );
            sb.append( jcrAssetItem.getContent() );
            sb.append( "\n" );
            sb.append( "\n" );
            sb.append( "end" );
        }
        else{
            sb.append( jcrAssetItem.getContent() );
            sb.append( "\n" );
        }


        String content = sb.toString();        
       
        // Support for '#' has been removed from Drools Expert -> replace it with '//'
        if (AssetFormats.DSL.equals(jcrAssetItem.getFormat())
                || AssetFormats.DSL_TEMPLATE_RULE.equals(jcrAssetItem.getFormat())
                || AssetFormats.RULE_TEMPLATE.equals(jcrAssetItem.getFormat())
                || AssetFormats.DRL.equals(jcrAssetItem.getFormat())
                || AssetFormats.FUNCTION.equals(jcrAssetItem.getFormat())) {
            content = DRLMigrationUtils.migrateStartOfCommentChar(content);
        }



        String sourceWithImport = packageImportHelper.assertPackageImportDRL( content,
                                                                              path );
        sourceWithImport = packageImportHelper.assertPackageName( sourceWithImport,
                                                                       path );

        ioService.write( nioPath,
                         sourceWithImport,
                         migrateMetaData(jcrModule, jcrAssetItem),
                         new CommentedOption( jcrAssetItem.getLastContributor(),
                                              null,
                                              jcrAssetItem.getCheckinComment(),
                                              jcrAssetItem.getLastModified().getTime() ) );

        return path;
    }

}
