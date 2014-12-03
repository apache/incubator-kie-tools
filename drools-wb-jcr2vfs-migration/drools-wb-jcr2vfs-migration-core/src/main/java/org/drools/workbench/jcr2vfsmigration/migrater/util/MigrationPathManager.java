package org.drools.workbench.jcr2vfsmigration.migrater.util;

import java.net.URI;
import java.util.HashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.Module;
import org.drools.repository.AssetItem;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.AssetType;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.XmlAsset;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;

/**
 * Generates a Path for every object that needs to be migrated.
 * Guarantees uniqueness. Supports look ups.
 */
@ApplicationScoped
public class MigrationPathManager {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    private FileSystem fs;

    private static final String[] JAVA_KEYWORDS = { "package", "import",
            "class", "public", "protected", "private", "extends", "implements",
            "return", "if", "while", "for", "do", "else", "try", "new", "void",
            "catch", "throws", "throw", "static", "final", "break", "continue",
            "super", "finally", "true", "false", "true;", "false;", "null",
            "boolean", "int", "char", "long", "float", "double", "short",
            "abstract", "this", "switch", "assert", "default", "goto", "synchronized",
            "byte", "case", "enum", "instanceof", "transient", "interface", "strictfp", "volatile", "const", "native" };

    // Generate methods

    public Path generateRootPath() {
        final org.uberfire.java.nio.file.Path _path = getFileSystem().getPath( "/" );

        return Paths.convert( _path );
    }

    public Path generatePathForModule( String jcrModuleName ) {
        return Paths.convert( getFileSystem().getPath( "/" + escapePathEntry( jcrModuleName ) ) );
    }

    public Path generatePathForAsset( Module jcrModule,
                                      Asset jcrAsset,
                                      boolean hasDSL ) {
        final org.uberfire.java.nio.file.Path modulePath = getFileSystem().getPath( "/" + escapePathEntry( jcrModule.getName() ) );

        //final org.uberfire.java.nio.file.Path directory = getPomDirectoryPath(pathToPom);
        org.uberfire.java.nio.file.Path assetPath = null;
        if ( AssetFormats.BUSINESS_RULE.equals( jcrAsset.getFormat() ) && !hasDSL ) {
            assetPath = modulePath.resolve( "src/main/resources/" + dotToSlash( jcrAsset.getName() ) + ".rdrl" );
        } else if ( AssetFormats.BUSINESS_RULE.equals( jcrAsset.getFormat() ) && hasDSL ) {
            assetPath = modulePath.resolve( "src/main/resources/" + dotToSlash( jcrModule.getName() ) + "/" + jcrAsset.getName() + ".rdslr" );
        } else {
            assetPath = modulePath.resolve( "src/main/resources/" + dotToSlash( jcrModule.getName() ) + "/" + jcrAsset.getName() + "." + jcrAsset.getFormat() );
        }

        return Paths.convert( assetPath );
    }

    public Path generatePathForGlobal( Module jcrModule ) {
        final org.uberfire.java.nio.file.Path modulePath = getFileSystem().getPath( "/" + escapePathEntry( jcrModule.getName() ) );

        org.uberfire.java.nio.file.Path assetPath = modulePath.resolve( "src/main/resources/" + dotToSlash( jcrModule.getName() ) + "/" + "globals.gdrl" );

        return Paths.convert( assetPath );
    }

    public Path generatePathForGlobal( org.drools.workbench.jcr2vfsmigration.xml.model.Module xmlModule ) {
        // the equivalent of jcrModule.getName() is xmlModule.getNormalizedPackageName(), since in the original migration
        // the jcrModule name is replaced with the normalized package name.
        final org.uberfire.java.nio.file.Path modulePath = getFileSystem().getPath( "/" + escapePathEntry( xmlModule.getNormalizedPackageName() ) );

        org.uberfire.java.nio.file.Path assetPath = modulePath.resolve( "src/main/resources/" + dotToSlash( xmlModule.getNormalizedPackageName() ) + "/" + "globals.gdrl" );

        return Paths.convert( assetPath );
    }

    public Path generatePathForAsset( Module jcrModule,
                                      AssetItem jcrAssetItem,
                                      boolean hasDSL ) {
        final org.uberfire.java.nio.file.Path modulePath = getFileSystem().getPath( "/" + escapePathEntry( jcrModule.getName() ) );

        org.uberfire.java.nio.file.Path assetPath = null;

        if ( AssetFormats.BUSINESS_RULE.equals( jcrAssetItem.getFormat() ) && !hasDSL ) {
            assetPath = modulePath.resolve( "src/main/resources/" + dotToSlash( jcrModule.getName() ) + "/" + jcrAssetItem.getName() + ".rdrl" );
        } else if ( AssetFormats.BUSINESS_RULE.equals( jcrAssetItem.getFormat() ) && hasDSL ) {
            assetPath = modulePath.resolve( "src/main/resources/" + dotToSlash( jcrModule.getName() ) + "/" + jcrAssetItem.getName() + ".rdslr" );
        } else if ( AssetFormats.FUNCTION.equals( jcrAssetItem.getFormat() ) ) {
            assetPath = modulePath.resolve( "src/main/resources/" + dotToSlash( jcrModule.getName() ) + "/" + jcrAssetItem.getName() + ".drl" );
        } else if ( AssetFormats.TEST_SCENARIO.equals( jcrAssetItem.getFormat() ) ) {
            assetPath = modulePath.resolve( "src/test/resources/" + dotToSlash( jcrModule.getName() ) + "/" + jcrAssetItem.getName() + "." + jcrAssetItem.getFormat() );
        } else {
            assetPath = modulePath.resolve( "src/main/resources/" + dotToSlash( jcrModule.getName() ) + "/" + jcrAssetItem.getName() + "." + jcrAssetItem.getFormat() );
        }

        return Paths.convert( assetPath );
    }

