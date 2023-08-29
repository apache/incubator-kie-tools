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


package org.kie.workbench.common.stunner.core.client.definition.adapter.binding;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.HasProperties;
import org.jboss.errai.databinding.client.PropertyType;
import org.jboss.errai.databinding.client.api.DataBinder;

import static org.kie.workbench.common.stunner.core.util.StringUtils.nonEmpty;

public class ClientBindingUtils {

    public static <T> Class<?> getProxiedType(final T pojo,
                                              final String fieldName) {
        if (null != pojo && null != fieldName) {
            HasProperties hasProperties = (HasProperties) DataBinder.forModel(pojo).getModel();
            PropertyType propertyType = hasProperties.getBeanProperties().get(fieldName);
            if (null != propertyType) {
                return propertyType.getType();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T, R> R getProxiedValue(final T pojo,
                                           final String fieldName) {
        R result = null;
        if (null != pojo && nonEmpty(fieldName)) {
            PropertyHolder propertyHolder = getProperty(pojo, fieldName);
            result = (R) propertyHolder.getValue().orElse(null);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Set<R> getProxiedSet(final T pojo,
                                              final Collection<String> fieldNames) {
        Set<R> result = new LinkedHashSet<>();
        if (null != pojo && null != fieldNames && !fieldNames.isEmpty()) {
            for (String fieldName : fieldNames) {
                PropertyHolder propertyHolder = getProperty(pojo, fieldName);
                propertyHolder.getValue().ifPresent(value -> result.add((R) value));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T, V> void setProxiedValue(final T pojo,
                                              final String fieldName,
                                              final V value) {
        if (null != pojo && nonEmpty(fieldName)) {
            PropertyHolder propertyHolder = getProperty(pojo, fieldName);
            propertyHolder.setValue(value);
        }
    }

    private static class PropertyHolder {

        private HasProperties hasProperties;
        private String fieldName;

        public static PropertyHolder empty() {
            return new PropertyHolder(null, null);
        }

        public PropertyHolder(HasProperties hasProperties, String fieldName) {
            this.hasProperties = hasProperties;
            this.fieldName = fieldName;
        }

        public Optional<?> getValue() {
            return null != hasProperties ? Optional.ofNullable(hasProperties.get(fieldName)) : Optional.empty();
        }

        public void setValue(Object value) {
            if (null != hasProperties) {
                hasProperties.set(fieldName, value);
            }
        }
    }

    private static PropertyHolder getProperty(final Object pojo,
                                              final String fieldName) {
        final int index = fieldName.indexOf('.');
        if (index > -1) {
            String parentField = fieldName.substring(0, index);
            String field = fieldName.substring(index + 1);
            HasProperties hasProperties = (HasProperties) DataBinder.forModel(pojo).getModel();
            Object parent = hasProperties.get(parentField);
            if (null == parent) {
                return PropertyHolder.empty();
            }
            HasProperties parentHasProperties = (HasProperties) DataBinder.forModel(parent).getModel();
            return new PropertyHolder(parentHasProperties, field);
        } else {
            HasProperties hasProperties = (HasProperties) DataBinder.forModel(pojo).getModel();
            return new PropertyHolder(hasProperties, fieldName);
        }
    }

    @SuppressWarnings("all")
    public static <T> T clone(final T pojo) {
        if (null != pojo) {
            final BindableProxy proxy = (BindableProxy) DataBinder.forModel(pojo).getModel();
            return (T) proxy.deepUnwrap();
        }
        return null;
    }
}
