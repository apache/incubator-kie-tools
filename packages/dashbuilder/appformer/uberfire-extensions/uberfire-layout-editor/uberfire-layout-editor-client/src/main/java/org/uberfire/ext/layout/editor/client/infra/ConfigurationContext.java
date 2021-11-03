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
package org.uberfire.ext.layout.editor.client.infra;

import java.util.Map;
import java.util.function.Supplier;

import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.mvp.Command;

/**
 * This class provides the context required during a layout component configuration
 */
public class ConfigurationContext {

    private LayoutComponent component;
    private Command configurationFinish;
    private Command configurationCanceled;
    private Supplier<LayoutTemplate> currentLayoutTemplateSupplier;

    public ConfigurationContext(LayoutComponent component,
                                Command configurationFinish,
                                Command configurationCanceled,
                                Supplier<LayoutTemplate> currentLayoutTemplateSupplier) {
        this.component = component;
        this.configurationFinish = configurationFinish;
        this.configurationCanceled = configurationCanceled;
        this.currentLayoutTemplateSupplier = currentLayoutTemplateSupplier;
    }

    public void setComponentProperty(String key,
                                     String property) {
        component.addProperty(key,
                              property);
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
        configurationFinish.execute();
    }

    public void configurationCancelled() {
        configurationCanceled.execute();
    }

    public LayoutTemplate getCurrentLayoutTemplate() {
        return currentLayoutTemplateSupplier.get();
    }

    public Command getConfigurationCanceled() {
        return configurationCanceled;
    }

    public void setConfigurationCanceled(final Command configurationCanceled) {
        this.configurationCanceled = configurationCanceled;
    }

    public Command getConfigurationFinish() {
        return configurationFinish;
    }

    public void setConfigurationFinish(final Command configurationFinish) {
        this.configurationFinish = configurationFinish;
    }
    
    public LayoutComponent getLayoutComponent() {
        return this.component;
    }
}
