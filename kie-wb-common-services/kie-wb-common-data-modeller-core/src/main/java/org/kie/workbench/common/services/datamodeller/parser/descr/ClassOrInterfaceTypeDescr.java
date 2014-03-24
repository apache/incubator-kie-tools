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

public class ClassOrInterfaceTypeDescr extends ElementDescriptor {

    public ClassOrInterfaceTypeDescr( ) {
        super( ElementType.CLASS_OR_INTERFACE_TYPE );
    }

    public ClassOrInterfaceTypeDescr( String text, int start, int stop, int line, int position ) {
        super( ElementType.CLASS_OR_INTERFACE_TYPE, text, start, stop, line, position );
    }

    public ClassOrInterfaceTypeDescr( String text, int start, int stop ) {
        this( text, start, stop, -1, -1 );
    }

    public void addIdentifierWithTypeArgument( IdentifierWithTypeArgumentsDescr identifierWithTypeArgumentsDescr ) {
        getElements( ).add( identifierWithTypeArgumentsDescr );
    }

    public List<IdentifierWithTypeArgumentsDescr> getIdentifierWithTypeArguments( ) {
        List<IdentifierWithTypeArgumentsDescr> identifiers = new ArrayList<IdentifierWithTypeArgumentsDescr>( );
        for ( ElementDescriptor member : getElements( ).getElementsByType( ElementType.IDENTIFIER_WITH_TYPE_ARGUMENTS ) ) {
            identifiers.add( ( IdentifierWithTypeArgumentsDescr ) member );
        }
        return identifiers;
    }

    public String getClassName( ) {
        return getText( );
    }

}
