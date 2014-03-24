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

public class ParameterListDescr extends ElementDescriptor {

    public ParameterListDescr( ) {
        super( ElementType.PARAMETER_LIST );
    }

    public ParameterListDescr( String text, int start, int line, int position ) {
        this( text, start, -1, line, position );
    }

    public ParameterListDescr( String text, int start, int stop ) {
        this( text, start, stop, -1, -1 );
    }

    public ParameterListDescr( String text, int start, int stop, int line, int position ) {
        super( ElementType.PARAMETER_LIST, text, start, stop, line, position );
    }

    public ParameterListDescr addParameter( ParameterDescr param ) {
        getElements( ).add( param );
        return this;
    }

    public List<ParameterDescr> getParameters( ) {
        List<ParameterDescr> params = new ArrayList<ParameterDescr>( );
        for ( ElementDescriptor element : getElements( ) ) {
            if ( ElementType.NORMAL_PARAMETER == element.getElementType( ) || ElementType.ELLIPSIS_PARAMETER == element.getElementType( ) ) {
                params.add( ( ParameterDescr ) element );

            }
        }
        return params;
    }
}
