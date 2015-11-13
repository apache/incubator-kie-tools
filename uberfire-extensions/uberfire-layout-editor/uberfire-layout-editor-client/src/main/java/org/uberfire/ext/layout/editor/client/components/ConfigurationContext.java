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
package org.uberfire.ext.layout.editor.client.components;

import java.util.Map;

import com.google.gwt.user.client.ui.Panel;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

/**
 * This class provides the context required during a layout component configuration
 */
public class ConfigurationContext {

    private LayoutComponent component;
    private Panel container;
    private LayoutComponentView view;

    public ConfigurationContext(LayoutComponent component, Panel container, LayoutComponentView view) {
        this.component = component;
        this.container = container;
        this.view = view;
    }

    public Panel getContainer() {
        return container;
    }

    public void setComponentProperty(String key, String property) {
        component.addProperty(key, property);
    }

    public void removeComponentProperty(String key) {
        component.getProperties().remove(key);
    }

    public String getComponentProperty(String key) {
        return component.getProperties().get(key);
    }

    public Map<String, String> getComponentProperties() {
        return component.getProperties();
    }

    public void resetComponentProperties() {
        component.getProperties().clear();
    }

    public void configurationFinished() {
        view.update();
    }

    public void configurationCancelled() {
        if (view.isNewComponent()) {
            view.remove();
        }
    }
}
