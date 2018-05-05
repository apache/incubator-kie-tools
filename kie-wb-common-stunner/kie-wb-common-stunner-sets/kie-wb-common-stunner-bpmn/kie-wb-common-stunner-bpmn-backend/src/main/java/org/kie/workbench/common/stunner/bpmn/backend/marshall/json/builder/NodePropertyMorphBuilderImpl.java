/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder;

import java.util.Collection;
import java.util.LinkedList;

import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.HasInheritance;
import org.kie.workbench.common.stunner.core.definition.morph.BindablePropertyMorphDefinition;
import org.kie.workbench.common.stunner.core.definition.morph.MorphProperty;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class NodePropertyMorphBuilderImpl extends NodeBuilderImpl {

    private final BindablePropertyMorphDefinition propertyMorphDefinition;

    public NodePropertyMorphBuilderImpl(final Class<?> baseDefinitionClass,
                                        final BindablePropertyMorphDefinition propertyMorphDefinition) {
        super(baseDefinitionClass);
        this.propertyMorphDefinition = propertyMorphDefinition;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected String getDefinitionToBuild(final BuilderContext context) {
        final String defaultDefinitionId = propertyMorphDefinition.getDefault();
        final String definitionId = BindableAdapterUtils.getDefinitionId(definitionClass);
        Collection<MorphProperty> mps = new LinkedList<MorphProperty>();
        // TODO: Iterate over all morph properties.
        final Iterable<MorphProperty> mps1 = propertyMorphDefinition.getMorphProperties(definitionId);
        if (mps1 != null && mps1.iterator().hasNext()) {
            mps.add(mps1.iterator().next());
        }
        DefinitionAdapter<Object> definitionAdapter = context.getDefinitionManager().adapters().registry().getDefinitionAdapter(definitionClass);
        String baseId = ((HasInheritance) definitionAdapter).getBaseType(definitionClass);
        if (null != baseId) {
            final Iterable<MorphProperty> mps2 = propertyMorphDefinition.getMorphProperties(baseId);
            if (mps2 != null && mps2.iterator().hasNext()) {
                mps.add(mps2.iterator().next());
            }
        }
        final MorphProperty morphProperty = mps.iterator().next();
        final Object defaultDefinition = context.getFactoryManager().newDefinition(defaultDefinitionId);
        final DefinitionUtils definitionUtils = new DefinitionUtils(context.getDefinitionManager(),
                                                                    context.getFactoryManager(),
                                                                    context.getDefinitionsRegistry());
        final Object mp = definitionUtils.getProperty(defaultDefinition,
                                                      morphProperty.getProperty());
        final PropertyType propertyType = context.getDefinitionManager().adapters().forProperty().getType(mp);
        final String oryxId = context.getOryxManager().getMappingsManager().getOryxPropertyId(mp.getClass());
        final String pRawValue = properties.get(oryxId);
        final Object pValue = context.getOryxManager().getPropertyManager().parse(mp,
                                                                                  propertyType,
                                                                                  pRawValue);
        return morphProperty.getMorphTarget(pValue);
    }
}
