/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.backend.definition.factory;

import org.kie.workbench.common.stunner.core.backend.util.BackendBindableDefinitionUtils;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.factory.definition.AbstractTypeDefinitionFactory;

import java.util.Set;

/**
 * Model factory for annotated modelsfor using  on test scope.
 */
public class TestScopeModelFactory extends AbstractTypeDefinitionFactory<Object> {

    private final Object definitionSet;

    public TestScopeModelFactory( Object definitionSet ) {
        this.definitionSet = definitionSet;
    }

    private static Set<Class<? extends Object>> getDefinitions( Object defSet ) {
        return BackendBindableDefinitionUtils.getDefinitions( defSet );
    }

    @Override
    public Set<Class<? extends Object>> getAcceptedClasses() {
        return getDefinitions( this.definitionSet );
    }

    @Override
    public Object build( Class<? extends Object> clazz ) {
        Builder<?> builder = newDefinitionBuilder( clazz );
        return builder.build();
    }

    private Builder<?> newDefinitionBuilder( Class<? extends Object> definitionClass ) {
        Class<? extends Builder<?>> builderClass = getDefinitionBuilderClass( definitionClass );
        if ( null != builderClass ) {
            try {
                return builderClass.newInstance();

            } catch ( InstantiationException e ) {
                e.printStackTrace();

            } catch ( IllegalAccessException e ) {
                e.printStackTrace();

            }

        }
        throw new RuntimeException( "No annotated builder found for Definition [" + definitionClass.getName() + "]" );
    }

    private Class<? extends Builder<?>> getDefinitionBuilderClass( Class<? extends Object> definitionClass ) {
        if ( null != definitionClass ) {
            Definition annotation = definitionClass.getAnnotation( Definition.class );
            if ( null != annotation ) {
                return annotation.builder();

            }
        }
        return null;
    }

}
