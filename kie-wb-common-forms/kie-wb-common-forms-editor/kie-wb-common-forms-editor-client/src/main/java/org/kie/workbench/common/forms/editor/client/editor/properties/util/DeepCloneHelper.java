/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.client.editor.properties.util;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.BindableProxyFactory;
import org.jboss.errai.databinding.client.PropertyType;

public class DeepCloneHelper {

    public static <T> T deepClone(T instance) {
        if(instance == null) {
            return null;
        }

        return doDeepClone(instance).deepUnwrap();
    }

    private static <T> BindableProxy<T> doDeepClone(T instance) {
        final BindableProxy<T> proxy;

        if(instance instanceof BindableProxy) {
            proxy = (BindableProxy<T>) instance;
        } else {
            proxy = (BindableProxy<T>) BindableProxyFactory.getBindableProxy(instance);
        }

        if(proxy != null) {
            proxy.getBeanProperties()
                    .entrySet()
                    .stream()
                    .filter(entry -> isProxy(entry.getValue().getType()))
                    .forEach(entry -> deepCloneProxyProperty(proxy, entry.getKey(), entry.getValue()));
        }

        return proxy;
    }

    private static <T> void deepCloneProxyProperty(BindableProxy<T> proxy, String propertyName, PropertyType type) {
        Object value = proxy.get(propertyName);

        if(type.isList() && value != null) {
            value = doDeepCloneList((List)value);
        } else {
            value = doDeepClone(value);
        }

        proxy.set(propertyName, value);
    }

    private static <T> List<BindableProxy<T>> doDeepCloneList(List<T> values) {
        return values.stream()
                .map(DeepCloneHelper::doDeepClone)
                .collect(Collectors.toList());
    }


    private static boolean isProxy(Class clazz) {
        try {
            if(BindableProxyFactory.getBindableProxy(clazz) != null) {
                return true;
            }
        } catch (Exception ex) {
            // Non proxyable class.
        }
        return false;
    }
}
