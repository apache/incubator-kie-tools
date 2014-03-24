/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.services.datamodeller.parser.descr;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO next version should be improved dot chars are not being stored '.'.
 */
public class ImportDescr extends ElementDescriptor {

    public ImportDescr( ) {
        super( ElementType.IMPORT );
    }

    public ImportDescr( String text, int start, int line, int position ) {
        this( text, start, -1, line, position );
    }

    public ImportDescr( String text, int start, int stop ) {
        this( text, start, stop, -1, -1 );
    }

    public ImportDescr( String text, int start, int stop, int line, int position ) {
        super( ElementType.IMPORT, text, start, stop, line, position );
    }

    public boolean isStaticImport( ) {
        return getStaticToken( ) != null;
    }

    public boolean isStarImport( ) {
        return getStarToken( ) != null;
    }

    public JavaTokenDescr getImportToken( ) {
        return ( JavaTokenDescr ) getElements( ).getFirst( ElementType.JAVA_IMPORT );
    }

    public void setImportToken( JavaTokenDescr importToken ) {
        getElements( ).removeFirst( ElementType.JAVA_IMPORT );
        getElements( ).add( importToken );
    }

    public JavaTokenDescr getStaticToken( ) {
        return ( JavaTokenDescr ) getElements( ).getFirst( ElementType.JAVA_STATIC );
    }

    public void setStaticToken( JavaTokenDescr staticToken ) {
        getElements( ).removeFirst( ElementType.JAVA_STATIC );
        getElements( ).add( staticToken );
    }

    public void addPart( IdentifierDescr identifierDescr ) {
        getElements( ).add( identifierDescr );
    }

    public List<IdentifierDescr> getParts( ) {
        List<IdentifierDescr> identifiers = new ArrayList<IdentifierDescr>( );
        for ( ElementDescriptor identifier : getElements( ).getElementsByType( ElementType.IDENTIFIER ) ) {
            identifiers.add( ( IdentifierDescr ) identifier );
        }
        return identifiers;
    }

    public void setStarToken( JavaTokenDescr starToken ) {
        getElements( ).removeFirst( ElementType.JAVA_STAR );
        getElements( ).add( starToken );
    }

    public JavaTokenDescr getStarToken( ) {
        return ( JavaTokenDescr ) getElements( ).getFirst( ElementType.JAVA_STAR );
    }

    public JavaTokenDescr getEndSemiColon( ) {
        return ( JavaTokenDescr ) getElements( ).getLast( ElementType.JAVA_SEMI_COLON );
    }

    public ImportDescr setEndSemiColon( JavaTokenDescr element ) {
        getElements( ).removeFirst( ElementType.JAVA_SEMI_COLON );
        getElements( ).add( element );
        return this;
    }

    public String getName( boolean includeStar ) {
        StringBuilder nameBuilder = new StringBuilder( );
        boolean first = true;
        for ( IdentifierDescr identifier : getParts( ) ) {
            if ( !first ) {
                nameBuilder.append( "." );
            }
            nameBuilder.append( identifier.getIdentifier( ) );
            first = false;
        }
        if ( !first && includeStar && isStarImport( ) ) {
            nameBuilder.append( ".*" );
        }
        return nameBuilder.toString( );
    }

}
