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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PreferenceRootElement {

    private String identifier;

    private String category;

    private String iconCss;

    private String bundleKey;

    public PreferenceRootElement() {
    }

    public PreferenceRootElement(@MapsTo("identifier") final String identifier,
                                 @MapsTo("category") final String category,
                                 @MapsTo("iconCss") final String iconCss,
                                 @MapsTo("bundleKey") final String bundleKey) {
        this.identifier = identifier;
        this.category = category;
        this.iconCss = iconCss;
        this.bundleKey = bundleKey;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getCategory() {
        return category;
    }

    public String getIconCss() {
        return iconCss;
    }

    public String getBundleKey() {
        return bundleKey;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PreferenceRootElement)) {
            return false;
        }

        final PreferenceRootElement that = (PreferenceRootElement) o;

        if (identifier != null ? !identifier.equals(that.identifier) : that.identifier != null) {
            return false;
        }
        if (category != null ? !category.equals(that.category) : that.category != null) {
            return false;
        }
        if (iconCss != null ? !iconCss.equals(that.iconCss) : that.iconCss != null) {
            return false;
        }
        return !(bundleKey != null ? !bundleKey.equals(that.bundleKey) : that.bundleKey != null);
    }

    @Override
    public int hashCode() {
        int result = identifier != null ? identifier.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (iconCss != null ? iconCss.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (bundleKey != null ? bundleKey.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
