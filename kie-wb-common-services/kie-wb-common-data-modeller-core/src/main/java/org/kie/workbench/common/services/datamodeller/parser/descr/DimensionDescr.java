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

public class DimensionDescr extends ElementDescriptor {

    public DimensionDescr( ) {
        super( ElementType.DIMENSION );
    }

    public DimensionDescr( String text, int start, int line, int position ) {
        this( text, start, -1, line, position );
    }

    public DimensionDescr( String text, int start, int stop ) {
        this( text, start, stop, -1, -1 );
    }

    public DimensionDescr( String text, int start, int stop, int line, int position ) {
        super( ElementType.DIMENSION, text, start, stop, line, position );
    }

    public DimensionDescr( String text, int start, int stop, int line, int position, JavaTokenDescr startBracket, JavaTokenDescr endBracket ) {
        super( ElementType.DIMENSION, text, start, stop, line, position );
        setStartBracket( startBracket );
        setEndBracket( endBracket );
    }

    public JavaTokenDescr getStartBracket( ) {
        return ( JavaTokenDescr ) getElements( ).getFirst( ElementType.JAVA_LBRACKET );
    }

    public DimensionDescr setStartBracket( JavaTokenDescr startBracket ) {
        getElements( ).removeFirst( ElementType.JAVA_LBRACKET );
        getElements( ).add( startBracket );
        return this;
    }

    public JavaTokenDescr getEndBracket( ) {
        return ( JavaTokenDescr ) getElements( ).getFirst( ElementType.JAVA_RBRACKET );
    }

    public DimensionDescr setEndBracket( JavaTokenDescr endBracket ) {
        getElements( ).removeFirst( ElementType.JAVA_RBRACKET );
        getElements( ).add( endBracket );
        return this;
    }
}