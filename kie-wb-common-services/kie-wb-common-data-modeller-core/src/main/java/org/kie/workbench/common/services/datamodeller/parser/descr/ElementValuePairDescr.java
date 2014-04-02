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

public class ElementValuePairDescr extends ElementDescriptor {

    public ElementValuePairDescr( ) {
        super( ElementType.ELEMENT_VALUE_PAIR );
    }

    public ElementValuePairDescr( String text, int start, int line, int position ) {
        this( text, start, -1, line, position );
    }

    public ElementValuePairDescr( String text, int start, int stop ) {
        this( text, start, stop, -1, -1 );
    }

    public ElementValuePairDescr( String text, int start, int stop, int line, int position ) {
        super( ElementType.ELEMENT_VALUE_PAIR, text, start, stop, line, position );
    }

    public JavaTokenDescr getStartComma( ) {
        return ( JavaTokenDescr ) getElements( ).getFirst( ElementType.JAVA_COMMA );
    }

    public ElementValuePairDescr setStartComma( JavaTokenDescr comma ) {
        getElements( ).removeFirst( ElementType.JAVA_COMMA );
        getElements( ).add( 0, comma );
        return this;
    }

    public IdentifierDescr getIdentifier( ) {
        return ( IdentifierDescr ) getElements( ).getFirst( ElementType.IDENTIFIER );
    }

    public ElementValuePairDescr setIdentifier( IdentifierDescr identifier ) {
        getElements( ).removeFirst( ElementType.IDENTIFIER );
        getElements( ).add( identifier );
        return this;
    }

    public JavaTokenDescr getEqualsSign( ) {
        return ( JavaTokenDescr ) getElements( ).getFirst( ElementType.JAVA_EQUALS );
    }

    public ElementValuePairDescr setEqualsSign( JavaTokenDescr equalsSign ) {
        getElements( ).removeFirst( ElementType.JAVA_EQUALS );
        getElements( ).add( equalsSign );
        return this;
    }

    public ElementValueDescr getValue() {
        return (ElementValueDescr) getElements().getFirst( ElementType.ELEMENT_VALUE );
    }

    public ElementValuePairDescr setValue(ElementValueDescr value) {
        getElements().removeFirst( ElementType.ELEMENT_VALUE );
        getElements().add( value );
        return this;
    }

}
