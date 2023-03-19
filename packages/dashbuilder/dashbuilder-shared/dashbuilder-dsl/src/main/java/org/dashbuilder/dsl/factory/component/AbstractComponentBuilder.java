/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dsl.factory.component;

import org.dashbuilder.dsl.model.Component;
import org.uberfire.ext.layout.editor.api.css.CssProperty;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public abstract class AbstractComponentBuilder<T> {

    private LayoutComponent layoutComponent;

    AbstractComponentBuilder() {
        String dragType = getDragType();
        this.layoutComponent = new LayoutComponent(dragType);
    }

    public Component build() {
        return Component.create(this.layoutComponent);
    }

    @SuppressWarnings("unchecked")
    public T property(String key, String value) {
        this.layoutComponent.addProperty(key, value);
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T cssProperty(CssProperty property, String value) {
        this.property(property.getName(), value);
        return (T) this;
    }

    abstract String getDragType();

}