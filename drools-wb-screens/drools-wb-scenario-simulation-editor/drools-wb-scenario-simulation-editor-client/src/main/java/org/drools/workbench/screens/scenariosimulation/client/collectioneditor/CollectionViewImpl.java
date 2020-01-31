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

import java.util.Map;

import javax.inject.Inject;

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FocusWidget;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.events.CloseCompositeEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SaveEditorEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.CloseCompositeEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.HasCloseCompositeHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.HasSaveEditorHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.SaveEditorEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder;
import org.drools.workbench.screens.scenariosimulation.client.utils.ExpressionUtils;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type.RULE;

/**
 * This class is used as <code>Collection</code> <b>editor</b>
 * <p>
 * The overall architecture is:
 * <p>this widget contains a series of elements</p>
 * <p>if this widget represent a list, each element will show a single item of it, represented by a <code>ListEditorElementViewImpl</code></p>
 * <p>if this widget represent a map, each element will show a single entry (key/value) of it, represented by a <code>MapEditorElementViewImpl</code></p>
 * <p><code>PropertyEditorViewImpl</code> represents a single property (label with name and textbox for value)</p>
 * <p>each item/key/value could be a simple java object or a complex one</p>
 * <p>for complex java object, for each property a <code>PropertyEditorViewImpl</code> will be created</p>
 * <p>the presenter will be responsible to choose which kind of elements are to be populated</p>
 */
