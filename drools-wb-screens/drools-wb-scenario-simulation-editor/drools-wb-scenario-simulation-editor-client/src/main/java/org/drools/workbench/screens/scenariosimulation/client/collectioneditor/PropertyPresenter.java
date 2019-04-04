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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;

import static org.drools.workbench.screens.scenariosimulation.client.collectioneditor.CollectionEditorUtils.setSpanAttributeAttributes;

public class PropertyPresenter implements PropertyView.Presenter {

    @Inject
    protected ViewsProvider viewsProvider;

    /**
     * <code>Map</code> to pair a given <b>property</b> with the <code>SpanElement</code> showing its value
     */
    protected Map<String, SpanElement> propertySpanElementMap = new HashMap<>();

    /**
     * <code>Map</code> to pair a given <b>itemId</b> with its <code>PropertyEditorView</code>s
     */
    protected Map<String, List<PropertyView>> propertyViewMap = new HashMap<>();

    @Override
    public String getPropertyValue(String propertyName) throws Exception {
        if (propertySpanElementMap.containsKey(propertyName)) {
            return propertySpanElementMap.get(propertyName).getInnerText();
        } else {
            throw new Exception(propertyName + " not found");
        }
    }

    @Override
    public void editProperties(String itemId) {
        propertyViewMap.get(itemId)
                .forEach(this::startEditPropertyView);
    }

    @Override
    public void stopEditProperties(String itemId) {
        stopEdit(itemId, false);
    }

    @Override
    public Map<String, String> updateProperties(String itemId) {
        return stopEdit(itemId, true);
    }

    @Override
    public Map<String, String> getSimpleProperties(String itemId) {
        Map<String, String> toReturn = new HashMap<>();
        propertyViewMap.get(itemId)
                .forEach(propertyEditorView -> {
                    String propertyName = propertyEditorView.getPropertyName().getInnerText();
                    propertyName = propertyName.substring(propertyName.lastIndexOf("#") + 1);
                    toReturn.put(propertyName, propertyEditorView.getPropertyValueSpan().getInnerText());
                });
        return toReturn;
    }

    @Override
    public LIElement getPropertyFields(String itemId, String propertyName, String propertyValue) {
        final PropertyView propertyEditorView = viewsProvider.getPropertyEditorView();
        String hashedPropertyName = "#" + propertyName;
        final SpanElement propertyNameSpan = propertyEditorView.getPropertyName();
        setSpanAttributeAttributes(propertyName, hashedPropertyName, "propertyName" + hashedPropertyName, propertyNameSpan);
        final SpanElement propertyValueSpan = propertyEditorView.getPropertyValueSpan();
        setSpanAttributeAttributes(propertyName, propertyValue, "propertyValue" + hashedPropertyName, propertyValueSpan);
        propertySpanElementMap.put(propertyName, propertyValueSpan);
        final InputElement propertyValueInput = propertyEditorView.getPropertyValueInput();
        propertyValueInput.setAttribute("placeholder", hashedPropertyName);
        propertyValueInput.setAttribute("data-field", "propertyValue" + hashedPropertyName);
        propertyValueInput.setDisabled(true);
        propertyValueInput.getStyle().setDisplay(Style.Display.NONE);
        final LIElement propertyFields = propertyEditorView.getPropertyFields();
        propertyFields.setAttribute("data-field", "propertyFields" + hashedPropertyName);
        if (propertyViewMap.containsKey(itemId)) {
            propertyViewMap.get(itemId).add(propertyEditorView);
        } else {
            List<PropertyView> toPut = new ArrayList<>();
            toPut.add(propertyEditorView);
            propertyViewMap.put(itemId, toPut);
        }
        return propertyFields;
    }

    @Override
    public LIElement getEditingPropertyFields(String itemId, String propertyName, String propertyValue) {
        LIElement toReturn = getPropertyFields(itemId, propertyName, propertyValue);
        editProperties(itemId);
        return toReturn;
    }

    @Override
    public void onToggleRowExpansion(String itemId, boolean isShown) {
        propertyViewMap.get(itemId)
                .forEach(propertyEditorView -> CollectionEditorUtils.toggleRowExpansion(propertyEditorView.getPropertyFields(), isShown));
    }

    @Override
    public void deleteProperties(String itemId) {
        propertyViewMap.get(itemId)
                .forEach(this::deletePropertyView);
        propertyViewMap.remove(itemId);
    }

    protected Map<String, String> stopEdit(String itemId, boolean toUpdate) {
        Map<String, String> toReturn = new HashMap<>();
        propertyViewMap.get(itemId)
                .forEach(propertyEditorView -> {
                    stopEditPropertyView(toReturn, propertyEditorView, toUpdate);
                });
        return toReturn;
    }

    /**
     * Enable editing a property
     * @param toEdit
     */
    protected void startEditPropertyView(PropertyView toEdit) {
        toEdit.getPropertyValueSpan().getStyle().setDisplay(Style.Display.NONE);
        toEdit.getPropertyValueInput().setValue(toEdit.getPropertyValueSpan().getInnerText());
        toEdit.getPropertyValueInput().getStyle().setDisplay(Style.Display.INLINE);
        toEdit.getPropertyValueInput().setDisabled(false);
    }

    /**
     * Disable editing a property and put its value in given <code>Map</code>
     * @param toPopulate
     * @param toStopEdit
     * @param toUpdate
     */
    protected void stopEditPropertyView(Map<String, String> toPopulate, PropertyView toStopEdit, boolean toUpdate) {
        if (toUpdate) {
            toStopEdit.getPropertyValueSpan().setInnerText(toStopEdit.getPropertyValueInput().getValue());
        }
        toStopEdit.getPropertyValueSpan().getStyle().setDisplay(Style.Display.INLINE);
        toStopEdit.getPropertyValueInput().getStyle().setDisplay(Style.Display.NONE);
        toStopEdit.getPropertyValueInput().setDisabled(true);
        String propertyName = toStopEdit.getPropertyName().getInnerText();
        propertyName = propertyName.substring(propertyName.lastIndexOf("#") + 1);
        toPopulate.put(propertyName, toStopEdit.getPropertyValueSpan().getInnerText());
    }

    protected void deletePropertyView(PropertyView toDelete) {
        String propertyName = toDelete.getPropertyName().getAttribute("data-i18n-key");
        toDelete.getPropertyFields().removeFromParent();
        propertySpanElementMap.remove(propertyName);

    }
}
