/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.navigation.widget.editor;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Templated
public class NavTreeEditorView extends NavItemEditorView<NavTreeEditor> implements NavTreeEditor.View {

    @Inject
    @DataField
    Div mainDiv;

    @Inject
    @DataField
    Div childrenDiv;

    @Inject
    @DataField
    Div alertDiv;

    @Inject
    @DataField
    Span alertMessage;

    @Inject
    @DataField
    Button saveButton;

    @Inject
    @DataField
    Button cancelButton;

    NavTreeEditor presenter;

    @Override
    public void init(NavTreeEditor presenter) {
        this.presenter = presenter;
        alertMessage.setTextContent(i18n.saveChanges());
        saveButton.setTextContent(i18n.save());
        cancelButton.setTextContent(i18n.cancel());
    }

    @Override
    public void setExpandEnabled(boolean enabled) {
    }

    @Override
    public void setExpanded(boolean expanded) {
        childrenDiv.setHidden(!expanded);
    }

    @Override
    public void clearChildren() {
        DOMUtil.removeAllChildren(childrenDiv);
        setChangedFlag(false);
    }

    @Override
    public void addChild(IsElement editor) {
        childrenDiv.appendChild(editor.getElement());
    }

    @Override
    public void setChangedFlag(boolean on) {
        if (on) {
            alertDiv.getStyle().removeProperty("display");
        } else {
            alertDiv.getStyle().setProperty("display", "none");
        }
    }

    @EventHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        presenter.onSaveClicked();
    }

    @EventHandler("cancelButton")
    public void onCancelClicked(ClickEvent event) {
        presenter.onCancelClicked();
    }

    // N/A: The rest of the NavItemEditorView methods below does not apply since the NavTreeEditor
    // item is non editable and it only allows for the creation of subgroup items.

    @Override
    public void setItemName(String name) {
    }

    @Override
    public String getItemName() {
        return null;
    }

    @Override
    public void setItemNameError(boolean hasError) {
    }

    @Override
    public void setItemDescription(String description) {
    }

    @Override
    public void setItemType(NavItemEditor.ItemType type) {
    }

    @Override
    public void clearCommands() {
    }

    @Override
    public void addCommand(String name, Command command) {
    }

    @Override
    public void addCommandDivider() {
    }

    @Override
    public void setCommandsEnabled(boolean enabled) {
    }

    @Override
    public void setItemEditable(boolean editable) {
    }

    @Override
    public void setItemDeletable(boolean deletable) {
    }

    @Override
    public void startItemEdition() {
    }

    @Override
    public void finishItemEdition() {
    }

    @Override
    public void setContextWidget(IsElement widget) {
    }
}

