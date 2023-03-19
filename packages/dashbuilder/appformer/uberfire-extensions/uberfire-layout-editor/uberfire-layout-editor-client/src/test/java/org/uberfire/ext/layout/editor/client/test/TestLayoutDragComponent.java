/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.layout.editor.client.test;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;

public abstract class TestLayoutDragComponent implements LayoutDragComponent {

    private String identifier;

    public TestLayoutDragComponent(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getDragComponentIconClass() {
        return null;
    }

    @Override
    public List<PropertyEditorCategory> getPropertyCategories(LayoutComponent layoutComponent) {
        return null;
    }

    @Override
    public String getDragComponentTitle() {
        return null;
    }

    @Override
    public IsWidget getPreviewWidget(RenderingContext ctx) {
        return null;
    }

    @Override
    public IsWidget getShowWidget(RenderingContext ctx) {
        return null;
    }

    @Override
    public void removeCurrentWidget(RenderingContext ctx) {

    }
}
