package org.drools.workbench.jcr2vfsmigration.migrater.asset;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.repository.AssetItem;
import org.kie.workbench.io.IOService;
import org.kie.workbench.java.nio.base.options.CommentedOption;
import org.kie.workbench.java.nio.file.NoSuchFileException;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class AttachementAssetMigrater {
    protected static final Logger logger = LoggerFactory.getLogger(AttachementAssetMigrater.class);

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    private Paths paths;
    
    @Inject
    @Named("ioStrategy")
    private IOService ioService;
    
    @Inject
    protected MigrationPathManager migrationPathManager;

    public void migrate(Module jcrModule, AssetItem jcrAssetItem) {        
        Path path = migrationPathManager.generatePathForAsset(jcrModule, jcrAssetItem);
        final org.kie.workbench.java.nio.file.Path nioPath = paths.convert( path );

        Map<String, Object> attrs;
        try {
            attrs = ioService.readAttributes( nioPath );
        } catch ( final NoSuchFileException ex ) {
            attrs = new HashMap<String, Object>();
        }
        
        byte[] attachement = jcrAssetItem.getBinaryContentAsBytes();
        
        ioService.write(nioPath, attachement, new CommentedOption(jcrAssetItem.getLastContributor(), null, jcrAssetItem.getCheckinComment(), jcrAssetItem.getLastModified().getTime() ));
     }

 }
