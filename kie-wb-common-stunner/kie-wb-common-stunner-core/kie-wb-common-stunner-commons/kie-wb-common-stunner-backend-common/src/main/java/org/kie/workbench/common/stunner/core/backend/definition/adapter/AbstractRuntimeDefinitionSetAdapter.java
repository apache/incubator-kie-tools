/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.backend.definition.adapter;

import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.annotation.DefinitionSet;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractRuntimeDefinitionSetAdapter<T> extends AbstractRuntimeAdapter<T>
        implements DefinitionSetAdapter<T> {

    protected Set<String> getAnnotatedDefinitions( T definitionSet ) {
        Set<String> result = null;
        if ( null != definitionSet ) {
            DefinitionSet annotation = definitionSet.getClass().getAnnotation( DefinitionSet.class );
            if ( null != annotation ) {
                Class<?>[] definitions = annotation.definitions();
                if ( definitions.length > 0 ) {
                    result = new HashSet<String>( definitions.length );
                    for ( Class<?> defClass : definitions ) {
                        result.add( BindableAdapterUtils.getDefinitionSetId( defClass ) );
                    }
                }
            }

        }
        return result;
    }

    @Override
    public String getId( T definitionSet ) {
        String defSetId = BindableAdapterUtils.getDefinitionSetId( definitionSet.getClass() );
        // Avoid weld proxy class names issues.
        if ( defSetId.contains( "$" ) ) {
            defSetId = defSetId.substring( 0, defSetId.indexOf( "$" ) );
        }
        return defSetId;
    }

    @Override
    public String getDomain( T definitionSet ) {
        String n = definitionSet.getClass().getName();
        return n.substring( n.lastIndexOf( "." ) + 1, n.length() );
    }

}
