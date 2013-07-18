package org.drools.workbench.jcr2vfsmigration.migrater.asset;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.repository.AssetItem;
import org.drools.workbench.jcr2vfsmigration.migrater.PackageHeaderInfo;
import org.drools.workbench.jcr2vfsmigration.migrater.PackageImportHelper;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.drools.workbench.models.commons.backend.imports.ImportsParser;
import org.drools.workbench.models.commons.shared.imports.Imports;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.rule.service.GuidedRuleEditorService;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.ProjectService;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class GuidedDecisionTableMigrater {

    protected static final Logger logger = LoggerFactory.getLogger( GuidedDecisionTableMigrater.class );

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

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
    protected PackageImportHelper packageImportHelper;

    @Inject
    private ProjectService projectService;

    @Inject
    private PackageHeaderInfo packageHeaderInfo;

    public void migrate( Module jcrModule,
                         AssetItem jcrAssetItem ) {
        if ( !AssetFormats.DECISION_TABLE_GUIDED.equals( jcrAssetItem.getFormat() ) ) {
            throw new IllegalArgumentException( "The jcrAsset (" + jcrAssetItem.getName() + ") has the wrong format (" + jcrAssetItem.getFormat() + ")." );
        }

        Path path = migrationPathManager.generatePathForAsset( jcrModule,
                                                               jcrAssetItem );
        final org.kie.commons.java.nio.file.Path nioPath = paths.convert( path );
        if ( !Files.exists( nioPath ) ) {
            ioService.createFile( nioPath );
        }

        String content = jcrAssetItem.getContent();
        
/*        while(content.indexOf("<auditLog>") > -1) {
            content  = content.replaceAll(content.substring(content.indexOf("<auditLog>"), content.indexOf("</auditLog>")+11), "");
        }
        */
        content = content.replaceAll( "org.drools.guvnor.client.modeldriven.dt52.Pattern52",
                                      "Pattern52" );

        GuidedDecisionTable52 model = GuidedDTXMLPersistence.getInstance().unmarshal( content );

        //Add package
        final Package pkg = projectService.resolvePackage( path );
        final String requiredPackageName = ( pkg == null ? null : pkg.getPackageName() );
        if ( requiredPackageName != null || !"".equals( requiredPackageName ) ) {
            model.setPackageName( requiredPackageName );
        }

        //Add import
        if ( packageHeaderInfo.getHeader() != null ) {
            final Imports imports = ImportsParser.parseImports( packageHeaderInfo.getHeader() );
            if ( imports != null ) {
                model.setImports( imports );
            }
        }

        String sourceContent = GuidedDTXMLPersistence.getInstance().marshal( model );
        
/*        GuidedDTContentHandler h = new GuidedDTContentHandler();
        String sourceContent = h.getRawDRL(jcrAssetItem);*/

        //String sourceContent = jcrAssetItem.getContent();

        //String sourceContentWithPackage = packageImportHelper.assertPackageNameXML(sourceContent, path);
        //sourceContentWithPackage = packageImportHelper.assertPackageImportXML(sourceContentWithPackage, path);

        ioService.write( nioPath,
                         sourceContent,
                         new CommentedOption( jcrAssetItem.getLastContributor(),
                                              null,
                                              jcrAssetItem.getCheckinComment(),
                                              jcrAssetItem.getLastModified().getTime() ) );
    }
}
