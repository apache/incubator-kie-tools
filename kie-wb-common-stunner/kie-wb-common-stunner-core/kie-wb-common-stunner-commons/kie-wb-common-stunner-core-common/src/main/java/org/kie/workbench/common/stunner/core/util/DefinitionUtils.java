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

package org.kie.workbench.common.stunner.core.util;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.MorphAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.HasInheritance;
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
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.registry.factory.FactoryRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

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

    public <T> Object getProperty(final T definition,
                                  final String propertyId) {
        final Set<?> properties = definitionManager.adapters().forDefinition().getProperties(definition);
        if (null != properties && !properties.isEmpty()) {
            for (final Object property : properties) {
                final String pId = definitionManager.adapters().forProperty().getId(property);
                if (pId.equals(propertyId)) {
                    return property;
                }
            }
        }
        return null;
    }

    public <T> String getName(final T definition) {
        final Optional<String> nameField = definitionManager.adapters()
                .forDefinition()
                .getNameField(definition);

        //first try to get by name field from Definition annotation
        if (nameField.isPresent()) {
            return getPropertyValueAsString(GraphUtils.getPropertyByField(definitionManager,
                                                                          definition,
                                                                          nameField.get()));
        }

        //default getting by metadata
        return Optional.ofNullable(definitionManager.adapters()
                                           .forDefinition()
                                           .getMetaProperty(PropertyMetaTypes.NAME, definition))
                .map(this::getPropertyValueAsString)
                .orElse(null);
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
        final Object r = adapter.getMetaProperty(PropertyMetaTypes.RADIUS,
                                                 definition);
        Double width = null;
        Double height = null;
        if (null != r) {
            final Double rv = (Double) definitionManager.adapters().forProperty().getValue(r);
            if (null != rv) {
                width = rv * 2;
                height = width;
            }
        } else {
            final Object w = adapter.getMetaProperty(PropertyMetaTypes.WIDTH,
                                                     definition);
            final Object h = adapter.getMetaProperty(PropertyMetaTypes.HEIGHT,
                                                     definition);
            if (null != w && null != h) {
                width = (Double) definitionManager.adapters().forProperty().getValue(w);
                height = (Double) definitionManager.adapters().forProperty().getValue(h);
            }
        }

        final double _width = null != width ? width : 0d;
        final double _height = null != height ? height : 0d;
        return Bounds.create(x, y, x + _width, y + _height);
    }

    public <T> String getNameIdentifier(final T definition) {
        return definitionManager.adapters()
                .forDefinition()
                .getNameField(definition)
                .orElseGet(
                        () -> Optional.ofNullable(definitionManager.adapters()
                                                          .forDefinition()
                                                          .getMetaProperty(PropertyMetaTypes.NAME, definition))
                                .filter(Objects::nonNull)
                                .map(name -> definitionManager.adapters().forProperty().getId(name))
                                .orElse(null)
                );
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
        checkNotNull("defSetId",
                     defSetId);
        final Object ds = definitionManager.definitionSets().getDefinitionSetById(defSetId);
        return getQualifier(ds);
    }

    private Annotation getQualifier(final Object ds) {
        return definitionManager.adapters().forDefinitionSet().getQualifier(ds);
    }

    /**
     * Returns all properties from Definition's property sets.
     */
    public Set<?> getPropertiesFromPropertySets(final Object definition) {
        final Set<Object> properties = new HashSet<>();
        // And properties on each definition's PropertySet instance.
        final Set<?> propertySets = definitionManager.adapters().forDefinition().getPropertySets(definition);
        if (null != propertySets && !propertySets.isEmpty()) {
            for (Object propertySet : propertySets) {
                final Set<?> setProperties = definitionManager.adapters().forPropertySet().getProperties(propertySet);
                if (null != setProperties && !setProperties.isEmpty()) {
                    for (final Object property : setProperties) {
                        if (null != property) {
                            properties.add(property);
                        }
                    }
                }
            }
        }
        return properties;
    }

    @SuppressWarnings("unchecked")
    public Object getPropertyAllowedValue(final Object property,
                                          final String value) {
        final Map<Object, String> allowedValues = definitionManager.adapters().forProperty().getAllowedValues(property);
        if (null != value && null != allowedValues && !allowedValues.isEmpty()) {
            for (final Map.Entry<Object, String> entry : allowedValues.entrySet()) {
                final String v = entry.getValue();
                if (value.equals(v)) {
                    return entry.getKey();
                }
            }
        }
        return null;
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

    private static final Map<Class<?>, Class<? extends PropertyType>> DEFAULT_PROPERTY_TYPES = new Maps.Builder<Class<?>, Class<? extends PropertyType>>()
            .put(String.class,
                 StringType.class)
            .put(Double.class,
                 DoubleType.class)
            .put(Integer.class,
                 IntegerType.class)
            .put(Boolean.class,
                 BooleanType.class)
            .build();

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
