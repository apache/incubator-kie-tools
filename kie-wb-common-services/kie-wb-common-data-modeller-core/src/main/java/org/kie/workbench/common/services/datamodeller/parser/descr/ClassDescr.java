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

public class ClassDescr extends ModifiersContainerDescr {

    /*
    //TODO add:
    //List of implemented interfaces
    //Type annotations.
    */

    public ClassDescr( ) {
        super( ElementType.CLASS );
    }

    public ClassDescr( String text, int start, int line, int position ) {
        this( text, start, -1, line, position );
    }

    public ClassDescr( String text, int start, int stop ) {
        this( text, start, stop, -1, -1 );
    }

    public ClassDescr( String text, int start, int stop, int line, int position ) {
        super( ElementType.CLASS, text, start, stop, line, position );
    }

    public String getName( ) {
        return getIdentifier( ) != null ? getIdentifier( ).getIdentifier( ) : null;
    }

    public IdentifierDescr getIdentifier( ) {
        return ( IdentifierDescr ) getElements( ).getFirst( ElementType.IDENTIFIER );
    }

    public ClassDescr setIdentifier( IdentifierDescr identifier ) {
        getElements( ).removeFirst( ElementType.IDENTIFIER );
        getElements( ).add( identifier );
        return this;
    }

    public void addMember( ElementDescriptor member ) {
        getElements( ).add( member );
    }

    public List<ElementDescriptor> getMembers( ) {
        return getElements( );
    }

    public void addField( FieldDescr fieldDescr ) {
        int index = getElements( ).lastIndexOf( ElementType.FIELD );
        getElements( ).add( index + 1, fieldDescr );
    }

    public void addMethod( MethodDescr methodDescr ) {
        int index = getElements( ).lastIndexOf( ElementType.METHOD );
        getElements( ).add( index + 1, methodDescr );
    }

    public List<MethodDescr> getMethods( ) {
        List<MethodDescr> methods = new ArrayList<MethodDescr>( );
        for ( ElementDescriptor member : getElements( ).getElementsByType( ElementType.METHOD ) ) {
            methods.add( ( MethodDescr ) member );
        }
        return methods;
    }

    public MethodDescr getMethod( String methodIdentifier ) {
        if ( methodIdentifier == null ) {
            return null;
        }

        List<MethodDescr> methods = getMethods( );
        IdentifierDescr identifier;
        for ( MethodDescr method : methods ) {
            identifier = method.getIdentifier( );
            if ( identifier != null && methodIdentifier.equals( identifier.getIdentifier( ) ) ) {
                return method;
            }
        }
        return null;
    }

    public List<FieldDescr> getFields( ) {
        List<FieldDescr> fields = new ArrayList<FieldDescr>( );
        for ( ElementDescriptor member : getElements( ).getElementsByType( ElementType.FIELD ) ) {
            fields.add( ( FieldDescr ) member );
        }
        return fields;
    }

    public FieldDescr getField( String name ) {
        if ( name == null ) {
            return null;
        }
        List<FieldDescr> fields = getFields( );
        for ( FieldDescr field : fields ) {
            if ( field.getVariableDeclaration( name ) != null ) {
                return field;
            }
        }
        return null;
    }

    public boolean removeField( FieldDescr field ) {
        return getElements( ).remove( field );
    }

    public boolean removeField( String name ) {
        FieldDescr field = getField( name );
        boolean result = false;
        if ( field != null ) {
            if ( field.getVariableDeclarations( ).size( ) <= 1 ) {
                result = getElements( ).remove( field );
            } else {
                VariableDeclarationDescr variable = field.getVariableDeclaration( name );
                result = variable != null ? field.removeVariableDeclaration( variable ) : false;
                if ( result ) {
                    variable = field.getVariableDeclarations( ).get( 0 );
                    if ( variable.getStartComma( ) != null ) {
                        variable.getElements( ).remove( variable.getStartComma( ) );
                    }
                }
            }
        }
        return result;
    }

    public JavaTokenDescr getClassToken( ) {
        return ( JavaTokenDescr ) getElements( ).getFirst( ElementType.JAVA_CLASS );
    }

    public void setClassToken( JavaTokenDescr classToken ) {
        getElements( ).removeFirst( ElementType.JAVA_CLASS );
        getElements( ).add( classToken );
    }

    public JavaTokenDescr getExtendsToken( ) {
        return ( JavaTokenDescr ) getElements( ).getFirst( ElementType.JAVA_EXTENDS );
    }

    public void setExtendsToken( JavaTokenDescr extendsToken ) {
        getElements( ).removeFirst( ElementType.JAVA_EXTENDS );
        getElements( ).add( extendsToken );
    }

    public TypeDescr getSuperClass( ) {
        return ( TypeDescr ) getElements( ).getFirst( ElementType.TYPE );
    }

    public void setSuperClass( TypeDescr superClass ) {
        getElements( ).removeFirst( ElementType.TYPE );
        getElements( ).add( superClass );
    }

    public JavaTokenDescr getImplementsToken( ) {
        return ( JavaTokenDescr ) getElements( ).getFirst( ElementType.JAVA_IMPLEMENTS );
    }

    public void setImplementsToken( JavaTokenDescr implementsToken ) {
        getElements( ).removeFirst( ElementType.JAVA_IMPLEMENTS );
        getElements( ).add( implementsToken );
    }

}