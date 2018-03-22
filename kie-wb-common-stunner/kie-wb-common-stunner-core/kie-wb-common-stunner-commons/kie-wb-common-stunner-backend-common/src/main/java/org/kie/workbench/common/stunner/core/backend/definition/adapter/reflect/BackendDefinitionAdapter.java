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

package org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.backend.definition.adapter.AbstractReflectAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.HasInheritance;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Id;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Handle i18n bundle.

@Dependent
public class BackendDefinitionAdapter<T> extends AbstractReflectAdapter<T>
        implements DefinitionAdapter<T>,
                   HasInheritance {

    private static final Logger LOG = LoggerFactory.getLogger(BackendDefinitionAdapter.class);

    private static final Class[] DEF_ANNOTATIONS = new Class[]{
            Title.class,
            Category.class,
            Description.class,
            PropertySet.class,
            Property.class
    };

    private final DefinitionUtils definitionUtils;

    @Inject
    public BackendDefinitionAdapter(final DefinitionUtils definitionUtils) {
        this.definitionUtils = definitionUtils;
    }

    @Override
    public boolean accepts(Class<?> pojo) {
        return pojo.getAnnotation(Definition.class) != null;
    }

    @Override
    public String getId(final T definition) {
        final Id id = getClassAnnotation(definition.getClass(),
                                         Id.class);
        if (null != id) {
            try {

                return BindableAdapterUtils.getDynamicDefinitionId(definition.getClass(),
                                                                   getAnnotatedFieldValue(definition,
                                                                                          Id.class));
            } catch (Exception e) {
                LOG.error("Error obtaining annotated id for Definition " + definition.getClass().getName());
            }
            return null;
        }
        return getDefinitionId(definition.getClass());
    }

    @Override
    public Object getMetaProperty(final PropertyMetaTypes metaType,
                                  final T pojo) {
        Set<?> properties = getProperties(pojo);
        if (null != properties) {
            return properties
                    .stream()
                    .filter(property -> {
                        Property p = getClassAnnotation(property.getClass(),
                                                        Property.class);
                        return null != p && metaType.equals(p.meta());
                    })
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Override
    public String getCategory(final T definition) {
        try {
            return getAnnotatedFieldValue(definition,
                                          Category.class);
        } catch (Exception e) {
            LOG.error("Error obtaining annotated category for Definition with id " + getId(definition));
        }
        return null;
    }

    @Override
    public String getTitle(final T definition) {
        try {
            return getAnnotatedFieldValue(definition,
                                          Title.class);
        } catch (Exception e) {
            LOG.error("Error obtaining annotated title for Definition with id " + getId(definition));
        }
        return BindableAdapterUtils.toSimpleName(definition);
    }

    @Override
    public String getDescription(final T definition) {
        try {
            return getAnnotatedFieldValue(definition,
                                          Description.class);
        } catch (Exception e) {
            LOG.error("Error obtaining annotated description for Definition with id " + getId(definition));
        }
        return BindableAdapterUtils.toSimpleName(definition);
    }

    @Override
    public Set<String> getLabels(final T definition) {
        try {
            return getAnnotatedFieldValue(definition,
                                          Labels.class);
        } catch (Exception e) {
            LOG.error("Error obtaining annotated labels for Definition with id " + getId(definition));
        }
        return Collections.emptySet();
    }

    @Override
    public Set<?> getPropertySets(final T definition) {
        Collection<Field> fields = getFieldAnnotations(definition.getClass(),
                                                       PropertySet.class);
        if (null != fields) {
            Set<Object> result = new LinkedHashSet<>();
            fields.forEach(field -> {
                try {
                    Object v = _getValue(field,
                                         PropertySet.class,
                                         definition);
                    result.add(v);
                } catch (Exception e) {
                    LOG.error("Error obtaining annotated property sets for Definition with id " + getId(definition));
                }
            });
            return result;
        }
        return Collections.emptySet();
    }

    @Override
    public Set<?> getProperties(final T definition) {
        if (null != definition) {
            final Set<Object> result = new HashSet<>();
            // Obtain all properties from property sets.
            Set<?> propertySetProperties = definitionUtils.getPropertiesFromPropertySets(definition);
            if (null != propertySetProperties) {
                result.addAll(propertySetProperties);
            }
            Collection<Field> fields = getFieldAnnotations(definition.getClass(),
                                                           Property.class);
            if (null != fields) {
                fields.forEach(field -> {
                    try {
                        Object v = _getValue(field,
                                             Property.class,
                                             definition);
                        result.add(v);
                    } catch (Exception e) {
                        LOG.error("Error obtaining annotated properties for Definition with id " + getId(definition));
                    }
                });
                return result;
            }
        }
        return Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    private <V> V _getValue(final Field field,
                            final Object annotation,
                            final T definition) throws IllegalAccessException {
        if (null != annotation) {
            field.setAccessible(true);
            return (V) field.get(definition);
        }
        return null;
    }

    @Override
    public Class<? extends ElementFactory> getGraphFactoryType(final T definition) {
        Definition annotation = getDefinitionAnnotation(definition.getClass());
        return null != annotation ? annotation.graphFactory() : null;
    }

    public static Class<? extends ElementFactory> getGraphFactory(final Class<?> type) {
        Definition annotation = getDefinitionAnnotation(type);
        return null != annotation ? annotation.graphFactory() : null;
    }

    protected static Definition getDefinitionAnnotation(final Class<?> type) {
        if (null != type) {
            Definition annotation = getClassAnnotation(type,
                                                       Definition.class);
            if (null != annotation) {
                return annotation;
            }
        }
        return null;
    }

    @Override
    public String getBaseType(final Class<?> type) {
        if (null != type) {
            Definition annotation = getClassAnnotation(type,
                                                       Definition.class);
            if (null != annotation) {
                Class<?> parentType = type.getSuperclass();
                if (isBaseType(parentType)) {
                    return getDefinitionId(parentType);
                }
            }
        }
        return null;
    }

    @Override
    public String[] getTypes(final String baseType) {
        throw new UnsupportedOperationException("Not implemented yet. Must keep some collection for this. ");
    }

    private boolean isBaseType(final Class<?> type) {
        Field[] fields = type.getDeclaredFields();
        if (null != fields) {
            for (Field field : fields) {
                for (Class a : DEF_ANNOTATIONS) {
                    Annotation annotation = field.getAnnotation(a);
                    if (null != annotation) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
