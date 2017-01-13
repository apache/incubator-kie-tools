/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.shape.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;

public abstract class AbstractShapeDefFactory<W, V extends ShapeView, S extends Shape<V>, P extends ShapeDef<W>>
        extends AbstractBindableShapeFactory<W, S> implements ShapeDefFactory<W, AbstractCanvasHandler, S, P> {

    protected final Map<Class<?>, P> definitions = new HashMap<Class<?>, P>();

    protected final DefinitionManager definitionManager;
    protected final FactoryManager factoryManager;

    public AbstractShapeDefFactory(final DefinitionManager definitionManager,
                                   final FactoryManager factoryManager) {
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
    }

    public Set<Class<?>> getSupportedModelClasses() {
        return definitions.keySet();
    }

    @Override
    public String getDescription(final String definitionId) {
        final P proxy = getShapeDef(definitionId);
        // TODO: Avoid creating domain object instance here.
        final W tempObject = factoryManager.newDefinition(definitionId);
        return definitionManager.adapters().forDefinition().getDescription(tempObject);
    }

    @Override
    protected String getDescription(final Class<?> clazz) {
        final String id = getDefinitionId(clazz);
        return getDescription(id);
    }

    @Override
    public void addShapeDef(final Class<?> clazz,
                            final P proxy) {
        definitions.put(clazz,
                        proxy);
    }

    @SuppressWarnings("unchecked")
    public P getShapeDef(final Class<?> clazz) {
        return definitions.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public P getShapeDef(final String definitionId) {
        for (final Map.Entry<Class<?>, P> entry : definitions.entrySet()) {
            final String id = BindableAdapterUtils.getDefinitionId(entry.getKey());
            if (id.equals(definitionId)) {
                return entry.getValue();
            }
        }
        throw new RuntimeException("This factory should provide a def for Definition [" + definitionId + "]");
    }

    @Override
    public Glyph glyph(final String definitionId,
                       final double width,
                       final double height) {
        final Class<?> clazz = getDefinitionClass(definitionId);
        return glyph(clazz,
                     width,
                     height);
    }
}
