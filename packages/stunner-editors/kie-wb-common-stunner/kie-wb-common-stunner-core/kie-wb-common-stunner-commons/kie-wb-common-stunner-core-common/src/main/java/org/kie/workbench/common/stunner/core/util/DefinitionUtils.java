/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.util;

import java.lang.annotation.Annotation;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.HasInheritance;
import org.kie.workbench.common.stunner.core.definition.adapter.MorphAdapter;
import org.kie.workbench.common.stunner.core.definition.clone.ClonePolicy;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.kie.workbench.common.stunner.core.definition.property.type.BooleanType;
import org.kie.workbench.common.stunner.core.definition.property.type.DoubleType;
import org.kie.workbench.common.stunner.core.definition.property.type.IntegerType;
import org.kie.workbench.common.stunner.core.definition.property.type.StringType;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.registry.factory.FactoryRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;

@ApplicationScoped
public class DefinitionUtils {

    private final DefinitionManager definitionManager;
    private final DefinitionsCacheRegistry definitionsRegistry;

    protected DefinitionUtils() {
        this(null,
             null);
    }

    @Inject
    @SuppressWarnings("all")
    public DefinitionUtils(final DefinitionManager definitionManager,
                           final DefinitionsCacheRegistry definitionsRegistry) {
        this.definitionManager = definitionManager;
        this.definitionsRegistry = definitionsRegistry;
    }

    public <T> String getName(final T definition) {
        final DefinitionAdapter<Object> definitionAdapter = definitionManager.adapters().registry().getDefinitionAdapter(definition.getClass());
        final String nameField = definitionAdapter.getMetaPropertyField(definition, PropertyMetaTypes.NAME);
        if (null != nameField) {
            Object name = definitionAdapter.getProperty(definition, nameField).get();
            return getPropertyValueAsString(name);
        }
        return null;
    }

    public <T> String getNameIdentifier(final T definition) {
        return definitionManager.adapters().forDefinition().getMetaPropertyField(definition, PropertyMetaTypes.NAME);
    }

    private String getPropertyValueAsString(Object property) {
        return Optional.ofNullable(definitionManager.adapters().forProperty().getValue(property))
                .map(String::valueOf)
                .orElse("");
    }

    public String getTitle(final String definitionId) {
        return definitionManager
                .adapters()
                .forDefinition()
                .getTitle(definitionsRegistry.getDefinitionById(definitionId));
    }

    @SuppressWarnings("unchecked")
    public Bounds buildBounds(final Object definition,
                              final double x,
                              final double y) {
        final DefinitionAdapter<Object> adapter = definitionManager.adapters().registry().getDefinitionAdapter(definition.getClass());
        final String radiusField = adapter.getMetaPropertyField(definition, PropertyMetaTypes.RADIUS);
        Double width = null;
        Double height = null;
        if (null != radiusField) {
            Optional<?> hasRadius = adapter.getProperty(definition, radiusField);
            if (hasRadius.isPresent()) {
                final Double rv = (Double) definitionManager.adapters().forProperty().getValue(hasRadius.get());
                if (null != rv) {
                    width = rv * 2;
                    height = width;
                }
            }
        } else {
            final String wField = adapter.getMetaPropertyField(definition, PropertyMetaTypes.WIDTH);
            final String hField = adapter.getMetaPropertyField(definition, PropertyMetaTypes.HEIGHT);
            if (null != wField && null != hField) {
                Optional<?> hasWitdth = adapter.getProperty(definition, wField);
                Optional<?> hasHeight = adapter.getProperty(definition, hField);
                if (hasWitdth.isPresent() && hasHeight.isPresent()) {
                    width = (Double) definitionManager.adapters().forProperty().getValue(hasWitdth.get());
                    height = (Double) definitionManager.adapters().forProperty().getValue(hasHeight.get());
                }
            }
        }

        final double _width = null != width ? width : 0d;
        final double _height = null != height ? height : 0d;
        return Bounds.create(x, y, x + _width, y + _height);
    }

