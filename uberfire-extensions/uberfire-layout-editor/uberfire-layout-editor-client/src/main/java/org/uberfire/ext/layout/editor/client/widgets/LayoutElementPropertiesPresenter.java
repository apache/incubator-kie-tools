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

import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.api.css.CssAllowedValue;
import org.uberfire.ext.layout.editor.client.api.LayoutElementWithProperties;
import org.uberfire.ext.layout.editor.client.event.LayoutElementClearAllPropertiesEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutElementPropertyChangedEvent;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorCssHelper;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Dependent
public class LayoutElementPropertiesPresenter {

    public interface View extends UberView<LayoutElementPropertiesPresenter> {

        void clear();

        void addCategory(PropertyEditorCategory category);

        void show();
    }

    private View view;
    private LayoutElementWithProperties layoutElement;
    private LayoutEditorCssHelper cssHelper;
    Event<LayoutElementPropertyChangedEvent> propertyChangedEvent;
    Event<LayoutElementClearAllPropertiesEvent> propertyClearAllEvent;
    Map<String,String> currentValues = new HashMap<>();

    public LayoutElementPropertiesPresenter() {
    }

    @Inject
    public LayoutElementPropertiesPresenter(final View view,
            final LayoutEditorCssHelper cssHelper,
            final Event<LayoutElementPropertyChangedEvent> propertyChangedEvent,
            final Event<LayoutElementClearAllPropertiesEvent> propertyClearAllEvent) {
        this.view = view;
        this.cssHelper = cssHelper;
        this.propertyChangedEvent = propertyChangedEvent;
        this.propertyClearAllEvent = propertyClearAllEvent;
        view.init(this);
    }

    public UberView<LayoutElementPropertiesPresenter> getView() {
        return view;
    }

    public LayoutElementWithProperties getLayoutElement() {
        return layoutElement;
    }

    public boolean hasValues() {
        return !currentValues.isEmpty();
    }

    public Map<String, String> getCurrentValues() {
        return currentValues;
    }

    public void edit(LayoutElementWithProperties element) {
        this.layoutElement = element;

        view.clear();

        layoutElement.getPropertyCategories().forEach(category -> {
            view.addCategory(category);

            category.getFields().forEach(field -> {
                String prop = field.getKey();
                String val = field.getCurrentStringValue();
                if (val != null && !val.isEmpty()) {
                    currentValues.put(prop, val);
                }
            });
        });
        view.show();
    }

    public void reset() {
        if (layoutElement != null) {
            currentValues.clear();

            layoutElement.getPropertyCategories()
                    .forEach(category -> category.getFields()
                            .forEach(field -> {
                                String property = field.getKey();
                                layoutElement.removeProperty(property);
                                field.setCurrentStringValue("");
                            }));

            this.edit(layoutElement);

            propertyClearAllEvent.fire(new LayoutElementClearAllPropertiesEvent(layoutElement));
        }
    }

    // View callbacks

    public void onPropertyChanged(String property, String value) {
        if (layoutElement != null) {
            String oldValue = layoutElement.getProperties().get(property);

            if (value == null || value.isEmpty()) {
                layoutElement.removeProperty(property);
                currentValues.remove(property);
            }
            else {
                // CSS allowed values requires parsing
                CssAllowedValue cssAllowedValue = cssHelper.parseCssAllowedValue(property, value);
                if (cssAllowedValue != null) {
                    layoutElement.setProperty(property, cssAllowedValue.getName());
                    currentValues.put(property, cssAllowedValue.getName());
                } else {
                    layoutElement.setProperty(property, value);
                    currentValues.put(property, value);
                }
            }
            propertyChangedEvent.fire(new LayoutElementPropertyChangedEvent(layoutElement, property, oldValue, value));
        }
    }
}
