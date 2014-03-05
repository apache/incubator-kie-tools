package org.drools.workbench.jcr2vfsmigration.migrater.asset;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.contenthandler.drools.BRLContentHandler;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.server.util.BRLPersistence;
import org.drools.ide.common.server.util.BRXMLPersistence;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.RulesRepository;
import org.drools.workbench.jcr2vfsmigration.migrater.PackageImportHelper;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.drools.workbench.screens.drltext.service.DRLTextEditorService;
import org.drools.workbench.screens.guided.rule.service.GuidedRuleEditorService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.StandardCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class GuidedEditorMigrater extends BaseAssetMigrater {

    protected static final Logger logger = LoggerFactory.getLogger( GuidedEditorMigrater.class );

    @Inject
    @Preferred
    private RulesRepository rulesRepository;

    @Inject
    protected GuidedRuleEditorService guidedRuleEditorService;

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
        if ( !AssetFormats.BUSINESS_RULE.equals( jcrAssetItem.getFormat() ) ) {
            throw new IllegalArgumentException( "The jcrAsset (" + jcrAssetItem.getName() + ") has the wrong format (" + jcrAssetItem.getFormat() + ")." );
        }

        try {
           // Asset jcrAsset = jcrRepositoryAssetService.loadRuleAsset( jcrAssetItem.getUUID() );

            RuleModel ruleModel = getBrlXmlPersistence().unmarshal( jcrAssetItem.getContent() );

            Path path = null;
            if ( ruleModel.hasDSLSentences() ) {
                path = migrationPathManager.generatePathForAsset( jcrModule,
                                                                  jcrAssetItem,
                                                                  true );
            } else {
                path = migrationPathManager.generatePathForAsset( jcrModule,
                                                                  jcrAssetItem,
                                                                  false );
            }

            final org.uberfire.java.nio.file.Path nioPath = Paths.convert( path );

            //The asset was renamed in this version. We move this asset first.
            if(previousVersionPath != null && !previousVersionPath.equals(path)) {
                ioService.move(Paths.convert( previousVersionPath ), nioPath, StandardCopyOption.REPLACE_EXISTING);
            }

            StringBuilder sb = new StringBuilder();
            BRMSPackageBuilder builder = new BRMSPackageBuilder( rulesRepository.loadModuleByUUID( jcrModule.getUuid() ) );
            BRLContentHandler handler = new BRLContentHandler();

            handler.assembleDRL( builder,
                                 jcrAssetItem,
                                 sb );

            //Support for # has been removed from Drools Expert
            String content = sb.toString().replaceAll( "#",
                                                       "//" );

            content = getExtendExpression(jcrModule,jcrAssetItem,content);

            String sourceDRLWithImport = packageImportHelper.assertPackageImportDRL( content,
                                                                                     path );
            sourceDRLWithImport = packageImportHelper.assertPackageName( sourceDRLWithImport,
                                                                              path );

            ioService.write( nioPath,
                             sourceDRLWithImport,
                             migrateMetaData(jcrModule, jcrAssetItem),
                             new CommentedOption( jcrAssetItem.getLastContributor(),
                                                  null,
                                                  jcrAssetItem.getCheckinComment(),
                                                  jcrAssetItem.getLastModified().getTime() ) );
            
            return path;

        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }

    protected BRLPersistence getBrlXmlPersistence() {
        return BRXMLPersistence.getInstance();
    }
}
