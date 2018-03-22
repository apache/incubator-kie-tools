/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.definition.adapter.binding;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.HasProperties;
import org.jboss.errai.databinding.client.NonExistingPropertyException;
import org.jboss.errai.databinding.client.PropertyType;
import org.jboss.errai.databinding.client.api.DataBinder;

public class ClientBindingUtils {

    private static Logger LOGGER = Logger.getLogger(ClientBindingUtils.class.getName());

    @SuppressWarnings("unchecked")
    public static <T, R> R getProxiedValue(final T pojo,
                                           final String fieldName) {
        R result = null;
        if (null != pojo && null != fieldName) {
            HasProperties hasProperties = (HasProperties) DataBinder.forModel(pojo).getModel();
            result = (R) hasProperties.get(fieldName);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Set<R> getProxiedSet(final T pojo,
                                              final Collection<String> fieldNames) {
        Set<R> result = new LinkedHashSet<>();
        if (null != pojo && null != fieldNames && !fieldNames.isEmpty()) {
            for (String fieldName : fieldNames) {
                HasProperties hasProperties = (HasProperties) DataBinder.forModel(pojo).getModel();
                result.add((R) hasProperties.get(fieldName));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T, V> void setProxiedValue(final T pojo,
                                              final String fieldName,
                                              final V value) {
        if (null != pojo && null != fieldName) {
            HasProperties hasProperties = (HasProperties) DataBinder.forModel(pojo).getModel();
            hasProperties.set(fieldName,
                              value);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(final Class<?> pojoType) {
        if (null != pojoType) {
            return (T) DataBinder.forType(pojoType).getModel();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T clone(final T pojo) {
        if (null != pojo) {
            final BindableProxy proxy = (BindableProxy) DataBinder.forModel(pojo).getModel();
            return (T) proxy.deepUnwrap();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T, R> R merge(final T source,
                                 final R target) {
        if (null != source) {
            final HasProperties hasProperties = (HasProperties) DataBinder.forModel(source).getModel();
            if (null != hasProperties) {
                final Map<String, PropertyType> propertyTypeMap = hasProperties.getBeanProperties();
                if (null != propertyTypeMap && !propertyTypeMap.isEmpty()) {
                    final HasProperties targetProperties = (HasProperties) DataBinder.forModel(target).getModel();
                    for (final Map.Entry<String, PropertyType> entry : propertyTypeMap.entrySet()) {
                        final String pId = entry.getKey();
                        try {
                            targetProperties.set(pId,
                                                 hasProperties.get(pId));
                        } catch (NonExistingPropertyException exception) {
                            // Just skip it, Go to next property.
                            LOGGER.log(Level.INFO,
                                       "BindableAdapterUtils#merge - Skipping merge property [" + pId + "]");
                        }
                    }
                    return (R) target;
                }
            }
        }
        return null;
    }
}
