/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.layout.editor.api.editor;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class LayoutComponentPart {
    
    private String partId;
    
    private Map<String, String> cssProperties = new HashMap<>();

    public LayoutComponentPart() {
    }
    
    public LayoutComponentPart(String partId) {
        this.partId = partId;
    }
    
    public LayoutComponentPart(String partId, Map<String, String> properties) {
        this.partId = partId;
        this.cssProperties = properties;
    }

    public String getPartId() {
        return partId;
    }

    public Map<String, String> getCssProperties() {
        return cssProperties;
    }
    
    public void clearCssProperties() {
        cssProperties.clear();
    }
    
    public void addCssProperty(String key, String value) {
        cssProperties.put(key, value);
    }

    
    public void removeCssProperty(String property) {
        cssProperties.remove(property);
        
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = ~~result;
        result = prime * result + ((partId == null) ? 0 : partId.hashCode());
        result = ~~result;
        result = prime * result + ((cssProperties == null) ? 0 : cssProperties.hashCode());
        result = ~~result;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LayoutComponentPart other = (LayoutComponentPart) obj;
        if (partId == null) {
            if (other.partId != null) {
                return false;
            }
        } else if (!partId.equals(other.partId)) {
            return false;
        }
        if (cssProperties == null) {
            if (other.cssProperties != null) {
                return false;
            }
        } else if (!cssProperties.equals(other.cssProperties)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LayoutComponentPart [partId=" + partId + ", properties=" + cssProperties + "]";
    }
    
}