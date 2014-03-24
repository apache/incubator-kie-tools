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
public class QualifiedNameDescr extends ElementDescriptor {

    public QualifiedNameDescr( ) {
        super( ElementType.QUALIFIED_NAME );
    }

    public QualifiedNameDescr( String text, int start, int line, int position ) {
        this( text, start, -1, line, position );
    }

    public QualifiedNameDescr( String text, int start, int stop ) {
        this( text, start, stop, -1, -1 );
    }

    public QualifiedNameDescr( String text, int start, int stop, int line, int position ) {
        super( ElementType.QUALIFIED_NAME, text, start, stop, line, position );
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

    public String getName( ) {
        StringBuilder nameBuilder = new StringBuilder( );
        boolean first = true;
        for ( IdentifierDescr identifier : getParts( ) ) {
            if ( !first ) {
                nameBuilder.append( "." );
            }
            nameBuilder.append( identifier.getIdentifier( ) );
            first = false;
        }
        return nameBuilder.toString( );
    }
}