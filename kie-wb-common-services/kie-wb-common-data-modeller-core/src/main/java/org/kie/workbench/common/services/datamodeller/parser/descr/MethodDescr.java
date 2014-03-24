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

import java.util.List;

public class MethodDescr extends ModifiersContainerDescr implements HasDimensions, HasType {

    public MethodDescr( ) {
        super( ElementType.METHOD );
    }

    public MethodDescr( String text, int start, int line, int position ) {
        this( text, start, -1, line, position );
    }

    public MethodDescr( String text, int start, int stop ) {
        this( text, start, stop, -1, -1 );
    }

    public MethodDescr( String text, int start, int stop, int line, int position ) {
        super( ElementType.METHOD, text, start, stop, line, position );
    }

    public IdentifierDescr getIdentifier( ) {
        return ( IdentifierDescr ) getElements( ).getFirst( ElementType.IDENTIFIER );
    }

    public MethodDescr setIdentifier( IdentifierDescr identifier ) {
        getElements( ).removeFirst( ElementType.IDENTIFIER );
        getElements( ).add( identifier );
        return this;
    }

    public boolean isConstructor( ) {
        return getType( ) == null;
    }

    @Override
    public int getDimensionsCount( ) {
        List<ElementDescriptor> dimensions = getElements( ).getElementsByType( ElementType.DIMENSION );
        return dimensions.size( );
    }

    @Override
    public MethodDescr addDimension( DimensionDescr dimensionDescr ) {
        getElements( ).add( dimensionDescr );
        return this;
    }

    public TypeDescr getType( ) {
        return ( TypeDescr ) getElements( ).getFirst( ElementType.TYPE );
    }

    public MethodDescr setType( TypeDescr type ) {
        getElements( ).removeFirst( ElementType.TYPE );
        getElements( ).add( type );
        return this;
    }

    public ParameterListDescr getParamsList( ) {
        return ( ParameterListDescr ) getElements( ).getFirst( ElementType.PARAMETER_LIST );
    }

    public void setParamsList( ParameterListDescr params ) {
        getElements( ).removeFirst( ElementType.PARAMETER_LIST );
        getElements( ).add( params );
    }

    public JavaTokenDescr getParamsStartParen( ) {
        return ( JavaTokenDescr ) getElements( ).getFirst( ElementType.JAVA_LPAREN );
    }

    public void setParamsStartParen( JavaTokenDescr paramsStart ) {
        getElements( ).removeFirst( ElementType.JAVA_LPAREN );
        getElements( ).add( paramsStart );
    }

    public JavaTokenDescr getParamsStopParen( ) {
        return ( JavaTokenDescr ) getElements( ).getFirst( ElementType.JAVA_RPAREN );
    }

    public void setParamsStopParen( JavaTokenDescr paramsStop ) {
        getElements( ).removeFirst( ElementType.JAVA_RPAREN );
        getElements( ).add( paramsStop );
    }
}
