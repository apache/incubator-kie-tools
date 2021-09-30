/*
* Copyright 2015 JBoss Inc
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
package org.uberfire.ext.layout.editor.client.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LayoutDragComponentGroup {

    private String name;

    private Map<String, LayoutDragComponent> components = new HashMap<String, LayoutDragComponent>();

    private boolean expandeByDefault = false;

    public LayoutDragComponentGroup(String name) {
        this.name = name;
    }

    public LayoutDragComponentGroup(String name, boolean expandeByDefault) {
        this.name = name;
        this.expandeByDefault = expandeByDefault;
    }

    public String getName() {
        return name;
    }

    public boolean isExpandeByDefault() {
        return expandeByDefault;
    }

    public void addLayoutDragComponent(String id,
                                       LayoutDragComponent component) {
        components.put(id,
                       component);
    }

    public Set<String> getLayoutDragComponentIds() {
        return components.keySet();
    }

    public LayoutDragComponent getLayoutDragComponent(String id) {
        return components.get(id);
    }

    public Map<String, LayoutDragComponent> getComponents() {
        return components;
    }
}
