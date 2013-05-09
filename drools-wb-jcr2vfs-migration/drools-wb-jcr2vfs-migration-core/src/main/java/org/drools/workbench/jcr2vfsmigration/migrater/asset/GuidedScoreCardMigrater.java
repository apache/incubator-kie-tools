package org.drools.workbench.jcr2vfsmigration.migrater.asset;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.repository.AssetItem;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.NoSuchFileException;
import org.kie.guvnor.guided.rule.service.GuidedRuleEditorService;
import org.drools.workbench.jcr2vfsmigration.migrater.PackageImportHelper;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class GuidedScoreCardMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(GuidedScoreCardMigrater.class);

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

    public void migrate(Module jcrModule,  AssetItem jcrAssetItem) {      
        if (!AssetFormats.SCORECARD_GUIDED.equals(jcrAssetItem.getFormat())) {
            throw new IllegalArgumentException("The jcrAsset (" + jcrAssetItem.getName()
                    + ") has the wrong format (" + jcrAssetItem.getFormat() + ").");
        }
        
        Path path = migrationPathManager.generatePathForAsset(jcrModule, jcrAssetItem);        
        final org.kie.commons.java.nio.file.Path nioPath = paths.convert( path );

        Map<String, Object> attrs;
        try {
            attrs = ioService.readAttributes( nioPath );
        } catch ( final NoSuchFileException ex ) {
            attrs = new HashMap<String, Object>();
        }        
        
/*        ScorecardsContentHandler h = new ScorecardsContentHandler();
        String sourceDRL = h.getRawDRL(jcrAssetItem);*/
        
        String sourceContent = jcrAssetItem.getContent();    
        
        String sourceContentWithPackage = packageImportHelper.assertPackageNameXML(sourceContent, path);
        sourceContentWithPackage = packageImportHelper.assertPackageImportXML(sourceContentWithPackage, path);

        ioService.write( nioPath, sourceContentWithPackage, attrs, new CommentedOption(jcrAssetItem.getLastContributor(), null, jcrAssetItem.getCheckinComment(), jcrAssetItem.getLastModified().getTime() ));
    }
}
