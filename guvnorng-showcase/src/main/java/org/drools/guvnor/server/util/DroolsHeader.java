/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.server.util;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;

/**
 * Helper class for getting Drools Header
 */
public class DroolsHeader {

    private static final String  NL           = System.getProperty( "line.separator" );

    private static final Pattern globalFinder = Pattern.compile( "(^\\s*global.*?$)",
                                                                 Pattern.DOTALL | Pattern.MULTILINE );

    private static final Pattern importFinder = Pattern.compile( "(^\\s*import.*?$)",
                                                                 Pattern.DOTALL | Pattern.MULTILINE );

    public static String getDroolsHeader(ModuleItem pkg) {
        if ( pkg.containsAsset( "drools" ) ) {
            return pkg.loadAsset( "drools" ).getContent();
        }
        return "";
    }

    public static void updateDroolsHeader(String string,
                                          ModuleItem pkg) {
        pkg.checkout();
        AssetItem conf;
        if ( pkg.containsAsset( "drools" ) ) {
            conf = pkg.loadAsset( "drools" );
            conf.updateContent( string );

            conf.checkin( "" );
        } else {
            conf = pkg.addAsset( "drools",
                                 "" );
            conf.updateFormat( "package" );
            conf.updateContent( string );

            conf.checkin( "" );
        }
    }

    public static String getPackageHeaderImports(String packageHeader) {
        final StringBuilder drl = new StringBuilder();
        Scanner scanner = new Scanner( packageHeader );
        try {
            while ( scanner.hasNextLine() ) {
                final String line = scanner.nextLine();
                if ( isImport( line ) ) {
                    drl.append( line ).append( NL );
                }
            }
        } finally {
            scanner.close();
        }
        return drl.toString();
    }

    public static String getPackageHeaderGlobals(String packageHeader) {
        final StringBuilder drl = new StringBuilder();
        Scanner scanner = new Scanner( packageHeader );
        try {
            while ( scanner.hasNextLine() ) {
                final String line = scanner.nextLine();
                if ( isGlobal( line ) ) {
                    drl.append( line ).append( NL );
                }
            }
        } finally {
            scanner.close();
        }
        return drl.toString();
    }

    public static String getPackageHeaderMiscellaneous(String packageHeader) {
        final StringBuilder drl = new StringBuilder();
        Scanner scanner = new Scanner( packageHeader );
        try {
            while ( scanner.hasNextLine() ) {
                final String line = scanner.nextLine();
                if ( !isImport( line ) && !isGlobal( line ) ) {
                    drl.append( line ).append( NL );
                }
            }
        } finally {
            scanner.close();
        }
        return drl.toString();
    }

    private static boolean isImport(final String line) {
        Matcher gm = importFinder.matcher( line );
        return gm.find();
    }

    private static boolean isGlobal(final String line) {
        Matcher gm = globalFinder.matcher( line );
        return gm.find();
    }

}
