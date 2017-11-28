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
import org.dashbuilder.client.navigation.resources.i18n.NavigationConstants;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Anchor;
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
    Span expandIcon;

    @Inject
    @DataField
    Span titleSpan;

    @Inject
    @DataField
    Div createDiv;

    @Inject
    @DataField
    Span createSpan;

    @Inject
    @DataField
    Anchor newTreeAnchor;

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
        titleSpan.setTextContent(i18n.navigationTitle());
        alertMessage.setTextContent(i18n.saveChanges());
        saveButton.setTextContent(i18n.save());
        cancelButton.setTextContent(i18n.cancel());
        createSpan.setTextContent(i18n.newMenu());
        newTreeAnchor.setTextContent(i18n.treeItem());
    }

    @Override
    public String getTreeLiteralI18n() {
        return NavigationConstants.INSTANCE.treeItem();
    }

    @Override
    public void setExpandEnabled(boolean enabled) {
        if (enabled) {
            expandIcon.getStyle().removeProperty("opacity");
            expandIcon.getStyle().removeProperty("pointer-events");
            expandIcon.getStyle().setProperty("cursor", "pointer");
            titleSpan.getStyle().setProperty("cursor", "pointer");
        } else {
            expandIcon.getStyle().setProperty("opacity", ".5");
            expandIcon.getStyle().setProperty("pointer-events", "none");
            expandIcon.getStyle().removeProperty("cursor");
            titleSpan.getStyle().removeProperty("cursor");
        }
    }

    @Override
    public void setExpanded(boolean expanded) {
        childrenDiv.setHidden(!expanded);
        expandIcon.setClassName("uf-cms-expand-icon " + (expanded ? "fa fa-angle-down" : "fa fa-angle-right"));
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
    public void setMaximized(boolean maximized) {
        childrenDiv.setClassName(maximized ? "uf-cms-nav-children-maximized" : "uf-cms-nav-children-panel");
    }


    @Override
    public void setChangedFlag(boolean on) {
        if (on) {
            alertDiv.getStyle().removeProperty("display");
        } else {
            alertDiv.getStyle().setProperty("display", "none");
        }
    }

    @EventHandler("titleSpan")
    public void onTitleClick(final ClickEvent event) {
        presenter.expandOrCollapse();
    }

    @EventHandler("expandIcon")
    public void onExpandClick(final ClickEvent event) {
        presenter.expandOrCollapse();
    }

    @EventHandler("newTreeAnchor")
    public void onNewTreeClicked(ClickEvent event) {
        presenter.onNewTreeClicked();
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