@Templated
public class CollectionViewImpl extends FocusWidget implements HasCloseCompositeHandler,
                                                               HasSaveEditorHandler,
                                                               CollectionView {

    @Inject
    protected CollectionPresenter presenter;

    @DataField("collectionEditor")
    protected DivElement collectionEditor = Document.get().createDivElement();

    @DataField("collectionEditorModalDialog")
    protected DivElement collectionEditorModalDialog = Document.get().createDivElement();

    @DataField("collectionEditorModalBody")
    protected DivElement collectionEditorModalBody = Document.get().createDivElement();

    @DataField("elementsContainer")
    protected UListElement elementsContainer = Document.get().createULElement();

    @DataField("closeCollectionEditorButton")
    protected ButtonElement closeCollectionEditorButton = Document.get().createPushButtonElement();

    @DataField("addItemButtonContainer")
    protected DivElement addItemButtonContainer = Document.get().createDivElement();

    @DataField("createCollectionRadio")
    protected InputElement createCollectionRadio = Document.get().createRadioInputElement("collectionRadio");

    @DataField("defineCollectionRadio")
    protected InputElement defineCollectionRadio = Document.get().createRadioInputElement("collectionRadio");

    @DataField("cancelButton")
    protected ButtonElement cancelButton = Document.get().createPushButtonElement();

    @DataField("removeButton")
    protected ButtonElement removeButton = Document.get().createPushButtonElement();

    @DataField("saveButton")
    protected ButtonElement saveButton = Document.get().createPushButtonElement();

    @DataField("addItemButton")
    protected ButtonElement addItemButton = Document.get().createPushButtonElement();

    @DataField("editorTitle")
    protected HeadingElement editorTitle = Document.get().createHElement(4);

    @DataField("faAngleRight")
    protected SpanElement faAngleRight = Document.get().createSpanElement();

    @DataField("propertyTitle")
    protected SpanElement propertyTitle = Document.get().createSpanElement();

    @DataField("defineCollectionContainer")
    protected DivElement defineCollectionContainer = Document.get().createDivElement();

    @DataField("createCollectionContainer")
    protected DivElement createCollectionContainer = Document.get().createDivElement();

    @DataField("addItemButtonLabel")
    protected SpanElement addItemButtonLabel = Document.get().createSpanElement();

    @DataField("createLabel")
    protected LabelElement createLabel = Document.get().createLabelElement();

    @DataField("collectionCreationModeLabel")
    protected LabelElement collectionCreationModeLabel = Document.get().createLabelElement();

    @DataField("collectionCreationCreateLabel")
    protected SpanElement collectionCreationCreateLabel = Document.get().createSpanElement();

    @DataField("collectionCreationCreateSpan")
    protected SpanElement collectionCreationCreateSpan = Document.get().createSpanElement();

    @DataField("collectionCreationDefineLabel")
    protected SpanElement collectionCreationDefineLabel = Document.get().createSpanElement();

    @DataField("collectionCreationDefineSpan")
    protected SpanElement collectionCreationDefineSpan = Document.get().createSpanElement();

    @DataField("expressionElement")
    protected TextAreaElement expressionElement = Document.get().createTextAreaElement();

    /**
     * Flag to indicate if this <code>CollectionEditorViewImpl</code> will manage a <code>List</code> or a <code>Map</code>.
     */
    protected boolean listWidget;

    /**
     * Flag to indicate if this <code>CollectionEditorViewImpl</code> is opened in DMN or RULE scenario
     */
    protected ScenarioSimulationModel.Type scenarioType;

    /**
     * The <b>json</b> representation of the values of this editor
     */
    protected String value;

    public CollectionViewImpl() {
        setElement(collectionEditor);
        addKeyDownHandler(DomEvent::stopPropagation);
    }

    /**
     * Set the <b>name</b> of the property and the <code>Map</code> to be used to create the skeleton of the current <code>CollectionViewImpl</code> editor
     * showing a <b>List</b> of elements
     * @param key The key representing the property, i.e Classname#propertyname (e.g Author#books)
     * @param simplePropertiesMap
     * @param expandablePropertiesMap
     * @param type
     */
    @Override
    public void initListStructure(String key, Map<String, String> simplePropertiesMap, Map<String, Map<String, String>> expandablePropertiesMap, ScenarioSimulationModel.Type type) {
        listWidget = true;
        commonInit(type);
        createLabel.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelList());
        collectionCreationModeLabel.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.collectionListCreation());
        collectionCreationCreateLabel.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelList());
        collectionCreationCreateSpan.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelListDescription());
        collectionCreationDefineLabel.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.defineLabelList());
        collectionCreationDefineSpan.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.defineLabelListDescription());
        presenter.initListStructure(key, simplePropertiesMap, expandablePropertiesMap, this);
    }

    /**
     * Set the <b>name</b> of the property and the <code>Map</code> to be used to create the skeleton of the current <code>CollectionViewImpl</code> editor
     * showing a <b>Map</b> of elements
     * @param key The key representing the property, i.e Classname#propertyname (e.g Author#books)
     * @param keyPropertyMap
     * @param valuePropertyMap
     * @param type
     *
     */
    @Override
    public void initMapStructure(String key, Map<String, String> keyPropertyMap, Map<String, String> valuePropertyMap, ScenarioSimulationModel.Type type) {
        listWidget = false;
        commonInit(type);
        createLabel.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelMap());
        collectionCreationModeLabel.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.collectionMapCreation());
        collectionCreationCreateLabel.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelMap());
        collectionCreationCreateSpan.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelMapDescription());
        collectionCreationDefineLabel.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.defineLabelMap());
        collectionCreationDefineSpan.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.defineLabelMapDescription());
        presenter.initMapStructure(key, keyPropertyMap, valuePropertyMap, this);
    }

    protected void commonInit(ScenarioSimulationModel.Type type) {
        scenarioType = type ;
        saveButton.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.saveButton());
        cancelButton.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.cancelButton());
        removeButton.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.removeButton());
        addItemButtonLabel.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.collectionEditorAddNewItem());
        enableCreateCollectionContainer(true);
        if (RULE.equals(scenarioType)) {
            initAndRegisterHandlerForExpressionTextArea();
        }
    }

    /**
     * It inits and registers the native "input" , which is not managed by GWT
     */
    protected void initAndRegisterHandlerForExpressionTextArea() {
        ensureExpressionSyntax();
        DOM.sinkBitlessEvent(expressionElement, ConstantHolder.INPUT);
        DOM.setEventListener(expressionElement, event -> {
            if (ConstantHolder.INPUT.contains(event.getType()))  {
                ensureExpressionSyntax();}
        });
    }

    @Override
    public HandlerRegistration addCloseCompositeEventHandler(CloseCompositeEventHandler handler) {
        return addDomHandler(handler, CloseCompositeEvent.getType());
    }

    @Override
    public HandlerRegistration addSaveEditorEventHandler(SaveEditorEventHandler handler) {
        return addDomHandler(handler, SaveEditorEvent.getType());
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String jsonString) {
        presenter.setValue(jsonString);
    }

    @Override
    public boolean isExpressionWidget() {
        return defineCollectionRadio.isChecked();
    }

    @Override
    public boolean isListWidget() {
        return listWidget;
    }

    @Override
    public UListElement getElementsContainer() {
        return elementsContainer;
    }

    @Override
    public HeadingElement getEditorTitle() {
        return editorTitle;
    }

    @Override
    public SpanElement getPropertyTitle() {
        return propertyTitle;
    }

    @Override
    public String getExpression() {
        return expressionElement.getValue();
    }

    @Override
    public void setExpression(String expressionValue) {
        enableCreateCollectionContainer(false);
        expressionElement.setValue(expressionValue);
    }

    @EventHandler("createCollectionRadio")
    public void onCreateCollectionClick(ClickEvent clickEvent) {
        enableCreateCollectionContainer(true);
        clickEvent.stopPropagation();
    }

    @EventHandler("defineCollectionRadio")
    public void onDefineCollectionClick(ClickEvent clickEvent) {
        enableCreateCollectionContainer(false);
        clickEvent.stopPropagation();
    }

    protected void enableCreateCollectionContainer(boolean toEnable) {
        showCreateCollectionContainer(toEnable);
        showDefineCollectionContainer(!toEnable);
        showAddItemButtonContainer(toEnable);
        createCollectionRadio.setChecked(toEnable);
        defineCollectionRadio.setChecked(!toEnable);
        if (listWidget) {
            createLabel.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelList());
        } else {
            createLabel.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelMap());
        }
    }

    @EventHandler("collectionEditor")
    public void onCollectionEditorClick(ClickEvent clickEvent) {
        clickEvent.stopPropagation();
    }

    @EventHandler("collectionEditorModalDialog")
    public void onCollectionEditorModalDialogClick(ClickEvent clickEvent) {
        clickEvent.stopPropagation();
    }

    @EventHandler("editorTitle")
    public void onEditorTitleClick(ClickEvent clickEvent) {
        clickEvent.stopPropagation();
    }

    @EventHandler("elementsContainer")
    public void onElementsContainerClick(ClickEvent clickEvent) {
        clickEvent.stopPropagation();
    }

    @EventHandler("closeCollectionEditorButton")
    public void onCloseCollectionEditorButtonClick(ClickEvent clickEvent) {
        close();
        clickEvent.stopPropagation();
    }

    @EventHandler("cancelButton")
    public void onCancelButtonClick(ClickEvent clickEvent) {
        close();
        clickEvent.stopPropagation();
    }

    @EventHandler("removeButton")
    public void onRemoveButtonClick(ClickEvent clickEvent) {
        presenter.remove();
        clickEvent.stopPropagation();
    }

    @EventHandler("saveButton")
    public void onSaveButtonClick(ClickEvent clickEvent) {
        presenter.save();
        clickEvent.stopPropagation();
    }

    @EventHandler("addItemButton")
    public void onAddItemButton(ClickEvent clickEvent) {
        presenter.showEditingBox();
        clickEvent.stopPropagation();
    }

    @EventHandler("faAngleRight")
    public void onFaAngleRightClick(ClickEvent clickEvent) {
        presenter.onToggleRowExpansion(isShown());
        clickEvent.stopPropagation();
    }

    @EventHandler("propertyTitle")
    public void onPropertyTitleClick(ClickEvent clickEvent) {
        clickEvent.stopPropagation();
    }

    @EventHandler("addItemButtonContainer")
    public void onAddItemButtonContainerClick(ClickEvent clickEvent) {
        clickEvent.stopPropagation();
    }

    @Override
    public void toggleRowExpansion() {
        toggleRowExpansion(!isShown());
    }

    @Override
    public void updateRowExpansionStatus(boolean isShown) {
        toggleRowExpansion(!isShown);
    }

    @Override
    public void updateValue(String value) {
        this.value = value;
        fireEvent(new SaveEditorEvent());
    }

    @Override
    public void close() {
        fireEvent(new CloseCompositeEvent());
    }

    @Override
    public void setFixedHeight(double value, Style.Unit unit) {
        collectionEditorModalBody.getStyle().setHeight(value, unit);
    }

    @Override
    public void enableEditingMode(boolean isEditingMode) {
        createCollectionRadio.setDisabled(isEditingMode);
        defineCollectionRadio.setDisabled(isEditingMode);
        addItemButton.setDisabled(isEditingMode);
        cancelButton.setDisabled(isEditingMode);
        removeButton.setDisabled(isEditingMode);
        saveButton.setDisabled(isEditingMode);
    }

    protected boolean isShown() {
        return CollectionEditorUtils.isShown(faAngleRight);
    }

    protected void toggleRowExpansion(boolean toExpand) {
        CollectionEditorUtils.toggleRowExpansion(faAngleRight, toExpand);
    }

    protected void ensureExpressionSyntax() {
        if (RULE.equals(scenarioType)) {
            expressionElement.setValue(ExpressionUtils.ensureExpressionSyntax(expressionElement.getValue()));
        }
    }

    protected void showCreateCollectionContainer(boolean show) {
        if (show) {
            createCollectionContainer.getStyle().setDisplay(Style.Display.BLOCK);
        } else {
            createCollectionContainer.getStyle().setDisplay(Style.Display.NONE);
        }
    }

    protected void showDefineCollectionContainer(boolean show) {
        if (show) {
            defineCollectionContainer.getStyle().setDisplay(Style.Display.BLOCK);
        } else {
            defineCollectionContainer.getStyle().setDisplay(Style.Display.NONE);
        }
    }

    protected void showAddItemButtonContainer(boolean show) {
        if (show) {
            addItemButtonContainer.getStyle().setDisplay(Style.Display.BLOCK);
        } else {
            addItemButtonContainer.getStyle().setDisplay(Style.Display.NONE);
        }
    }
}
