package org.drools.workbench.jcr2vfsmigration.migrater.asset;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.contenthandler.drools.FactModelContentHandler;
import org.drools.repository.AssetItem;
import org.kie.workbench.io.IOService;
import org.kie.workbench.java.nio.base.options.CommentedOption;
import org.kie.workbench.java.nio.file.NoSuchFileException;
import org.kie.guvnor.drltext.service.DRLTextEditorService;
import org.kie.guvnor.factmodel.service.FactModelService;
import org.drools.workbench.jcr2vfsmigration.migrater.PackageImportHelper;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import com.google.gwt.user.client.rpc.SerializationException;

@ApplicationScoped
public class FactModelsMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(FactModelsMigrater.class);

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    protected FactModelService vfsFactModelService;

    @Inject
    protected MigrationPathManager migrationPathManager;
    
    @Inject
    @Named("ioStrategy")
    private IOService ioService;
    
    @Inject
    private Paths paths;
        
    @Inject
    DRLTextEditorService drlTextEditorServiceImpl;
    
    @Inject
    PackageImportHelper packageImportHelper;
    
    public void migrate(Module jcrModule, AssetItem jcrAssetItem) {      
        if (!AssetFormats.DRL_MODEL.equals(jcrAssetItem.getFormat())) {
            throw new IllegalArgumentException("The jcrAsset (" + jcrAssetItem.getName()
                    + ") has the wrong format (" + jcrAssetItem.getFormat() + ").");
        }
        Path path = migrationPathManager.generatePathForAsset(jcrModule, jcrAssetItem);     
        final org.kie.workbench.java.nio.file.Path nioPath = paths.convert( path );

        Map<String, Object> attrs;
        try {
            attrs = ioService.readAttributes( nioPath );
        } catch ( final NoSuchFileException ex ) {
            attrs = new HashMap<String, Object>();
        }        
        
        FactModelContentHandler h = new FactModelContentHandler();
        StringBuilder stringBuilder = new StringBuilder();
        
        try {
            Asset jcrAsset = jcrRepositoryAssetService.loadRuleAsset(jcrAssetItem.getUUID());
            h.assembleSource(jcrAsset.getContent(), stringBuilder);

            String sourceDRLWithImport = drlTextEditorServiceImpl.assertPackageName(stringBuilder.toString(), path);
            sourceDRLWithImport = packageImportHelper.assertPackageImportDRL(sourceDRLWithImport, path);
            
            ioService.write( nioPath, sourceDRLWithImport, attrs, new CommentedOption(jcrAssetItem.getLastContributor(), null, jcrAssetItem.getCheckinComment(), jcrAssetItem.getLastModified().getTime() ));  
        } catch (SerializationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
     }
}
