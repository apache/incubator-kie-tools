/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.contenthandler;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Pattern;

import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.server.builder.ClassLoaderBuilder;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * This is used for handling jar models for the rules.
 */
public class ModelContentHandler extends ContentHandler
    implements
    ICanHasAttachment {

    public void retrieveAssetContent(Asset asset,
                                     AssetItem item) throws SerializationException {
        // do nothing, as we have an attachment
    }

    public void storeAssetContent(Asset asset,
                                  AssetItem repoAsset) throws SerializationException {
        // do nothing, as we have an attachment
    }

    /**
     * This is called when a model jar is attached, it will peer into it, and
     * then automatically add imports if there aren't any already in the package
     * header configuration.
     */
    public void onAttachmentAdded(AssetItem asset) throws IOException {

        ModuleItem pkg = asset.getModule();
        StringBuilder header = createNewHeader( DroolsHeader.getDroolsHeader( pkg ) );

        Set<String> imports = getImportsFromJar( asset );

        for ( String importLine : imports ) {
            Pattern pattern = Pattern.compile( "\\s" + importLine.replace( ".",
                                                                           "\\." ) + "\\s" );
            if ( !pattern.matcher( header ).find() ) {
                header.append( importLine ).append( "\n" );
            }
        }

        DroolsHeader.updateDroolsHeader( header.toString(),
                                         pkg );
        pkg.checkin( "Imports setup automatically on model import." );

    }

    public void onAttachmentRemoved(AssetItem item) throws IOException {

        ModuleItem pkg = item.getModule();
        StringBuilder header = createNewHeader( DroolsHeader.getDroolsHeader( pkg ) );

        Set<String> imports = getImportsFromJar( item );

        for ( String importLine : imports ) {
            String importLineWithLineEnd = importLine + "\n";

            header = removeImportIfItExists( header,
                                             importLineWithLineEnd );
        }

        DroolsHeader.updateDroolsHeader( header.toString(),
                                                  pkg );

        pkg.checkin( "Imports removed automatically on model archiving." );

    }

    private StringBuilder removeImportIfItExists(StringBuilder header,
                                                 String importLine) {
        if ( header.indexOf( importLine ) >= 0 ) {
            int indexOfImportLine = header.indexOf( importLine );
            header = header.replace( indexOfImportLine,
                                     indexOfImportLine + importLine.length(),
                                     "" );
        }
        return header;
    }

    private StringBuilder createNewHeader(String header) {
        StringBuilder buf = new StringBuilder();

        if ( header != null ) {
            buf.append( header );
            buf.append( '\n' );
        }
        return buf;
    }

    private Set<String> getImportsFromJar(AssetItem assetItem) throws IOException {

        Set<String> imports = new HashSet<String>();
        Map<String, String> nonCollidingImports = new HashMap<String, String>();
        String assetPackageName = assetItem.getModuleName();

        //Setup class-loader to check for class visibility
        JarInputStream cljis = new JarInputStream( assetItem.getBinaryContentAttachment() );
        List<JarInputStream> jarInputStreams = new ArrayList<JarInputStream>();
        jarInputStreams.add( cljis );
        ClassLoaderBuilder clb = new ClassLoaderBuilder( jarInputStreams );
        ClassLoader cl = clb.buildClassLoader();

        //Reset stream to read classes
        JarInputStream jis = new JarInputStream( assetItem.getBinaryContentAttachment() );
        JarEntry entry = null;

        //Get Class names from JAR, only the first occurrence of a given Class leaf name will be inserted. Thus 
        //"org.apache.commons.lang.NumberUtils" will be imported but "org.apache.commons.lang.math.NumberUtils"
        //will not, assuming it follows later in the JAR structure.
        while ( (entry = jis.getNextJarEntry()) != null ) {
            if ( !entry.isDirectory() ) {
                if ( entry.getName().endsWith( ".class" ) && !entry.getName().endsWith( "package-info.class" ) ) {
                    String fullyQualifiedName = convertPathToName( entry.getName() );
                    if ( isClassVisible( cl,
                                         fullyQualifiedName,
                                         assetPackageName ) ) {
                        String leafName = getLeafName( fullyQualifiedName );
                        if ( !nonCollidingImports.containsKey( leafName ) ) {
                            nonCollidingImports.put( leafName,
                                                     fullyQualifiedName.replaceAll( "\\$",
                                                                                    "." ) );
                        }
                    }
                }
            }
        }

        //Build list of imports
        for ( String value : nonCollidingImports.values() ) {
            String line = "import " + value;
            imports.add( line );
        }

        return imports;
    }

    private String getLeafName(String fullyQualifiedName) {
        int index = fullyQualifiedName.lastIndexOf( "." );
        if ( index == -1 ) {
            return fullyQualifiedName;
        }
        return fullyQualifiedName.substring( index + 1 );
    }

    //Only import public classes; or those in the same package as the Asset
    private boolean isClassVisible(ClassLoader cl,
                                   String className,
                                   String assetPackageName) {
        try {
            Class< ? > cls = cl.loadClass( className );
            int modifiers = cls.getModifiers();
            if ( Modifier.isPublic( modifiers ) ) {
                return true;
            }
            String packageName = className.substring( 0,
                                                      className.lastIndexOf( "." ) );
            if ( !packageName.equals( assetPackageName ) ) {
                return false;
            }
        } catch ( Exception e ) {
            return false;
        }
        return true;
    }

    public static String convertPathToName(String name) {
        String convertedName = name.replace( ".class",
                                             "" );
        convertedName = convertedName.replace( "/",
                                               "." );
        convertedName = convertedName.replaceAll( "\\$",
                                                  "." );
        return convertedName;
    }

}
