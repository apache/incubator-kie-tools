package org.drools.workbench.jcr2vfsmigration.migrater.asset;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Module;
import org.drools.repository.AssetItem;
import org.drools.workbench.jcr2vfsmigration.migrater.PackageHeaderInfo;
import org.drools.workbench.jcr2vfsmigration.migrater.PackageImportHelper;
import org.drools.workbench.models.commons.backend.imports.ImportsParser;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.rule.service.GuidedRuleEditorService;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.StandardCopyOption;


@ApplicationScoped
public class GuidedDecisionTableMigrater extends BaseAssetMigrater {

    protected static final Logger logger = LoggerFactory.getLogger( GuidedDecisionTableMigrater.class );

    @Inject
    protected GuidedRuleEditorService guidedRuleEditorService;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    protected PackageImportHelper packageImportHelper;

    @Inject
    private ProjectService projectService;

    @Inject
    private PackageHeaderInfo packageHeaderInfo;

    public Path migrate( Module jcrModule,
                         AssetItem jcrAssetItem,
                         Path previousVersionPath) {
        if ( !AssetFormats.DECISION_TABLE_GUIDED.equals( jcrAssetItem.getFormat() ) ) {
            throw new IllegalArgumentException( "The jcrAsset (" + jcrAssetItem.getName() + ") has the wrong format (" + jcrAssetItem.getFormat() + ")." );
        }

        Path path = migrationPathManager.generatePathForAsset( jcrModule,
                                                               jcrAssetItem );
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert( path );
        //The asset was renamed in this version. We move this asset first.
        if(previousVersionPath != null && !previousVersionPath.equals(path)) {
            ioService.move(Paths.convert( previousVersionPath ), nioPath, StandardCopyOption.REPLACE_EXISTING);
        }

        String content = jcrAssetItem.getContent();
        
        content = content.replaceAll( "org.drools.guvnor.client.modeldriven.dt52.Pattern52",
                                      "Pattern52" );

        GuidedDecisionTable52 model = GuidedDTXMLPersistence.getInstance().unmarshal( content );

        //Add package
        final Package pkg = projectService.resolvePackage( path );
        String pkName =pkg.getPackageName();
        try{
            if(pkName!=null && pkg.getPackageName().endsWith(path.getFileName())){
                pkName = pkg.getPackageName().substring(0,pkg.getPackageName().indexOf(path.getFileName())-1);
            }
        }catch (Exception e){

        }
        final String requiredPackageName = pkName;
        if ( requiredPackageName != null && !"".equals( requiredPackageName ) ) {
            model.setPackageName( requiredPackageName );
        }
        model.setParentName(getExtendedRuleFromCategoryRules(jcrModule,jcrAssetItem,""));

        //Add import
        if ( packageHeaderInfo.getHeader() != null ) {
            final Imports imports = ImportsParser.parseImports( packageHeaderInfo.getHeader() );
            if ( imports != null ) {
                model.setImports( imports );
            }
        }

        String sourceContent = GuidedDTXMLPersistence.getInstance().marshal( model );
        
        ioService.write( nioPath,
                         sourceContent,
                         migrateMetaData(jcrModule, jcrAssetItem),
                         new CommentedOption( jcrAssetItem.getLastContributor(),
                                              null,
                                              jcrAssetItem.getCheckinComment(),
                                              jcrAssetItem.getLastModified().getTime() ) );
        return path;
    }
}