    public Path generatePathForAsset( org.drools.workbench.jcr2vfsmigration.xml.model.Module xmlModule,
            XmlAsset xmlAsset,
            boolean hasDSL ) {

        // In the previous version of the tool, when getting to this point the jcrModule's name had been replaced with the normalized package name
        String normalizedName = xmlModule.getNormalizedPackageName();
        final org.uberfire.java.nio.file.Path modulePath = getFileSystem().getPath( "/" + escapePathEntry( normalizedName ) );

        org.uberfire.java.nio.file.Path assetPath = null;

        if ( AssetType.BUSINESS_RULE.equals( xmlAsset.getAssetType() ) && !hasDSL ) {
            assetPath = modulePath.resolve( "src/main/resources/" + dotToSlash( normalizedName ) + "/" + xmlAsset.getName() + ".rdrl" );
        } else if ( AssetType.BUSINESS_RULE.equals( xmlAsset.getAssetType() ) && hasDSL ) {
            assetPath = modulePath.resolve( "src/main/resources/" + dotToSlash( normalizedName ) + "/" + xmlAsset.getName() + ".rdslr" );
        } else if ( AssetType.FUNCTION.equals( xmlAsset.getAssetType() ) ) {
            assetPath = modulePath.resolve( "src/main/resources/" + dotToSlash( normalizedName ) + "/" + xmlAsset.getName() + ".drl" );
        } else if ( AssetType.TEST_SCENARIO.equals( xmlAsset.getAssetType() ) ) {
            assetPath = modulePath.resolve( "src/test/resources/" + dotToSlash( normalizedName ) + "/" + xmlAsset.getName() + "." + xmlAsset.getAssetType() );
        } else {
            assetPath = modulePath.resolve( "src/main/resources/" + dotToSlash( normalizedName ) + "/" + xmlAsset.getName() + "." + xmlAsset.getAssetType() );
        }

        return Paths.convert( assetPath );
    }

    public Path generatePathForAsset( Module jcrModule,
                                      AssetItem jcrAssetItem ) {
        return generatePathForAsset( jcrModule, jcrAssetItem, false );
    }

    public Path generatePathForAsset( org.drools.workbench.jcr2vfsmigration.xml.model.Module xmlModule,
            XmlAsset xmlAsset ) {
        return generatePathForAsset( xmlModule, xmlAsset, false );
    }

    private org.uberfire.java.nio.file.Path getPomDirectoryPath( final Path pathToPomXML ) {
        return Paths.convert( pathToPomXML ).getParent();
    }

    // Helper methods

    public String escapePathEntry( String pathEntry ) {
        // VFS doesn't support /'s in the path entries
        pathEntry = pathEntry.replace( "/", " slash " );
        pathEntry = normalizePackageName( pathEntry );
        // TODO Once porcelli has a list of all illegal and escaped characters in PathEntry, deal with them here
        return pathEntry;
    }

    public String normalizePackageName( String stringToEscape ) {
        String[] nameSplit = stringToEscape.split( "\\." );
        StringBuilder normalizedPkgNameBuilder = new StringBuilder();

        for ( int j = 0; j < nameSplit.length; j++ ) {
            int i = 0;
            if ( j > 0 && j < nameSplit.length ) {
                normalizedPkgNameBuilder.append( "." );
            }
            for (; i < JAVA_KEYWORDS.length; i++ ) {
                if ( JAVA_KEYWORDS[ i ].equals( nameSplit[ j ] ) ) {
                    normalizedPkgNameBuilder.append( "mod_" );
                    normalizedPkgNameBuilder.append( nameSplit[ j ].toLowerCase() );
                    break;
                }
            }
            if ( i == JAVA_KEYWORDS.length ) {
                normalizedPkgNameBuilder.append( nameSplit[ j ].toLowerCase() );
            }
        }
        return normalizedPkgNameBuilder.toString();
    }

    public String dotToSlash( String pathEntry ) {
        // VFS doesn't support /'s in the path entries
        pathEntry = pathEntry.replace( ".", "/" );
        pathEntry = normalizePackageName( pathEntry ); //Added to be consistent with project creation
        return pathEntry;
    }

    public void setRepoName( final String repoName,
                             final String outputDir ) {
        URI uri = URI.create( "git://" + repoName );
        this.fs = ioService.newFileSystem( uri, new HashMap<String, Object>() {{
            put( "out-dir", outputDir );
            put( "init", true );
        }} );
    }

    public FileSystem getFileSystem() {
        return fs;
    }

}
