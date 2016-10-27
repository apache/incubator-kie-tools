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

package org.kie.workbench.common.stunner.core.client;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;

public abstract class AbstractBindableShapeSet implements ShapeSet<ShapeFactory<?, ?, ? extends Shape>> {

    protected abstract Class<?> getDefinitionSetClass();

    protected DefinitionManager definitionManager;

    protected String description;

    protected AbstractBindableShapeSet() {
    }

    public AbstractBindableShapeSet( final DefinitionManager definitionManager ) {
        this.definitionManager = definitionManager;
    }

    public void doInit() {
        final Object defSet = definitionManager.definitionSets().getDefinitionSetById( getDefinitionSetId() );
        this.description = definitionManager.adapters().forDefinitionSet().getDescription( defSet );

    }

    @Override
    public String getId() {
        return BindableAdapterUtils.getShapeSetId( getClass() );
    }

    @Override
    public String getName() {
        return this.description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getDefinitionSetId() {
        return BindableAdapterUtils.getDefinitionSetId( getDefinitionSetClass() );
    }

}
