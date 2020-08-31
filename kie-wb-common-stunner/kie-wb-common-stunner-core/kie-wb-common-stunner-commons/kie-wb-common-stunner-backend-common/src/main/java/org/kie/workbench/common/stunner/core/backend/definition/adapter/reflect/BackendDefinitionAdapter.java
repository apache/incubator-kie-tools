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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.backend.definition.adapter.AbstractReflectAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.ReflectionAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.adapter.HasInheritance;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
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
import org.kie.workbench.common.stunner.core.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Handle i18n bundle.

@Dependent
public class BackendDefinitionAdapter<T>
        extends AbstractReflectAdapter<T>
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

    @Inject
    public BackendDefinitionAdapter() {
    }

    @Override
    public boolean accepts(Class<?> pojo) {
        return pojo.getAnnotation(Definition.class) != null;
    }

    @Override
    public DefinitionId getId(final T definition) {
        final String definitionId = getDefinitionId(definition.getClass());
        final Id idAnn = getClassAnnotation(definition.getClass(),
                                            Id.class);
        if (null != idAnn) {
            try {
                final String value = BindableAdapterUtils.getDynamicDefinitionId(definitionId,
                                                                                 getAnnotatedFieldValue(definition,
                                                                                                        Id.class));
                return DefinitionId.build(value, definitionId.length());
            } catch (Exception e) {
                LOG.error("Error obtaining annotated id for Definition " + definition.getClass().getName());
            }
        }
        return DefinitionId.build(definitionId);
    }

    @Override
    public String[] getPropertyFields(final T pojo) {
        final List<String> fields = visitFields(pojo.getClass(), field -> null != field.getAnnotation(Property.class));
        return fields.toArray(new String[fields.size()]);
    }

    private static List<String> visitFields(Class<?> type,
                                            Predicate<Field> fieldAcceptor) {
        final ArrayList<String> result = new ArrayList<>();
        visitFields(type, "", fieldAcceptor, result, new HashSet<>());
        return result;
    }

    private static void visitFields(Class<?> type,
                                    String namespace,
                                    Predicate<Field> fieldAcceptor,
                                    List<String> result,
                                    Set<String> processedTypes) {
        final String fqcn = type.getName();
        if (ClassUtils.isJavaRuntimeClassname(fqcn) ||
                processedTypes.contains(fqcn)) {
            return;
        }
        processedTypes.add(fqcn);

        final List<Field> fields = ReflectionAdapterUtils.getFields(type);
        fields.forEach(field -> {
            final String fieldName = field.getName();
            final String absoluteFieldName = appendToNamespace(namespace, fieldName);
            if (fieldAcceptor.test(field)) {
                result.add(absoluteFieldName);
            } else {
                Class<?> fieldType = field.getType();
                visitFields(fieldType,
                            absoluteFieldName,
                            fieldAcceptor,
                            result,
                            processedTypes);
            }
        });
    }

    private static String appendToNamespace(String namespace,
                                            String field) {
        return namespace.trim().length() > 0 ?
                namespace + "." + field :
                field;
    }

    private static boolean isPropertyOfMetaType(final Field field,
                                                final PropertyMetaTypes metaType) {
        final Property annotation = field.getAnnotation(Property.class);
        if (null != annotation) {
            PropertyMetaTypes type = annotation.meta();
            return metaType.equals(type);
        }
        return false;
    }

    @Override
    public String getMetaPropertyField(final T pojo,
                                       final PropertyMetaTypes metaType) {
        List<String> fields = visitFields(pojo.getClass(), field -> isPropertyOfMetaType(field, metaType));
        return !fields.isEmpty() ? fields.get(0) : null;
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
    @SuppressWarnings("all")
    public String[] getLabels(final T definition) {
        try {
            Object value = getAnnotatedFieldValue(definition,
                                                  Labels.class);
            if (value instanceof Collection) {
                return (String[]) ((Collection) value).toArray(new String[((Collection) value).size()]);
            }
            return null != value ? (String[]) value : new String[0];
        } catch (Exception e) {
            LOG.error("Error obtaining annotated labels for Definition with id " + getId(definition));
        }
        return new String[0];
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
            return annotation;
        }
        return null;
    }

    @Override
    public String getBaseType(final Class<?> type) {
        return Optional.ofNullable(type)
                .filter(t -> !t.isPrimitive())
                .filter(t -> Objects.nonNull(getClassAnnotation(t, Definition.class)))
                .map(this::findBaseParent)
                .map(BackendDefinitionAdapter::getDefinitionId)
                .orElse(null);
    }

    /**
     * Find on the parent hierarchy of the given type the Class that is a BaseType
     */
    @SuppressWarnings("all")
    private Class findBaseParent(final Class type) {
        return (Objects.isNull(type) || Object.class.equals(type)
                ? null
                : Optional.ofNullable(type)
                .map(Class::getSuperclass)
                .filter(this::isBaseType)
                .orElse(findBaseParent(type.getSuperclass())));
    }

    @Override
    public String[] getTypes(final String baseType) {
        throw new UnsupportedOperationException("Not implemented yet. Must keep some collection for this. ");
    }

    @SuppressWarnings("all")
    private boolean isBaseType(final Class<?> type) {
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            for (Class a : DEF_ANNOTATIONS) {
                Annotation annotation = field.getAnnotation(a);
                if (null != annotation) {
                    return true;
                }
            }
        }
        return false;
    }
}
