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

public class AnnotationDescr extends ElementDescriptor {

    public AnnotationDescr( ) {
        super( ElementType.ANNOTATION );
    }

    public AnnotationDescr( String text, int start, int line, int position ) {
        this( text, start, -1, line, position );
    }

    public AnnotationDescr( String text, int start, int stop ) {
        this( text, start, stop, -1, -1 );
    }

    public AnnotationDescr( String text, int start, int stop, int line, int position ) {
        super( ElementType.ANNOTATION, text, start, stop, line, position );
    }

    public JavaTokenDescr getStartAt( ) {
        return ( JavaTokenDescr ) getElements( ).getFirst( ElementType.JAVA_AT );
    }

    public void setStartAt( JavaTokenDescr element ) {
        getElements( ).removeFirst( ElementType.JAVA_AT );
        getElements( ).add( 0, element );
    }

    public QualifiedNameDescr getQualifiedName( ) {
        return ( QualifiedNameDescr ) getElements( ).getFirst( ElementType.QUALIFIED_NAME );
    }

    public void setQualifiedName( QualifiedNameDescr qualifiedName ) {
        getElements( ).removeFirst( ElementType.QUALIFIED_NAME );
        getElements( ).add( qualifiedName );
    }

    public void setElementValue(ElementValueDescr elementValue) {
        getElements().removeFirst( ElementType.ELEMENT_VALUE );
        getElements().add( elementValue );
    }

    public ElementValueDescr getElementValue() {
        return (ElementValueDescr) getElements().getFirst( ElementType.ELEMENT_VALUE );
    }

    public void setElementValuePairs(ElementValuePairListDescr elementValuePairs) {
        getElements().removeFirst( ElementType.ELEMENT_VALUE_PAIR_LIST );
        getElements().add( elementValuePairs );
    }

    public ElementValuePairListDescr getElementValuePairs() {
        return (ElementValuePairListDescr) getElements().getFirst( ElementType.ELEMENT_VALUE_PAIR_LIST );
    }

    public boolean hasElementValue() {
        return getElementValue() != null;
    }

    public boolean hasElementValuePairs() {
        return getElementValuePairs() != null;
    }

    public boolean isMarker() {
        return !hasElementValue() && !hasElementValuePairs();
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
