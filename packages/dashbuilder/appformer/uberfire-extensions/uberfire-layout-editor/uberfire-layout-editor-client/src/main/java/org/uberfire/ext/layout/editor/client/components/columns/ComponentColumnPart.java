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

package org.uberfire.ext.layout.editor.client.components.columns;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.ext.layout.editor.api.editor.LayoutComponentPart;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElement;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElementPart;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorCssHelper;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;

@Dependent
public class ComponentColumnPart implements LayoutEditorElementPart {
    
    @Inject
    public LayoutEditorCssHelper cssHelper;
    
    
    private LayoutComponentPart part;
    private ComponentColumn componentColumnElement;

    public void init(ComponentColumn layoutEditorElement, LayoutComponentPart part) {
        this.componentColumnElement = layoutEditorElement;
        this.part = part;
    }

    @Override
    public String getId() {
        return part.getPartId();
    }

    @Override
    public Map<String, String> getProperties() {
        return part.getCssProperties();
    }

    @Override
    public void clearProperties() {
        part.clearCssProperties();
        componentColumnElement.updateView();
    }

    @Override
    public void setProperty(String property, String value) {
        part.addCssProperty(property, value);
        componentColumnElement.updateView();
    }

    @Override
    public void removeProperty(String property) {
        part.removeCssProperty(property);
        componentColumnElement.updateView();

    }

    @Override
    public List<PropertyEditorCategory> getPropertyCategories() {
        return cssHelper.allCategories(part.getCssProperties());
    }

    @Override
    public LayoutEditorElement getParent() {
        return componentColumnElement;
    }
    
    @Override
    public void setSelected(boolean status) {
        getParent().setSelected(status);
    }

}