    public <T> MorphDefinition getMorphDefinition(final T definition) {
        final MorphAdapter<Object> adapter = definitionManager.adapters().registry().getMorphAdapter(definition.getClass());
        final Iterable<MorphDefinition> definitions = adapter.getMorphDefinitions(definition);
        if (null != definitions && definitions.iterator().hasNext()) {
            return definitions.iterator().next();
        }
        return null;
    }

    public boolean hasMorphTargets(final Object definition) {
        final MorphAdapter<Object> morphAdapter = definitionManager.adapters().registry().getMorphAdapter(definition.getClass());
        final Iterable<MorphDefinition> morphDefinitions = morphAdapter.getMorphDefinitions(definition);
        if (null != morphDefinitions && morphDefinitions.iterator().hasNext()) {
            for (final MorphDefinition morphDefinition : morphDefinitions) {
                final Iterable<String> morphTargets = morphAdapter.getTargets(definition,
                                                                              morphDefinition);
                if (null != morphTargets && morphTargets.iterator().hasNext()) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getDefinitionSetId(final Class<?> type) {
        return definitionManager
                .adapters()
                .forDefinitionSet()
                .getId(definitionManager
                               .definitionSets()
                               .getDefinitionSetByType(type));
    }

    /**
     * Returns the identifiers for the definition type and its parent, if any.
     */
    public <T> String[] getDefinitionIds(final T definition) {
        final Class<?> type = definition.getClass();
        final DefinitionAdapter<Object> definitionAdapter = definitionManager.adapters().registry().getDefinitionAdapter(type);
        final String definitionId = definitionAdapter.getId(definition).value();
        String baseId = null;
        if (definitionAdapter instanceof HasInheritance) {
            baseId = ((HasInheritance) definitionAdapter).getBaseType(type);
        }
        return new String[]{definitionId, baseId};
    }

    public boolean isAllPolicy(final MorphDefinition definition) {
        return ClonePolicy.ALL.equals(definition.getPolicy());
    }

    public boolean isNonePolicy(final MorphDefinition definition) {
        return ClonePolicy.NONE.equals(definition.getPolicy());
    }

    public boolean isDefaultPolicy(final MorphDefinition definition) {
        return ClonePolicy.DEFAULT.equals(definition.getPolicy());
    }

    public Annotation getQualifier(final String defSetId) {
        Objects.requireNonNull(defSetId, "Parameter named 'defSetId' should be not null!");
        final Object ds = definitionManager.definitionSets().getDefinitionSetById(defSetId);
        return getQualifier(ds);
    }

    private Annotation getQualifier(final Object ds) {
        return definitionManager.adapters().forDefinitionSet().getQualifier(ds);
    }

    public static boolean isNodeFactory(final Class<? extends ElementFactory> graphFactoryClass,
                                        final FactoryRegistry registry) {
        if (!graphFactoryClass.equals(NodeFactory.class)) {
            ElementFactory factory = registry.getElementFactory(graphFactoryClass);
            return factory instanceof NodeFactory;
        }
        return true;
    }

    public static boolean isEdgeFactory(final Class<? extends ElementFactory> graphFactoryClass,
                                        final FactoryRegistry registry) {
        if (!graphFactoryClass.equals(EdgeFactory.class)) {
            ElementFactory factory = registry.getElementFactory(graphFactoryClass);
            return factory instanceof EdgeFactory;
        }
        return true;
    }

    public DefinitionManager getDefinitionManager() {
        return definitionManager;
    }

    private static final Map<Class<?>, Class<? extends PropertyType>> DEFAULT_PROPERTY_TYPES =
            Stream.of(new AbstractMap.SimpleEntry<>(String.class, StringType.class),
                      new AbstractMap.SimpleEntry<>(Double.class, DoubleType.class),
                      new AbstractMap.SimpleEntry<>(Integer.class, IntegerType.class),
                      new AbstractMap.SimpleEntry<>(Boolean.class, BooleanType.class))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public static Class<? extends PropertyType> getDefaultPropertyType(final Class<?> clazz) {
        return DEFAULT_PROPERTY_TYPES.get(clazz);
    }

    public static Object getElementDefinition(final Element element) {
        if (element != null && element.getContent() instanceof Definition) {
            return ((Definition) element.getContent()).getDefinition();
        } else {
            return null;
        }
    }
}
