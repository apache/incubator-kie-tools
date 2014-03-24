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

public class FieldDescr extends ModifiersContainerDescr implements HasType {

    public FieldDescr( ) {
        super( ElementType.FIELD );
    }

    public FieldDescr( String text, int start, int end ) {
        this( text, start, end, -1, -1 );
    }

    public FieldDescr( String text, int start, int stop, int line, int position ) {
        super( ElementType.FIELD, text, start, stop, line, position );
    }

    public TypeDescr getType( ) {
        return ( TypeDescr ) getElements( ).getFirst( ElementType.TYPE );
    }

    public FieldDescr setType( TypeDescr type ) {
        getElements( ).removeFirst( ElementType.TYPE );
        getElements( ).add( type );
        return this;
    }

    public List<VariableDeclarationDescr> getVariableDeclarations( ) {
        List<VariableDeclarationDescr> variableDeclarations = new ArrayList<VariableDeclarationDescr>( );
        for ( ElementDescriptor member : getElements( ).getElementsByType( ElementType.VARIABLE ) ) {
            variableDeclarations.add( ( VariableDeclarationDescr ) member );
        }
        return variableDeclarations;
    }

    public FieldDescr addVariableDeclaration( VariableDeclarationDescr variableDeclarationDescr ) {
        getElements( ).add( variableDeclarationDescr );
        return this;
    }

    public boolean removeVariableDeclaration( VariableDeclarationDescr variableDeclarationDescr ) {
        return getElements( ).remove( variableDeclarationDescr );
    }

    public JavaTokenDescr getEndSemiColon( ) {
        return ( JavaTokenDescr ) getElements( ).getLast( ElementType.JAVA_SEMI_COLON );
    }

    public FieldDescr setEndSemiColon( JavaTokenDescr element ) {
        getElements( ).removeFirst( ElementType.JAVA_SEMI_COLON );
        getElements( ).add( element );
        return this;
    }

    public VariableDeclarationDescr getVariableDeclaration( String name ) {
        if ( name == null ) {
            return null;
        }
        IdentifierDescr identifier;
        for ( VariableDeclarationDescr variable : getVariableDeclarations( ) ) {
            identifier = variable.getIdentifier( );
            if ( identifier != null && name.equals( identifier.getIdentifier( ) ) ) {
                return variable;
            }
        }
        return null;
    }
}
