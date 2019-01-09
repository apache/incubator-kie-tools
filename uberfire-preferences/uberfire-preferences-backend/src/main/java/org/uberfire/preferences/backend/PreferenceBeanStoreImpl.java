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

package org.uberfire.preferences.backend;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.annotations.Customizable;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.PreferenceStore;
import org.uberfire.preferences.shared.annotations.PortablePreference;
import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;
import org.uberfire.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.preferences.shared.bean.Preference;
import org.uberfire.preferences.shared.bean.PreferenceBeanServerStore;
import org.uberfire.preferences.shared.bean.PreferenceBeanStore;
import org.uberfire.preferences.shared.bean.PreferenceHierarchyElement;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

/**
 * Backend implementation for {@link PreferenceBeanStore}.
 */
@Service
public class PreferenceBeanStoreImpl implements PreferenceBeanServerStore {

    private static final AnnotationLiteral<PortablePreference> portablePreferenceAnnotation = new AnnotationLiteral<PortablePreference>() {
    };

    private PreferenceStore preferenceStore;

    private PreferenceScopeResolutionStrategy defaultScopeResolutionStrategy;

    private Instance<Preference> preferences;

    private Map<String, List<BasePreferencePortable>> childrenByParent;

    public PreferenceBeanStoreImpl() {
    }

