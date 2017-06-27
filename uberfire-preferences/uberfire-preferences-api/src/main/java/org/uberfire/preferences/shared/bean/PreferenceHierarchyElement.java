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

package org.uberfire.preferences.shared.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.preferences.shared.PropertyFormOptions;

/**
 * Holds all preference information necessary to build a hierarchic interface for preferences.
 * @param <T> Preference bean type.
 */
@Portable
public class PreferenceHierarchyElement<T> {

    private String id;

    private BasePreferencePortable<T> portablePreference;

    private List<PreferenceHierarchyElement<?>> children;

    private boolean shared;

    private boolean root;

    private String bundleKey;

    private Map<String, String> bundleKeyByProperty;

    private Map<String, String> helpBundleKeyByProperty;

    private Map<String, PropertyFormOptions[]> formOptionsByProperty;

    public PreferenceHierarchyElement() {
        this(null,
             null,
             false,
             false,
             null);
    }

    public PreferenceHierarchyElement(final String id,
                                      final BasePreferencePortable<T> portablePreference,
                                      final boolean shared,
                                      final boolean root,
                                      final String bundleKey) {
        this(id,
             portablePreference,
             new ArrayList<>(),
             shared,
             root,
             bundleKey,
             new HashMap<>(),
             new HashMap<>(),
             new HashMap<>());
    }

    public PreferenceHierarchyElement(@MapsTo("id") final String id,
                                      @MapsTo("portablePreference") final BasePreferencePortable<T> portablePreference,
                                      @MapsTo("children") final List<PreferenceHierarchyElement<?>> children,
                                      @MapsTo("shared") final boolean shared,
                                      @MapsTo("root") final boolean root,
                                      @MapsTo("bundleKey") final String bundleKey,
                                      @MapsTo("bundleKeyByProperty") final Map<String, String> bundleKeyByProperty,
                                      @MapsTo("helpBundleKeyByProperty") final Map<String, String> helpBundleKeyByProperty,
                                      @MapsTo("formOptionsByProperty") final Map<String, PropertyFormOptions[]> formOptionsByProperty) {
        this.id = id;
        this.portablePreference = portablePreference;
        this.children = children;
        this.shared = shared;
        this.root = root;
        this.bundleKey = bundleKey;
        this.bundleKeyByProperty = bundleKeyByProperty;
        this.helpBundleKeyByProperty = helpBundleKeyByProperty;
        this.formOptionsByProperty = formOptionsByProperty;
    }

    public boolean isSelectable() {
        final boolean hasProperties = bundleKeyByProperty != null && bundleKeyByProperty.size() > 0;
        return hasProperties;
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public BasePreferencePortable<T> getPortablePreference() {
        return portablePreference;
    }

    public void setPortablePreference(final BasePreferencePortable<T> portablePreference) {
        this.portablePreference = portablePreference;
    }

    public List<PreferenceHierarchyElement<?>> getChildren() {
        return children;
    }

    public void setChildren(final List<PreferenceHierarchyElement<?>> children) {
        this.children = children;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(final boolean shared) {
        this.shared = shared;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(final boolean root) {
        this.root = root;
    }

    public String getBundleKey() {
        return bundleKey;
    }

    public void setBundleKey(final String bundleKey) {
        this.bundleKey = bundleKey;
    }

    public void addPropertyBundleKey(final String propertyFieldName,
                                     final String bundleKey) {
        bundleKeyByProperty.put(propertyFieldName,
                                bundleKey);
    }

    public Map<String, String> getBundleKeyByProperty() {
        return bundleKeyByProperty;
    }

    public void addPropertyHelpBundleKey(final String propertyFieldName,
                                         final String helpBundleKey) {
        helpBundleKeyByProperty.put(propertyFieldName,
                                    helpBundleKey);
    }

    public Map<String, String> getHelpBundleKeyByProperty() {
        return helpBundleKeyByProperty;
    }

    public void addPropertyFormOptions(final String propertyFieldName,
                                       final PropertyFormOptions[] formOptions) {
        formOptionsByProperty.put(propertyFieldName,
                                  formOptions);
    }

    public Map<String, PropertyFormOptions[]> getFormOptionsByProperty() {
        return formOptionsByProperty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PreferenceHierarchyElement)) {
            return false;
        }

        PreferenceHierarchyElement<?> that = (PreferenceHierarchyElement<?>) o;

        if (isShared() != that.isShared()) {
            return false;
        }
        if (isRoot() != that.isRoot()) {
            return false;
        }
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
            return false;
        }
        if (getPortablePreference() != null ? !getPortablePreference().equals(that.getPortablePreference()) : that.getPortablePreference() != null) {
            return false;
        }
        if (getChildren() != null ? !getChildren().equals(that.getChildren()) : that.getChildren() != null) {
            return false;
        }
        if (getBundleKey() != null ? !getBundleKey().equals(that.getBundleKey()) : that.getBundleKey() != null) {
            return false;
        }
        if (getBundleKeyByProperty() != null ? !getBundleKeyByProperty().equals(that.getBundleKeyByProperty()) : that.getBundleKeyByProperty() != null) {
            return false;
        }
        if (getHelpBundleKeyByProperty() != null ? !getHelpBundleKeyByProperty().equals(that.getHelpBundleKeyByProperty()) : that.getHelpBundleKeyByProperty() != null) {
            return false;
        }
        return !(getFormOptionsByProperty() != null ? !getFormOptionsByProperty().equals(that.getFormOptionsByProperty()) : that.getFormOptionsByProperty() != null);
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = ~~result;
        result = 31 * result + (getPortablePreference() != null ? getPortablePreference().hashCode() : 0);
        result = ~~result;
        result = 31 * result + (getChildren() != null ? getChildren().hashCode() : 0);
        result = ~~result;
        result = 31 * result + (isShared() ? 1 : 0);
        result = ~~result;
        result = 31 * result + (isRoot() ? 1 : 0);
        result = ~~result;
        result = 31 * result + (getBundleKey() != null ? getBundleKey().hashCode() : 0);
        result = ~~result;
        result = 31 * result + (getBundleKeyByProperty() != null ? getBundleKeyByProperty().hashCode() : 0);
        result = ~~result;
        result = 31 * result + (getHelpBundleKeyByProperty() != null ? getHelpBundleKeyByProperty().hashCode() : 0);
        result = ~~result;
        result = 31 * result + (getFormOptionsByProperty() != null ? getFormOptionsByProperty().hashCode() : 0);
        result = ~~result;
        return result;
    }
}
