package org.drools.workbench.jcr2vfsmigration.migrater.asset;

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
import org.drools.repository.RulesRepository;
import org.drools.workbench.jcr2vfsmigration.migrater.PackageImportHelper;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.drools.workbench.screens.drltext.service.DRLTextEditorService;
import org.drools.workbench.screens.guided.rule.service.GuidedRuleEditorService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class GuidedEditorMigrater {

    protected static final Logger logger = LoggerFactory.getLogger( GuidedEditorMigrater.class );

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    @Preferred
    private RulesRepository rulesRepository;

    @Inject
    protected GuidedRuleEditorService guidedRuleEditorService;

    @Inject
    protected MigrationPathManager migrationPathManager;

    @Inject
    private Paths paths;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    DRLTextEditorService drlTextEditorServiceImpl;

    @Inject
    PackageImportHelper packageImportHelper;

    public void migrate( Module jcrModule,
                         AssetItem jcrAssetItem ) {
        if ( !AssetFormats.BUSINESS_RULE.equals( jcrAssetItem.getFormat() ) ) {
            throw new IllegalArgumentException( "The jcrAsset (" + jcrAssetItem.getName() + ") has the wrong format (" + jcrAssetItem.getFormat() + ")." );
        }

        try {
            Asset jcrAsset = jcrRepositoryAssetService.loadRuleAsset( jcrAssetItem.getUUID() );

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

            final org.uberfire.java.nio.file.Path nioPath = paths.convert( path );
            if ( !Files.exists( nioPath ) ) {
                ioService.createFile( nioPath );
            }

            StringBuilder sb = new StringBuilder();
            BRMSPackageBuilder builder = new BRMSPackageBuilder( rulesRepository.loadModuleByUUID( jcrModule.getUuid() ) );
            BRLContentHandler handler = new BRLContentHandler();
            handler.assembleDRL( builder,
                                 jcrAsset,
                                 sb );

            //Support for # has been removed from Drools Expert
            String content = sb.toString().replaceAll( "#",
                                                       "//" );

            String sourceDRLWithImport = drlTextEditorServiceImpl.assertPackageName( content,
                                                                                     path );
            sourceDRLWithImport = packageImportHelper.assertPackageImportDRL( sourceDRLWithImport,
                                                                              path );

            ioService.write( nioPath,
                             sourceDRLWithImport,
                             new CommentedOption( jcrAssetItem.getLastContributor(),
                                                  null,
                                                  jcrAssetItem.getCheckinComment(),
                                                  jcrAssetItem.getLastModified().getTime() ) );

        } catch ( SerializationException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected BRLPersistence getBrlXmlPersistence() {
        return BRXMLPersistence.getInstance();
    }
}