    @Inject
    public PreferenceBeanStoreImpl(final PreferenceStore preferenceStore,
                                   @Customizable final PreferenceScopeResolutionStrategy defaultScopeResolutionStrategy,
                                   @PortablePreference final Instance<Preference> preferences) {
        this.preferenceStore = preferenceStore;
        this.defaultScopeResolutionStrategy = defaultScopeResolutionStrategy;
        this.preferences = preferences;
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> T load(final T emptyPortablePreference) {
        return load(emptyPortablePreference,
                    defaultScopeResolutionStrategy.getInfo());
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> T load(final T emptyPortablePreference,
                                                                                     final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo) {
        Class<U> clazz = emptyPortablePreference.getPojoClass();
        T portablePreference = preferenceStore.get(scopeResolutionStrategyInfo,
                                                   emptyPortablePreference.identifier());
        if (portablePreference == null) {
            portablePreference = (T) emptyPortablePreference.defaultValue((U) emptyPortablePreference);
        }

        try {
            return load(clazz,
                        portablePreference,
                        scopeResolutionStrategyInfo);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void load(final T emptyPortablePreference,
                                                                                        final ParameterizedCommand<T> successCallback,
                                                                                        final ParameterizedCommand<Throwable> errorCallback) {
        load(emptyPortablePreference,
             defaultScopeResolutionStrategy.getInfo(),
             successCallback,
             errorCallback);
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void load(final T emptyPortablePreference,
                                                                                        final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                                                                        final ParameterizedCommand<T> successCallback,
                                                                                        final ParameterizedCommand<Throwable> errorCallback) {
        T loadedPreference = null;
        try {
            loadedPreference = load(emptyPortablePreference,
                                    scopeResolutionStrategyInfo);
        } catch (Exception e) {
            if (errorCallback != null) {
                errorCallback.execute(e);
            }
        }

        if (successCallback != null) {
            successCallback.execute(loadedPreference);
        }
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(final T portablePreference) {
        save(portablePreference,
             defaultScopeResolutionStrategy.getInfo());
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(final T portablePreference,
                                                                                        final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo) {
        save(portablePreference,
             scopeResolutionStrategyInfo.defaultScope());
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(final T portablePreference,
                                                                                        final PreferenceScope scope) {
        try {
            Class<U> clazz = portablePreference.getPojoClass();
            save(clazz,
                 portablePreference,
                 scope);
            if (portablePreference.isPersistable()) {
                preferenceStore.put(scope,
                                    portablePreference.identifier(),
                                    portablePreference);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(final T portablePreference,
                                                                                        final Command successCallback,
                                                                                        final ParameterizedCommand<Throwable> errorCallback) {
        save(portablePreference,
             defaultScopeResolutionStrategy.getInfo(),
             successCallback,
             errorCallback);
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(final T portablePreference,
                                                                                        final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                                                                        final Command successCallback,
                                                                                        final ParameterizedCommand<Throwable> errorCallback) {
        save(portablePreference,
             scopeResolutionStrategyInfo.defaultScope(),
             successCallback,
             errorCallback);
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(final T portablePreference,
                                                                                        final PreferenceScope scope,
                                                                                        final Command successCallback,
                                                                                        final ParameterizedCommand<Throwable> errorCallback) {
        try {
            save(portablePreference,
                 scope);
        } catch (Exception e) {
            if (errorCallback != null) {
                errorCallback.execute(e);
            }
        }

        if (successCallback != null) {
            successCallback.execute();
        }
    }

    @Override
    public void save(final Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences) {
        save(portablePreferences,
             defaultScopeResolutionStrategy.getInfo());
    }

    @Override
    public void save(final Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences,
                     final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo) {
        save(portablePreferences,
             scopeResolutionStrategyInfo.defaultScope());
    }

    @Override
    public void save(final Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences,
                     final PreferenceScope scope) {
        for (BasePreferencePortable<? extends BasePreference<?>> portablePreference : portablePreferences) {
            saveOne(portablePreference,
                    scope);
        }
    }

    @Override
    public void save(final Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences,
                     final Command successCallback,
                     final ParameterizedCommand<Throwable> errorCallback) {
        save(portablePreferences,
             defaultScopeResolutionStrategy.getInfo(),
             successCallback,
             errorCallback);
    }

    @Override
    public void save(final Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences,
                     final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                     final Command successCallback,
                     final ParameterizedCommand<Throwable> errorCallback) {
        save(portablePreferences,
             scopeResolutionStrategyInfo.defaultScope(),
             successCallback,
             errorCallback);
    }

    @Override
    public void save(final Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences,
                     final PreferenceScope scope,
                     final Command successCallback,
                     final ParameterizedCommand<Throwable> errorCallback) {
        try {
            save(portablePreferences,
                 scope);
        } catch (Exception e) {
            if (errorCallback != null) {
                errorCallback.execute(e);
            }
        }

        if (successCallback != null) {
            successCallback.execute();
        }
    }

    @Override
    public PreferenceHierarchyElement<?> buildHierarchyStructureForPreference(final String identifier) {
        return buildHierarchyStructureForPreference(identifier,
                                                    defaultScopeResolutionStrategy.getInfo());
    }

    @Override
    public PreferenceHierarchyElement<?> buildHierarchyStructureForPreference(final String identifier,
                                                                              final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo) {
        BasePreferencePortable preference = getPortablePreferenceByIdentifier(identifier);
        preference = load(preference,
                          scopeResolutionStrategyInfo);

        final PreferenceHierarchyElement<?> rootElement = buildHierarchyElement(preference,
                                                                                null,
                                                                                false,
                                                                                true,
                                                                                preference.bundleKey(),
                                                                                scopeResolutionStrategyInfo);

        return rootElement;
    }

    private <U extends BasePreference<U>, T extends BasePreferencePortable<U>> T load(final Class<U> clazz,
                                                                                      T portablePreference,
                                                                                      final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo) throws IllegalAccessException {
        if (portablePreference == null) {
            portablePreference = lookupPortablePreference(clazz);
        }

        for (Field field : portablePreference.getPojoClass().getDeclaredFields()) {
            Property propertyAnnotation = field.getAnnotation(Property.class);
            if (propertyAnnotation != null) {
                if (field.getType().isAnnotationPresent(WorkbenchPreference.class)) {
                    final Class<? extends BasePreference<?>> propertyType = (Class<? extends BasePreference<?>>) field.getType();
                    boolean shared = propertyAnnotation.shared();

                    field.setAccessible(true);

                    if (shared) {
                        BasePreferencePortable<?> loadedSharedProperty = loadSharedPreference(field,
                                                                                              scopeResolutionStrategyInfo);
                        field.set(portablePreference,
                                  loadedSharedProperty);
                    } else {
                        final BasePreferencePortable<?> subPreferenceValue = loadSubPreferenceValue(portablePreference,
                                                                                                    field,
                                                                                                    scopeResolutionStrategyInfo);
                        field.set(portablePreference,
                                  subPreferenceValue);
                    }
                }
            }
        }

        return portablePreference;
    }

    private <U extends BasePreference<U>, T extends BasePreferencePortable<U>> T loadSharedPreference(final Field field,
                                                                                                      final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo) {
        final Class<U> propertyType = (Class<U>) field.getType();
        T loadedPreference;

        try {
            T emptyPortablePreference = lookupPortablePreference(propertyType);
            T portablePreference = preferenceStore.get(scopeResolutionStrategyInfo,
                                                       emptyPortablePreference.identifier());
            if (portablePreference == null) {
                portablePreference = (T) emptyPortablePreference.defaultValue((U) emptyPortablePreference);
            }
            loadedPreference = load(propertyType,
                                    portablePreference,
                                    scopeResolutionStrategyInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return loadedPreference;
    }

    private <U extends BasePreference<U>, T extends BasePreferencePortable<U>> T loadSubPreferenceValue(final Object portablePreference,
                                                                                                        final Field field,
                                                                                                        final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo) throws IllegalAccessException {
        final Class<U> propertyType = (Class<U>) field.getType();
        final T subPreferenceValue = (T) field.get(portablePreference);
        return load(propertyType,
                    subPreferenceValue,
                    scopeResolutionStrategyInfo);
    }

    private <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save(final Class<U> clazz,
                                                                                         final T portablePreference,
                                                                                         final PreferenceScope scope) throws IllegalAccessException {
        for (Field field : portablePreference.getPojoClass().getDeclaredFields()) {
            Property propertyAnnotation = field.getAnnotation(Property.class);
            if (propertyAnnotation != null) {
                if (field.getType().isAnnotationPresent(WorkbenchPreference.class)) {
                    boolean shared = propertyAnnotation.shared();

                    field.setAccessible(true);

                    if (shared) {
                        saveSharedPreference(portablePreference,
                                             field,
                                             scope);
                    } else {
                        saveSubPreference(portablePreference,
                                          field,
                                          scope);
                    }
                }
            }
        }
    }

    private <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void saveSharedPreference(final Object portablePreference,
                                                                                                         final Field field,
                                                                                                         final PreferenceScope scope) throws IllegalAccessException {
        final Class<U> propertyType = (Class<U>) field.getType();
        final T sharedPropertyValue = (T) field.get(portablePreference);
        save(sharedPropertyValue,
             scope);
    }

    private <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void saveSubPreference(final Object portablePreference,
                                                                                                      final Field field,
                                                                                                      final PreferenceScope scope) throws IllegalAccessException {
        final Class<U> propertyType = (Class<U>) field.getType();
        final T subPreferenceValue = (T) field.get(portablePreference);
        save(propertyType,
             subPreferenceValue,
             scope);
    }

    private <T extends BasePreference<T>> void saveOne(final BasePreferencePortable<?> portablePreference,
                                                       final PreferenceScope scope) {
        Class<T> clazz = (Class<T>) portablePreference.getPojoClass();
        try {
            save(clazz,
                 (BasePreferencePortable<T>) portablePreference,
                 scope);
            if (portablePreference.isPersistable()) {
                preferenceStore.put(scope,
                                    portablePreference.identifier(),
                                    portablePreference);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private List<BasePreferencePortable> getAnnotatedChildren(String parentIdentifier) {
        if (childrenByParent == null) {
            childrenByParent = new HashMap<>();

            final Iterable<Preference> portablePreferences = getPortablePreferences();
            portablePreferences.forEach(preference -> {
                final BasePreferencePortable portablePreference = (BasePreferencePortable) preference;
                final String[] parents = portablePreference.parents();

                for (String parent : parents) {
                    if (parent != null && !parent.isEmpty()) {
                        List<BasePreferencePortable> children = childrenByParent.computeIfAbsent(parent, k -> new ArrayList<>());
                        children.add(portablePreference);
                    }
                }
            });
        }

        return childrenByParent.get(parentIdentifier);
    }

    private <T> PreferenceHierarchyElement<T> buildHierarchyElement(final BasePreferencePortable<T> portablePreference,
                                                                    final PreferenceHierarchyElement<?> parent,
                                                                    final boolean shared,
                                                                    final boolean root,
                                                                    final String bundleKey,
                                                                    final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo) {
        PreferenceHierarchyElement<T> hierarchyElement = new PreferenceHierarchyElement<>(UUID.randomUUID().toString(),
                                                                                          portablePreference,
                                                                                          shared,
                                                                                          root,
                                                                                          bundleKey);

        buildHierarchyElementForAnnotatedChildren(portablePreference,
                                                  hierarchyElement,
                                                  scopeResolutionStrategyInfo);

        try {
            hierarchyElement.setPortablePreference(portablePreference);

            for (Field field : portablePreference.getPojoClass().getDeclaredFields()) {
                Property propertyAnnotation = field.getAnnotation(Property.class);
                if (propertyAnnotation != null) {
                    String propertyBundleKey = "";
                    if (!propertyAnnotation.bundleKey().isEmpty()) {
                        propertyBundleKey = propertyAnnotation.bundleKey();
                    }

                    if (field.getType().isAnnotationPresent(WorkbenchPreference.class)) {
                        field.setAccessible(true);
                        final BasePreferencePortable fieldValue = (BasePreferencePortable) field.get(portablePreference);

                        if (propertyBundleKey.isEmpty()) {
                            propertyBundleKey = fieldValue.bundleKey();
                        }

                        final PreferenceHierarchyElement<?> childElement = buildHierarchyElement(fieldValue,
                                                                                                 hierarchyElement,
                                                                                                 propertyAnnotation.shared(),
                                                                                                 false,
                                                                                                 propertyBundleKey,
                                                                                                 scopeResolutionStrategyInfo);

                        hierarchyElement.getChildren().add(childElement);
                    } else {
                        if (propertyBundleKey.isEmpty()) {
                            propertyBundleKey = field.getName();
                        }

                        hierarchyElement.addPropertyBundleKey(field.getName(),
                                                              propertyBundleKey);
                    }

                    hierarchyElement.addPropertyHelpBundleKey(field.getName(),
                                                              propertyAnnotation.helpBundleKey());
                    hierarchyElement.addPropertyFormOptions(field.getName(),
                                                            propertyAnnotation.formOptions());
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return hierarchyElement;
    }

    private <T> void buildHierarchyElementForAnnotatedChildren(final BasePreferencePortable<T> portablePreference,
                                                               final PreferenceHierarchyElement<T> hierarchyElement,
                                                               final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo) {
        final List<BasePreferencePortable> annotatedChildren = getAnnotatedChildren(portablePreference.identifier());
        if (annotatedChildren != null) {
            annotatedChildren.forEach(childPreference -> {
                final BasePreferencePortable<?> loadedChild = load(childPreference,
                                                                   scopeResolutionStrategyInfo);
                final PreferenceHierarchyElement<?> childElement = buildHierarchyElement(loadedChild,
                                                                                         hierarchyElement,
                                                                                         false,
                                                                                         true,
                                                                                         childPreference.bundleKey(),
                                                                                         scopeResolutionStrategyInfo);

                hierarchyElement.getChildren().add(childElement);
            });
        }
    }

    BasePreferencePortable getPortablePreferenceByIdentifier(String identifier) {
        for (Preference preference : getPortablePreferences()) {
            BasePreferencePortable portablePreference = (BasePreferencePortable) preference;

            if (portablePreference.identifier().equals(identifier)) {
                return portablePreference;
            }
        }

        return null;
    }

    Iterable<Preference> getPortablePreferences() {
        return preferences.select(portablePreferenceAnnotation);
    }

    <U extends BasePreference<U>, T extends BasePreferencePortable<U>> T lookupPortablePreference(final Class<U> clazz) {
        return (T) preferences.select(clazz,
                                      portablePreferenceAnnotation).get();
    }
}