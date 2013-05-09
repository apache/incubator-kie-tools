package org.drools.workbench.jcr2vfsmigration.migrater.util;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.Module;
import org.drools.repository.AssetItem;
import org.kie.commons.java.nio.file.FileSystem;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

/**
 * Generates a Path for every object that needs to be migrated.
 * Guarantees uniqueness. Supports look ups.
 */
@ApplicationScoped
public class MigrationPathManager {
    @Inject
    private Paths paths;

    @Inject
    @Named("migrationFS")
    private FileSystem fs;
    
    private Map<String, Path> uuidToPathMap = new HashMap<String, Path>();
    private Map<Path, String> pathToUuidMap = new HashMap<Path, String>();

    // Generate methods

    public Path generateRootPath() {

        final org.kie.commons.java.nio.file.Path _path = fs.getPath( "/" + escapePathEntry( "project" ));

        final Path path = PathFactory.newPath( paths.convert( _path.getFileSystem() ), _path.getFileName().toString(), _path.toUri().toString() );

        return path;
    }
    
    public Path generatePathForModule( Module jcrModule ) {
        final org.kie.commons.java.nio.file.Path modulePath = fs.getPath( "/" + escapePathEntry( jcrModule.getName() ) );

        final Path path = PathFactory.newPath( paths.convert( modulePath.getFileSystem() ), modulePath.getFileName().toString(), modulePath.toUri().toString() );

        register( jcrModule.getUuid(), path );
        return path;
    }

    public Path generatePathForAsset( Module jcrModule,
                                      Asset jcrAsset,
                                      boolean hasDSL) {
        final org.kie.commons.java.nio.file.Path modulePath = fs.getPath( "/" + escapePathEntry( jcrModule.getName() ) );
        
        //final org.kie.commons.java.nio.file.Path directory = getPomDirectoryPath(pathToPom);
        org.kie.commons.java.nio.file.Path assetPath = null;
        if(AssetFormats.BUSINESS_RULE.equals(jcrAsset.getFormat()) && !hasDSL) {
            assetPath = modulePath.resolve("src/main/resources/" + jcrAsset.getName() + ".gre.drl");
        } else if (AssetFormats.BUSINESS_RULE.equals(jcrAsset.getFormat()) && hasDSL) {
            assetPath = modulePath.resolve("src/main/resources/" + jcrAsset.getName() + ".gre.dslr");
        } else {
            assetPath = modulePath.resolve("src/main/resources/" + jcrAsset.getName() + "." + jcrAsset.getFormat());           
        }
        
        //final org.kie.commons.java.nio.file.Path _path = fs.getPath( "/" + escapePathEntry( jcrModule.getName() ) + "/" + escapePathEntry( jcrAsset.getName() ) + "." + jcrAsset.getFormat() );

        final Path path = PathFactory.newPath( paths.convert( assetPath.getFileSystem() ), assetPath.getFileName().toString(), assetPath.toUri().toString() );

        register( jcrAsset.getUuid(), path );
        return path;
    }

    public Path generatePathForAsset(Module jcrModule, Asset jcrAsset) {        
        return  generatePathForAsset( jcrModule, jcrAsset, false );
    } 

    public Path generatePathForAsset(Module jcrModule, AssetItem jcrAssetItem, boolean hasDSL) {
        final org.kie.commons.java.nio.file.Path modulePath = fs.getPath("/" + escapePathEntry(jcrModule.getName()));

        org.kie.commons.java.nio.file.Path assetPath = null;
        if (AssetFormats.BUSINESS_RULE.equals(jcrAssetItem.getFormat()) && !hasDSL) {
            assetPath = modulePath.resolve("src/main/resources/" + jcrAssetItem.getName() + ".gre.drl");
        } else if (AssetFormats.BUSINESS_RULE.equals(jcrAssetItem.getFormat()) && hasDSL) {
            assetPath = modulePath.resolve("src/main/resources/" + jcrAssetItem.getName() + ".gre.dslr");
        } else if (AssetFormats.TEST_SCENARIO.equals(jcrAssetItem.getFormat())) {
            assetPath = modulePath.resolve("src/test/resources/" + jcrAssetItem.getName() + "." + jcrAssetItem.getFormat());
        } else {
            assetPath = modulePath.resolve("src/main/resources/" + jcrAssetItem.getName() + "." + jcrAssetItem.getFormat());
        }

        final Path path = PathFactory.newPath(paths.convert(assetPath.getFileSystem()), assetPath.getFileName().toString(), assetPath.toUri().toString());

        register(jcrAssetItem.getUUID(), path);
        return path;
    }
    
    public Path generatePathForAsset(Module jcrModule, AssetItem jcrAssetItem) {        
        return generatePathForAsset(jcrModule, jcrAssetItem, false);
    }
    
    private org.kie.commons.java.nio.file.Path getPomDirectoryPath(final Path pathToPomXML) {
        return paths.convert(pathToPomXML).getParent();
    }
    
    // Helper methods

    public String escapePathEntry( String pathEntry ) {
        // VFS doesn't support /'s in the path entries
        pathEntry = pathEntry.replaceAll( "/", " slash " );
        // TODO Once porcelli has a list of all illegal and escaped characters in PathEntry, deal with them here
        return pathEntry;
    }

    protected void register( String uuid,
                             Path path ) {
        if ( uuidToPathMap.containsKey( uuid ) ) {
            //already registered
            return;
/*            throw new IllegalArgumentException( "The uuid (" + uuid + ") cannot be registered for path ("
                                                        + path + ") because it has already been registered once. Last time it was for path ("
                                                        + uuidToPathMap.get( uuid ) + "), but even if it's equal, it should never be registered twice." );
*/        }
        if ( pathToUuidMap.containsKey( path ) ) {
            //already registered
            return;
/*            throw new IllegalArgumentException( "The path (" + path + ") cannot be registered from uuid ("
                                                        + uuid + ") because it has already been registered once. Last time it was for uuid ("
                                                        + pathToUuidMap.get( path ) + "), but even if it's equal, it should never be registered twice." );
*/        }
        uuidToPathMap.put( uuid, path );
        pathToUuidMap.put( path, uuid );
    }

    public Path getPath( String uuid ) {
        return uuidToPathMap.get( uuid );
    }

    public String getUuid( Path path ) {
        return pathToUuidMap.get( path );
    }

}
