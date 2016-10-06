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

package org.uberfire.ext.preferences.shared.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

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

    public PreferenceHierarchyElement() {
        this( null, null, false, false, null );
    }

    public PreferenceHierarchyElement( final String id,
                                       final BasePreferencePortable<T> portablePreference,
                                       final boolean shared,
                                       final boolean root,
                                       final String bundleKey ) {

        this( id, portablePreference, new ArrayList<>(), shared, root, bundleKey, new HashMap<>() );
    }

    public PreferenceHierarchyElement( @MapsTo( "id" ) final String id,
                                       @MapsTo( "portablePreference" ) final BasePreferencePortable<T> portablePreference,
                                       @MapsTo( "children" ) final List<PreferenceHierarchyElement<?>> children,
                                       @MapsTo( "shared" ) final boolean shared,
                                       @MapsTo( "root" ) final boolean root,
                                       @MapsTo( "bundleKey" ) final String bundleKey,
                                       @MapsTo( "bundleKeyByProperty" ) final Map<String, String> bundleKeyByProperty ) {
        this.id = id;
        this.portablePreference = portablePreference;
        this.children = children;
        this.shared = shared;
        this.root = root;
        this.bundleKey = bundleKey;
        this.bundleKeyByProperty = bundleKeyByProperty;
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public String getId() {
        return id;
    }

    public void setId( final String id ) {
        this.id = id;
    }

    public BasePreferencePortable<T> getPortablePreference() {
        return portablePreference;
    }

    public void setPortablePreference( final BasePreferencePortable<T> portablePreference ) {
        this.portablePreference = portablePreference;
    }

    public List<PreferenceHierarchyElement<?>> getChildren() {
        return children;
    }

    public void setChildren( final List<PreferenceHierarchyElement<?>> children ) {
        this.children = children;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared( final boolean shared ) {
        this.shared = shared;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot( final boolean root ) {
        this.root = root;
    }

    public String getBundleKey() {
        return bundleKey;
    }

    public void setBundleKey( final String bundleKey ) {
        this.bundleKey = bundleKey;
    }

    public void addPropertyBundleKey( final String propertyFieldName, final String bundleKey ) {
        bundleKeyByProperty.put( propertyFieldName, bundleKey );
    }

    public Map<String, String> getBundleKeyByProperty() {
        return bundleKeyByProperty;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof PreferenceHierarchyElement ) ) {
            return false;
        }

        final PreferenceHierarchyElement<?> that = (PreferenceHierarchyElement<?>) o;

        if ( shared != that.shared ) {
            return false;
        }
        if ( root != that.root ) {
            return false;
        }
        if ( id != null ? !id.equals( that.id ) : that.id != null ) {
            return false;
        }
        if ( portablePreference != null ? !portablePreference.equals( that.portablePreference ) : that.portablePreference != null ) {
            return false;
        }
        if ( children != null ? !children.equals( that.children ) : that.children != null ) {
            return false;
        }
        if ( bundleKey != null ? !bundleKey.equals( that.bundleKey ) : that.bundleKey != null ) {
            return false;
        }
        return !( bundleKeyByProperty != null ? !bundleKeyByProperty.equals( that.bundleKeyByProperty ) : that.bundleKeyByProperty != null );

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( portablePreference != null ? portablePreference.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( children != null ? children.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( shared ? 1 : 0 );
        result = ~~result;
        result = 31 * result + ( root ? 1 : 0 );
        result = ~~result;
        result = 31 * result + ( bundleKey != null ? bundleKey.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( bundleKeyByProperty != null ? bundleKeyByProperty.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
