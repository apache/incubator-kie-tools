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
import org.kie.workbench.io.IOService;
import org.kie.workbench.java.nio.base.options.CommentedOption;
import org.kie.workbench.java.nio.file.NoSuchFileException;
import org.kie.guvnor.drltext.service.DRLTextEditorService;
import org.drools.workbench.jcr2vfsmigration.migrater.PackageImportHelper;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class PlainTextAssetWithPackagePropertyMigrater {
    protected static final Logger logger = LoggerFactory.getLogger(PlainTextAssetWithPackagePropertyMigrater.class);

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

    public void migrate(Module jcrModule, AssetItem jcrAssetItem) {        
        Path path = migrationPathManager.generatePathForAsset(jcrModule, jcrAssetItem);
        final org.kie.workbench.java.nio.file.Path nioPath = paths.convert( path );

        Map<String, Object> attrs;
        try {
            attrs = ioService.readAttributes( nioPath );
        } catch ( final NoSuchFileException ex ) {
            attrs = new HashMap<String, Object>();
        }
 
        StringBuilder sb = new StringBuilder();
        
        if(AssetFormats.DRL.equals(jcrAssetItem.getFormat())) {
            sb.append("rule '" + jcrAssetItem.getName() + "'");     
            sb.append( "\n" );
            sb.append( "\n" );
        } else if (AssetFormats.FUNCTION.equals(jcrAssetItem.getFormat())) {
            sb.append("function '" + jcrAssetItem.getName() + "'"); 
            sb.append( "\n" );
            sb.append( "\n" );
        }        
        sb.append(jcrAssetItem.getContent());      
        sb.append( "\n" );
        sb.append( "\n" );
        sb.append("end");     
        
        String sourceWithImport = drlTextEditorServiceImpl.assertPackageName(sb.toString(), path);
        sourceWithImport = packageImportHelper.assertPackageImportDRL(sourceWithImport, path);
        
        ioService.write( nioPath, sourceWithImport, attrs, new CommentedOption(jcrAssetItem.getLastContributor(), null, jcrAssetItem.getCheckinComment(), jcrAssetItem.getLastModified().getTime() ));
    }

 }
