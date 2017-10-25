/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.model;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Identifier of a provider type.
 */
@Portable
public class ProviderTypeKey {

    private String id;

    private String version;

    public ProviderTypeKey(@MapsTo("id") final String id,
                           @MapsTo("version") final String version) {
        this.id = id;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProviderTypeKey that = (ProviderTypeKey) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        return version != null ? version.equals(that.version) : that.version == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "ProviderTypeKey{" +
                "id='" + id + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}