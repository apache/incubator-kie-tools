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

public class ElementDescriptor {

    public static enum ElementType {
        MODIFIER,
        FIELD,
        VARIABLE,
        VARIABLE_INITIALIZER,
        VARIABLE_STOP,
        METHOD,
        CLASS,
        PRIMITIVE_TYPE,
        CLASS_OR_INTERFACE_TYPE,
        IDENTIFIER_WITH_TYPE_ARGUMENTS,
        TYPE_ARGUMENT,
        TYPE,
        NORMAL_PARAMETER,
        ELLIPSIS_PARAMETER,
        PARAMETER_LIST,
        TEXT_TOKEN,
        TYPE_ARGUMENT_LIST,
        MODIFIER_LIST,
        SENTENCE,
        FILE,
        QUALIFIED_NAME,
        PACKAGE,
        IMPORT,
        IDENTIFIER,
        DIMENSION,
        JAVA_SEMI_COLON,
        JAVA_COMMA,
        JAVA_EQUALS,
        JAVA_LBRACKET,
        JAVA_RBRACKET,
        JAVA_DOT,
        JAVA_GT,
        JAVA_LT,
        JAVA_LPAREN,
        JAVA_RPAREN,
        JAVA_ELLIPSIS,
        JAVA_PACKAGE,
        JAVA_VOID,
        JAVA_IMPORT,
        JAVA_STATIC,
        JAVA_STAR,
        JAVA_CLASS,
        JAVA_EXTENDS,
        JAVA_IMPLEMENTS
    }

    private ElementType elementType;

    private int start;

    private int stop;

    private int line;

    private int position;

    private String text;

    private StringBuilder sourceBuffer = new StringBuilder( );

    protected ElementDescrList elements = new ElementDescrList( );

    public ElementDescriptor( ElementType elementType ) {
        this.elementType = elementType;
    }

    public ElementDescriptor( ElementType elementType, String text, int start, int line, int position ) {
        this.elementType = elementType;
        this.text = text;
        this.start = start;
        this.line = line;
        this.position = position;
    }

    public ElementDescriptor( ElementType elementType, String text, int start, int stop ) {
        this.elementType = elementType;
        this.text = text;
        this.start = start;
        this.stop = stop;
    }

    public ElementDescriptor( ElementType elementType, String text, int start, int stop, int line, int position ) {
        this.elementType = elementType;
        this.text = text;
        this.start = start;
        this.stop = stop;
        this.line = line;
        this.position = position;
    }

    public ElementType getElementType( ) {
        return elementType;
    }

    public void setElementType( ElementType elementType ) {
        this.elementType = elementType;
    }

    public int getStart( ) {
        return start;
    }

    public void setStart( int start ) {
        this.start = start;
    }

    public int getStop( ) {
        return stop;
    }

    public void setStop( int stop ) {
        this.stop = stop;
    }

    public int getLine( ) {
        return line;
    }

    public void setLine( int line ) {
        this.line = line;
    }

    public int getPosition( ) {
        return position;
    }

    public void setPosition( int position ) {
        this.position = position;
    }

    public String getText( ) {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }

    public boolean isElementType( ElementType elementType ) {
        return this.elementType == elementType;
    }

    public ElementDescrList getElements( ) {
        return elements;
    }

    public StringBuilder getSourceBuffer( ) {
        return sourceBuffer;
    }

    public void setSourceBuffer( StringBuilder sourceBuffer ) {
        this.sourceBuffer = sourceBuffer;
    }
}
