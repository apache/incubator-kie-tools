/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.layout.editor.api.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class LayoutComponent {

    private String dragTypeName;

    private Map<String, String> properties = new HashMap<>();
    
    private List<LayoutComponentPart> parts = new ArrayList<>();
    
    public LayoutComponent() {
    }

    public LayoutComponent(String dragType) {
        this.dragTypeName = dragType;
    }

    public String getDragTypeName() {
        return dragTypeName;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
    
    public void addProperty(String key,
                            String value) {
        properties.put(key,
                       value);
    }
    
    public void addPartProperty(String partId, 
                                String key,
                                String value) {
        parts.stream().filter(p -> p.getPartId().equals(partId))
                      .findFirst()
                      .ifPresent(part -> part.addCssProperty(key, value));
    }

    public void addProperties(Map<String, String> properties) {
        properties.forEach(this.properties::put);
    }
    
    public void addPartProperties(String partId, Map<String, String> properties) {
        parts.stream().filter(p -> p.getPartId().equals(partId))
                      .findFirst()
                      .ifPresent(part -> properties.forEach(part::addCssProperty));
    }
    
    public void addPartIfAbsent(String partId) {
        Optional<LayoutComponentPart> containsPart = parts.stream().filter(p -> p.getPartId()
                                                                   .equals(partId)).findFirst();
        if (!containsPart.isPresent()) {
            parts.add(new LayoutComponentPart(partId));
        }
    }
    
    public void removePartIf(Predicate<String> condition) {
        parts.removeIf(p -> condition.test(p.getPartId()));
    }
    
    public List<LayoutComponentPart> getParts() {
        return Collections.unmodifiableList(parts);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = ~~result;
        result = prime * result + ((dragTypeName == null) ? 0 : dragTypeName.hashCode());
        result = ~~result;
        result = prime * result + ((parts == null) ? 0 : parts.hashCode());
        result = ~~result;
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
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
        LayoutComponent other = (LayoutComponent) obj;
        if (dragTypeName == null) {
            if (other.dragTypeName != null) {
                return false;
            }
        } else if (!dragTypeName.equals(other.dragTypeName))
            return false;
        if (parts == null) {
            if (other.parts != null) {
                return false;
            }
        } else if (!parts.equals(other.parts)) {
            return false;
        }
        if (properties == null) {
            if (other.properties != null) {
                return false;
            }
        } else if (!properties.equals(other.properties)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LayoutComponent [dragTypeName=" + dragTypeName 
                                + ", properties=" + properties 
                                + ", parts=" + parts + "]";
    }


}
