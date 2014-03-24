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

public class TypeArgumentDescr extends ElementDescriptor implements HasType {

    public TypeArgumentDescr( ) {
        super( ElementType.TYPE_ARGUMENT );
    }

    public TypeArgumentDescr( String text, int start, int stop, int line, int position ) {
        super( ElementType.TYPE_ARGUMENT, text, start, stop, line, position );
    }

    public TypeArgumentDescr( String text, int start, int stop ) {
        this( text, start, stop, -1, -1 );
    }

    public TypeDescr getType( ) {
        return ( TypeDescr ) getElements( ).getFirst( ElementType.TYPE );
    }

    public TypeArgumentDescr setType( TypeDescr type ) {
        getElements( ).removeFirst( ElementType.TYPE );
        getElements( ).add( type );
        return this;
    }

    public TypeArgumentDescr setStartComma( JavaTokenDescr comma ) {
        getElements( ).removeFirst( ElementType.JAVA_COMMA );
        getElements( ).add( 0, comma );
        return this;
    }
}
