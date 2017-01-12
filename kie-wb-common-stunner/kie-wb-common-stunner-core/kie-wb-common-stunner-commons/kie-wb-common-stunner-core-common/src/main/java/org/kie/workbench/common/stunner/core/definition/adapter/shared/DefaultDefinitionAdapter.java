/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.definition.adapter.shared;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.impl.DefinitionImpl;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;

// TODO
@ApplicationScoped
public class DefaultDefinitionAdapter implements DefinitionAdapter<DefinitionImpl> {

    public DefaultDefinitionAdapter() {
    }

    @Override
    public String getId( final DefinitionImpl pojo ) {
        return pojo.getId();
    }

    @Override
    public String getCategory( final DefinitionImpl pojo ) {
        return pojo.getCategory();
    }

    @Override
    public String getTitle( final DefinitionImpl pojo ) {
        return pojo.getTitle();
    }

    @Override
    public String getDescription( final DefinitionImpl pojo ) {
        return pojo.getDescription();
    }

    @Override
    public Set<String> getLabels( final DefinitionImpl pojo ) {
        return pojo.getLabels();
    }

    @Override
    public Set<?> getPropertySets( final DefinitionImpl pojo ) {
        return pojo.getPropertySets();
    }

    @Override
    public Set<?> getProperties( final DefinitionImpl pojo ) {
        return pojo.getProperties();
    }

    @Override
    public Object getMetaProperty( final PropertyMetaTypes metaType,
                                   final DefinitionImpl pojo ) {
        return null;
    }

    @Override
    public Class<? extends ElementFactory> getGraphFactoryType( final DefinitionImpl pojo ) {
        // TODO
        return null;
    }

    @Override
    public boolean accepts( final Class<?> pojo ) {
        return pojo.getName().equals( DefinitionImpl.class.getName() );
    }

    @Override
    public boolean isPojoModel() {
        return false;
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
