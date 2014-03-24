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

public class IdentifierWithTypeArgumentsDescr extends ElementDescriptor implements HasTypeArguments {

    public IdentifierWithTypeArgumentsDescr( ) {
        super( ElementType.IDENTIFIER_WITH_TYPE_ARGUMENTS );
    }

    public IdentifierWithTypeArgumentsDescr( String text, int start, int line, int position ) {
        this( text, start, -1, line, position );
    }

    public IdentifierWithTypeArgumentsDescr( String text, int start, int stop ) {
        this( text, start, stop, -1, -1 );
    }

    public IdentifierWithTypeArgumentsDescr( String text, int start, int stop, int line, int position ) {
        super( ElementType.IDENTIFIER_WITH_TYPE_ARGUMENTS, text, start, stop, line, position );
    }

    @Override
    public TypeArgumentListDescr getArguments( ) {
        return ( TypeArgumentListDescr ) getElements( ).getFirst( ElementType.TYPE_ARGUMENT_LIST );
    }

    public IdentifierWithTypeArgumentsDescr setArguments( TypeArgumentListDescr arguments ) {
        getElements( ).removeFirst( ElementType.TYPE_ARGUMENT_LIST );
        getElements( ).add( arguments );
        return this;
    }

    public JavaTokenDescr getStartDot( ) {
        return ( JavaTokenDescr ) getElements( ).getFirst( ElementType.JAVA_DOT );
    }

    public IdentifierWithTypeArgumentsDescr setStartDot( JavaTokenDescr dot ) {
        getElements( ).removeFirst( ElementType.JAVA_DOT );
        getElements( ).add( 0, dot );
        return this;
    }

    public IdentifierDescr getIdentifier( ) {
        return ( IdentifierDescr ) getElements( ).getFirst( ElementType.IDENTIFIER );
    }

    public IdentifierWithTypeArgumentsDescr setIdentifier( IdentifierDescr identifier ) {
        getElements( ).removeFirst( ElementType.IDENTIFIER );
        getElements( ).add( identifier );
        return this;
    }

}
