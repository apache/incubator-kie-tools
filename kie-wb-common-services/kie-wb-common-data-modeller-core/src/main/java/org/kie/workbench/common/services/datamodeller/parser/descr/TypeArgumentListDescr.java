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

public class TypeArgumentListDescr extends ElementDescriptor {

    public TypeArgumentListDescr( ) {
        super( ElementType.TYPE_ARGUMENT_LIST );
    }

    public TypeArgumentListDescr( String text, int start, int line, int position ) {
        this( text, start, -1, line, position );
    }

    public TypeArgumentListDescr( String text, int start, int stop ) {
        this( text, start, stop, -1, -1 );
    }

    public TypeArgumentListDescr( String text, int start, int stop, int line, int position ) {
        super( ElementType.TYPE_ARGUMENT_LIST, text, start, stop, line, position );
    }

    public JavaTokenDescr getLTStart( ) {
        return ( JavaTokenDescr ) getElements( ).getFirst( ElementType.JAVA_LT );
    }

    public TypeArgumentListDescr setLTStart( JavaTokenDescr start ) {
        getElements( ).removeFirst( ElementType.JAVA_LT );
        getElements( ).add( 0, start );
        return this;
    }

    public JavaTokenDescr getGTStop( ) {
        return ( JavaTokenDescr ) getElements( ).getFirst( ElementType.JAVA_GT );
    }

    public TypeArgumentListDescr setGTStop( JavaTokenDescr stop ) {
        getElements( ).removeFirst( ElementType.JAVA_GT );
        getElements( ).add( stop );
        return this;
    }

    public void addArgument( TypeArgumentDescr argument ) {
        getElements( ).add( argument );
    }

    public List<TypeArgumentDescr> getArguments() {
        List<TypeArgumentDescr> arguments = new ArrayList<TypeArgumentDescr>(  );
        for (ElementDescriptor element : getElements().getElementsByType( ElementType.TYPE_ARGUMENT )) {
            arguments.add( (TypeArgumentDescr) element );
        }
        return arguments;
    }

}
