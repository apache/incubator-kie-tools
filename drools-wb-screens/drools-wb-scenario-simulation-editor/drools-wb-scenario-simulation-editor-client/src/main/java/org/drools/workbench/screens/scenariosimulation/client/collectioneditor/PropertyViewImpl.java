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
package org.drools.workbench.screens.scenariosimulation.client.collectioneditor;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

/**
 * This class is used to show a single <b>property</b> inside an <code>ItemElementView</code>
 */
@Templated
public class PropertyViewImpl implements PropertyView {

    @DataField("propertyFields")
    protected LIElement propertyFields = Document.get().createLIElement();

    @DataField("propertyName")
    protected SpanElement propertyName = Document.get().createSpanElement();

    @DataField("propertyValueSpan")
    protected SpanElement propertyValueSpan = Document.get().createSpanElement();

    @DataField("propertyValueInput")
    protected InputElement propertyValueInput = Document.get().createTextInputElement();

    @Override
    public LIElement getPropertyFields() {
        return propertyFields;
    }

    @Override
    public SpanElement getPropertyName() {
        return propertyName;
    }

    @Override
    public SpanElement getPropertyValueSpan() {
        return propertyValueSpan;
    }

    public InputElement getPropertyValueInput() {
        return propertyValueInput;
    }

    @EventHandler("propertyValueInput")
    public void onPropertyValueInputClickEvent(ClickEvent clickEvent) {
        propertyValueInput.focus();
        clickEvent.stopPropagation();
    }
}
