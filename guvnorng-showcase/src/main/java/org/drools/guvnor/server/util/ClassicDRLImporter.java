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

package org.drools.guvnor.server.util;

import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.lang.descr.RuleDescr;
import org.drools.repository.utils.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class imports legacy DRL into a structure suitable for storing more
 * normalised in the repository.
 */
public class ClassicDRLImporter {

    private String               source;

    private String               packageName;

    private static final Pattern functionPattern            = Pattern.compile( "function\\s+.*\\s+(.*)\\(.*\\).*" );

    private static final Pattern declarationNamePattern     = Pattern.compile( "declare\\s+(\\w+)\\s*.*" );

    //Single line declarations
    private static final Pattern declarationCompletePattern = Pattern.compile( "declare\\s+(\\w+).*\\s+end" );

    private static final Pattern endPattern                 = Pattern.compile( "\\s*end" );

    private final List<Asset>    declaredTypes              = new ArrayList<Asset>();

    private final List<Asset>    assets                     = new ArrayList<Asset>();

    private StringBuilder        header;

    private boolean              usesDSL;

    /**
     * @param in will be closed after it's read
     */
    public ClassicDRLImporter(InputStream in) throws DroolsParserException {
        BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
        try {
            StringBuilder drl = new StringBuilder();
            String line;
            while ( (line = reader.readLine()) != null ) {
                drl.append( "\n" ).append( line );
            }
            this.source = drl.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read DRL inputStream.", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }

        parse();
    }

    private void parse() throws DroolsParserException {
        StringTokenizer lines = new StringTokenizer( source,
                                                     "\r\n" );

        header = new StringBuilder();
        while ( lines.hasMoreTokens() ) {
            String line = lines.nextToken().trim();

            if ( line.startsWith( "package" ) ) {
                packageName = getPackage( line );

            } else if ( line.startsWith( "rule" ) ) {
                String ruleName = getRuleName( line );
                StringBuilder currentRule = new StringBuilder();
                laConsumeToEnd( lines,
                                currentRule,
                                "end",
                                false );
                addRule( ruleName,
                         currentRule );

            } else if ( line.startsWith( "function" ) ) {
                String functionName = getFuncName( line );
                StringBuilder currentFunc = new StringBuilder();

                int counter = 0;

                currentFunc.append( line ).append( "\n" );

                counter = countBrackets( counter,
                                         line );

                if ( counter > 0 ) {
                    laConsumeBracketsToEnd( counter,
                                            lines,
                                            currentFunc );
                }
                addFunction( functionName,
                             currentFunc );

            } else if ( line.startsWith( "declare" ) ) {
                String declarationName = getDeclarationName( line );
                StringBuilder currentDeclaration = new StringBuilder();

                currentDeclaration.append( line ).append( "\n" );

                if ( !isDeclarationComplete( line ) ) {
                    laConsumeDeclarationToEnd( lines,
                                               currentDeclaration );
                }
                addDeclaredModel( declarationName,
                                  currentDeclaration );

            } else if ( line.startsWith( "/*" ) ) {

                StringBuilder comment = new StringBuilder();
                comment.append( line ).append( "\n" );
                laConsumeToEnd( lines,
                                comment,
                                "*/",
                                true );

                header.append( comment );

            } else if ( line.startsWith( "expander" ) ) {

                usesDSL = true;
            } else {

                header.append( line );
                header.append( "\n" );
            }
        }
        addDeclaredModels();
    }

    private void addFunction(String functionName,
                             StringBuilder currentFunc) {
        this.assets.add( new Asset( functionName,
                                    currentFunc.toString(),
                                    AssetFormats.FUNCTION ) );
    }

    //Add a single Type to the collection from which a Declarative Model will be composed
    private void addDeclaredModel(String declarationName,
                                  StringBuilder currentDeclaration) {
        this.declaredTypes.add( new Asset( declarationName,
                                           currentDeclaration.toString(),
                                           AssetFormats.DRL_MODEL ) );
    }

    //Add a new Declarative Model asset for all Types identified
    private void addDeclaredModels() {
        final StringBuilder types = new StringBuilder();
        if ( this.declaredTypes.size() > 0 ) {
            for ( Asset declaredType : this.declaredTypes ) {
                types.append( declaredType.content );
            }
            this.assets.add( new Asset( "model",
                                        types.toString(),
                                        AssetFormats.DRL_MODEL ) );
        }
    }

    private String getFuncName(String line) {
        Matcher m = functionPattern.matcher( line );
        m.matches();
        return m.group( 1 );
    }

    private String getDeclarationName(String line) {
        Matcher m = declarationNamePattern.matcher( line );
        m.matches();
        return m.group( 1 );
    }

    private boolean isDeclarationComplete(String line) {
        Matcher m = declarationCompletePattern.matcher( line );
        return m.matches();
    }

    private boolean isEnd(String line) {
        Matcher m = endPattern.matcher( line );
        return m.matches();
    }

    /**
     * Consumes function to the ending curly bracket.
     * 
     * @param lines
     * @param currentFunc
     */
    private void laConsumeBracketsToEnd(int counter,
                                        StringTokenizer lines,
                                        StringBuilder currentFunc) {
        /*
         * Check if the first line contains matching amount of brackets.
         */
        boolean multilineIsOpen = false;
        // Start counting brackets
        while ( lines.hasMoreTokens() ) {
            String line = lines.nextToken();

            currentFunc.append( line );
            currentFunc.append( "\n" );

            if ( multilineIsOpen ) {
                int commentEnd = line.indexOf( "*/" );

                if ( commentEnd != -1 ) {
                    multilineIsOpen = false;
                    line = line.substring( commentEnd );
                }
            } else {
                multilineIsOpen = checkIfMultilineCommentStarts( line );
                line = removeComments( line );
            }

            if ( !multilineIsOpen ) {
                counter = countBrackets( counter,
                                         line );
            }

            if ( counter == 0 ) {
                break;
            }
        }
    }

    /**
     * Consumes Declaration to the "end".
     * 
     * @param lines
     * @param currentFunc
     */
    private void laConsumeDeclarationToEnd(StringTokenizer lines,
                                           StringBuilder currentDeclaration) {
        boolean multilineIsOpen = false;
        while ( lines.hasMoreTokens() ) {
            String line = lines.nextToken();
            currentDeclaration.append( removeComments( line ) );
            currentDeclaration.append( "\n" );

            if ( multilineIsOpen ) {
                int commentEnd = line.indexOf( "*/" );

                if ( commentEnd != -1 ) {
                    multilineIsOpen = false;
                    line = line.substring( commentEnd );
                }
            } else {
                multilineIsOpen = checkIfMultilineCommentStarts( line );
                line = removeComments( line );
            }
            if ( isEnd( line ) ) {
                break;
            }
        }
    }

    /**
     * @param line
     * @return
     */
    private boolean checkIfMultilineCommentStarts(String line) {

        int commentMultiLineStart = line.indexOf( "/*" );
        int commentMultiLineEnd = line.indexOf( "*/" );
        //        int commentSingleLine = line.indexOf( "//" );

        return commentMultiLineStart != -1 && commentMultiLineEnd == -1;
    }

    private int countBrackets(int counter,
                              String line) {
        char[] chars = line.toCharArray();
        for ( char aChar : chars ) {
            if ( aChar == '{' ) {
                counter++;
            } else if ( aChar == '}' ) {
                counter--;
            }
        }
        return counter;
    }

    private String removeComments(String line) {

        int commentMultiLineStart = line.indexOf( "/*" );
        int commentMultiLineEnd = line.indexOf( "*/" );
        int commentSingleLine = line.indexOf( "//" );

        // Single line comment is first
        // Case: some code // /* */
        // Another case: some code // No comments
        if ( commentSingleLine != -1 && commentMultiLineStart > commentSingleLine ) {
            return line.substring( 0,
                                   commentSingleLine );
        }

        // There is only a start for the multiline comment.
        // Case: some code here /* commented out
        if ( commentMultiLineStart != -1 && commentMultiLineEnd == -1 ) {
            return line.substring( 0,
                                   commentMultiLineStart );
        }

        // Two ends are on the same line
        // some code /* comment */ some code
        if ( commentMultiLineStart != -1 && commentMultiLineEnd != -1 ) {

            line = line.substring( 0,
                                   commentMultiLineStart ) + line.substring( commentMultiLineEnd + "*/".length() );
            return line;
        }

        return line;
    }

    private void laConsumeToEnd(StringTokenizer lines,
                                StringBuilder currentRule,
                                String end,
                                boolean addLastLine) {
        String line;
        while ( lines.hasMoreTokens() ) {
            line = lines.nextToken();
            if ( line.trim().startsWith( end ) ) {
                if ( addLastLine ) {
                    currentRule.append( line ).append( "\n" );
                }
                break;
            }
            currentRule.append( line );
            currentRule.append( "\n" );
        }
    }

    private void addRule(String ruleName,
                         StringBuilder currentRule) {
        ruleName = ruleName.replace( '\'',
                                     ' ' );
        if ( this.isDSLEnabled() ) {
            this.assets.add( new Asset( ruleName,
                                        currentRule.toString(),
                                        AssetFormats.DSL_TEMPLATE_RULE ) );
        } else {
            this.assets.add( new Asset( ruleName,
                                        currentRule.toString(),
                                        AssetFormats.DRL ) );
        }
    }

    /**
     * Get the rule name from a declaration line
     */
    public static String getRuleName(String line) throws DroolsParserException {
        DrlParser parser = new DrlParser();
        line = line + "\n when\n then \n end";
        RuleDescr rule = parser.parse( line ).getRules().get( 0 );
        return rule.getName();
    }

    private String getPackage(String line) throws DroolsParserException {
        DrlParser parser = new DrlParser();
        return parser.parse( line ).getName();

    }

    public List<Asset> getAssets() {
        return this.assets;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getPackageHeader() {
        return this.header.toString();
    }

    public boolean isDSLEnabled() {
        return this.usesDSL;
    }

    /**
     * Holds a rule to import. The content does not include the "end".
     */
    public static class Asset {

        public Asset(String name,
                     String content,
                     String format) {
            this.name = name;
            this.content = content;
            this.format = format;
        }

        public final String format;
        public final String name;
        public final String content;
    }

    /**
     * This merges the toMerge new schtuff into the existing. Line by line,
     * simple stuff.
     */
    @SuppressWarnings("rawtypes")
    public static String mergeLines(String existing,
                                    String toMerge) {

        if ( toMerge == null || toMerge.equals( "" ) ) {
            return existing;
        }
        if ( existing == null || existing.equals( "" ) ) {
            return toMerge;
        }
        Set existingLines = new HashSet<String>( Arrays.asList( existing.split( "\n" ) ) );
        String[] newLines = toMerge.split( "\n" );
        for ( String newLine1 : newLines ) {
            String newLine = newLine1.trim();

            if ( !newLine.equals( "" ) && !existingLines.contains( newLine1.trim() ) ) {
                existing = existing + "\n" + newLine1;
            }
        }
        return existing;

    }

}
