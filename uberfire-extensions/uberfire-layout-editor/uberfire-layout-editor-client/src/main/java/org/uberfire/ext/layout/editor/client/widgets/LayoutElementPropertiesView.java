/*
 * Copyright 2018 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.layout.editor.client.widgets;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.properties.editor.client.PropertyEditorWidget;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class LayoutElementPropertiesView implements LayoutElementPropertiesPresenter.View {

    private LayoutElementPropertiesPresenter presenter;
    private PropertyEditorWidget propertyEditor;
    private List<PropertyEditorCategory> categories = new ArrayList<>();

    private final String PROPERTY_EDITOR_ID = Document.get().createUniqueId();

    @Override
    public void init(LayoutElementPropertiesPresenter presenter) {
        this.presenter = presenter;
        this.propertyEditor = new PropertyEditorWidget();

    }

    @Override
    public Widget asWidget() {
        return propertyEditor;
    }

    @Override
    public void clear() {
        categories.clear();
    }

    @Override
    public void addCategory(PropertyEditorCategory category) {
        categories.add(category);
    }

    @Override
    public void show() {
        propertyEditor.handle(new PropertyEditorEvent(PROPERTY_EDITOR_ID, categories));
    }

    /**
     * Capture & process the modification events sent by the property editor
     */
    protected void onPropertyEditorChange(@Observes PropertyEditorChangeEvent event) {
        PropertyEditorFieldInfo property = event.getProperty();
        if (property.getEventId().equalsIgnoreCase(PROPERTY_EDITOR_ID)) {
            String attrKey = property.getKey();
            String attrValue = event.getNewValue();
            presenter.onPropertyChanged(attrKey, attrValue);
        }
    }
}