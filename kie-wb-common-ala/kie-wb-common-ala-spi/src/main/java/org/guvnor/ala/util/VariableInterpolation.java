/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.ala.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.configuration.interpol.ConfigurationInterpolator;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;
import org.guvnor.ala.config.CloneableConfig;

/**
 * This class deals with Variable Interpolations inside pipelines.
 * It uses bytebuddy to create intermediary types for the results
 * created by the interpolation process
 */
public final class VariableInterpolation {

    private VariableInterpolation() {

    }

    private static final ConfigurationInterpolator interpolator = new ConfigurationInterpolator();
    private static final StrSubstitutor substitutor = new StrSubstitutor(interpolator);

    public static <T> T interpolate(final Map<String, Object> values,
                                    final T object) {
        interpolator.setDefaultLookup(new MapOfMapStrLookup(values));
        return proxy(object);
    }

    private static class MapOfMapStrLookup extends StrLookup {

        private final Map map;

        MapOfMapStrLookup(Map map) {
            this.map = map;
        }

        @Override
        public String lookup(String key) {
            if (this.map == null) {
                return null;
            } else {
                int dotIndex = key.indexOf(".");
                Object obj = this.map.get(key.substring(0,
                                                        dotIndex < 0 ? key.length() : dotIndex));
                if (obj instanceof Map) {
                    return new MapOfMapStrLookup(((Map) obj)).lookup(key.substring(key.indexOf(".") + 1));
                } else if (obj != null && !(obj instanceof String) && key.contains(".")) {
                    final String subkey = key.substring(key.indexOf(".") + 1);
                    for (PropertyDescriptor descriptor : new PropertyUtilsBean().getPropertyDescriptors(obj)) {
                        if (descriptor.getName().equals(subkey)) {
                            try {
                                return descriptor.getReadMethod().invoke(obj).toString();
                            } catch (Exception ex) {
                                continue;
                            }
                        }
                    }
                }

                return obj == null ? "" : obj.toString();
            }
        }
    }

    public static <T> T proxy(final T instance) {
        try {
            Class<?>[] _interfaces;
            Class<?> currentClass = instance.getClass();
            do {
                _interfaces = currentClass.getInterfaces();
                currentClass = currentClass.getSuperclass();
            } while (_interfaces.length == 0 && currentClass != null);

            T result = (T) new ByteBuddy()
                    .subclass(Object.class)
                    .implement(_interfaces)
                    .method(ElementMatchers.any())
                    .intercept(InvocationHandlerAdapter.of(new InterpolationHandler(instance)))
                    .make()
                    .load(instance.getClass().getClassLoader(),
                          ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded()
                    .newInstance();
            if (instance instanceof CloneableConfig) {
                return (T) ((CloneableConfig) result).asNewClone(result);
            }
            return result;
        } catch (final Exception ignored) {
            ignored.printStackTrace();
            return instance;
        }
    }

    public static class InterpolationHandler implements InvocationHandler {

        Object object;

        public InterpolationHandler(final Object object) {
            this.object = object;
        }

        @Override
        public Object invoke(Object proxy,
                             Method method,
                             Object[] args) throws Throwable {
            Object result = method.invoke(object,
                                          args);

            if (result != null && result instanceof String) {
                return substitutor.replace((String) result);
            } else {
                return result;
            }
        }
    }
}