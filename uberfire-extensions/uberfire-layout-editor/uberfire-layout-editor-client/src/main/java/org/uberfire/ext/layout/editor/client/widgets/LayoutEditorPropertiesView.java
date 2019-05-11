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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.IsWidget;

import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.layout.editor.client.resources.i18n.PropertiesConstants;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Templated
@Dependent
public class LayoutEditorPropertiesView implements LayoutEditorPropertiesPresenter.View, IsElement {

    private LayoutEditorPropertiesPresenter presenter;

    @Inject
    @DataField
    Div selectorDiv;

    @Inject
    @DataField
    Div elementDiv;

    @Inject
    @DataField
    Span selectorLabel;

    @Inject
    @DataField
    Span propertiesLabel;

    @Inject
    @DataField
    Anchor clearAllAnchor;
    
    @Inject
    @DataField
    HTMLOptionElement partOption;
    
    @Inject
    @DataField
    HTMLSelectElement partsSelect;
    
    @Inject
    @DataField
    Div partsFieldContainer;

    private IsWidget selectorView;
    private IsWidget elementView;

    @Override
    public void init(LayoutEditorPropertiesPresenter presenter) {
        this.presenter = presenter;
        selectorLabel.setTextContent(PropertiesConstants.INSTANCE.elementLabel());
        propertiesLabel.setTextContent(PropertiesConstants.INSTANCE.propertiesLabel());
        clearAllAnchor.setTextContent(PropertiesConstants.INSTANCE.clearAll());
        partsSelect.onchange = this::onPartsSelectChange;
    }

    @Override
    public void dispose() {
        disposeSelectorWidget();
        disposeElementWidget();
    }

    private void disposeSelectorWidget() {
        DOMUtil.removeAllChildren(selectorDiv);
        if (selectorView != null) {
            DOMUtil.removeFromParent(selectorView);
            selectorView = null;
        }
    }

    private void disposeElementWidget() {
        DOMUtil.removeAllChildren(elementDiv);
        if (elementView != null) {
            DOMUtil.removeFromParent(elementView);
            elementView = null;
        }
    }
    
    @Override
    public void showSelector(IsWidget selector) {
        disposeSelectorWidget();
        this.selectorView = selector;
        DOMUtil.appendWidgetToElement(selectorDiv, selectorView);

    }

    @Override
    public void showElement(IsWidget elementView) {
        disposeElementWidget();
        this.elementView = elementView;
        DOMUtil.appendWidgetToElement(elementDiv, elementView);
    }

    @Override
    public String getDisplayPosition(String parentPosition, String elementName, String elementIndex) {
        return PropertiesConstants.INSTANCE.layoutElementPosition(parentPosition, elementName, elementIndex);
    }

    @Override
    public String getDisplayName(String elementName, String elementIndex) {
        return PropertiesConstants.INSTANCE.layoutElementName(elementName, elementIndex);
    }

    @Override
    public String getLayoutElementTypePage() {
        return PropertiesConstants.INSTANCE.layoutElementTypePage();
    }

    @Override
    public String getLayoutElementTypeRow() {
        return PropertiesConstants.INSTANCE.layoutElementTypeRow();
    }

    @Override
    public String getLayoutElementTypeColumn() {
        return PropertiesConstants.INSTANCE.layoutElementTypeColumn();
    }

    @Override
    public String getLayoutElementTypeComponent() {
        return PropertiesConstants.INSTANCE.layoutElementTypeComponent();
    }

    @Override
    public void setClearPropertiesEnabled(boolean enabled) {
        if (enabled) {
            clearAllAnchor.getStyle().removeProperty("display");
        } else {
            clearAllAnchor.getStyle().setProperty("display", "none");
        }
    }

    @EventHandler("clearAllAnchor")
    private void onClearAll(ClickEvent event) {
        presenter.clearElementProperties();
    }

    @Override
    public void noParts() {
        partsFieldContainer.setHidden(true);
        partsSelect.innerHTML = "";
        
    }

    @Override
    public void showParts(List<String> parts) {
        partsFieldContainer.setHidden(false);
        partsSelect.innerHTML = "";
        parts.stream().map(this::itemToOption).forEach(partsSelect::appendChild);
        
    }
    
    private Object onPartsSelectChange(Object event) {
        presenter.onPartSelected(partsSelect.value); 
        return null; 
    }
    
    private HTMLOptionElement itemToOption(String item) {
        HTMLOptionElement option = (HTMLOptionElement) partOption.cloneNode(false);
        option.text = item;
        option.value = item;
        return option;
    }
}