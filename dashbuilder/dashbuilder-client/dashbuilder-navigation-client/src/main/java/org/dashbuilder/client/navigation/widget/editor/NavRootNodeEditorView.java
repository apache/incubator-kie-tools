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

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.Event;
import org.dashbuilder.client.navigation.resources.i18n.NavigationConstants;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Templated
public class NavRootNodeEditorView extends NavItemEditorView<NavRootNodeEditor> implements NavRootNodeEditor.View {

    @Inject
    @DataField
    Span itemIcon;

    @Inject
    @DataField
    Span itemMenuIcon;

    @Inject
    @DataField
    Span itemEditIcon;

    @Inject
    @DataField
    Span itemDeleteIcon;

    @Inject
    @DataField
    Span itemConfirmIcon;

    @Inject
    @DataField
    Span itemCancelIcon;

    @Inject
    @DataField
    Span itemName;

    @Inject
    @DataField
    Input itemNameInput;

    @Inject
    @DataField
    Div extraDiv;

    @Inject
    @DataField
    Div itemViewDiv;

    @Inject
    @DataField
    Div itemEditDiv;

    @Inject
    @DataField
    UnorderedList commandMenu;

    @Inject
    @DataField
    Div childrenDiv;

    NavRootNodeEditor presenter;

    @Override
    public void init(NavRootNodeEditor presenter) {
        this.presenter = presenter;
        itemViewDiv.setHidden(false);
        itemEditDiv.setHidden(true);
        itemMenuIcon.setTitle(i18n.itemMenuTitle());
        itemEditIcon.setTitle(i18n.editItem());
        itemDeleteIcon.setTitle(i18n.deleteItem());
    }

    @Override
    public void clearChildren() {
        DOMUtil.removeAllChildren(childrenDiv);
    }

    @Override
    public void addChild(org.jboss.errai.common.client.api.IsElement editor) {
        childrenDiv.appendChild(editor.getElement());
    }

    @Override
    public void setExpandEnabled(boolean enabled) {
        if (enabled) {
            itemIcon.getStyle().removeProperty("opacity");
            itemIcon.getStyle().removeProperty("pointer-events");
            itemIcon.getStyle().setProperty("cursor", "pointer");
            itemName.getStyle().setProperty("cursor", "pointer");
        } else {
            itemIcon.getStyle().setProperty("opacity", ".5");
            itemIcon.getStyle().setProperty("pointer-events", "none");
            itemIcon.getStyle().removeProperty("cursor");
            itemName.getStyle().removeProperty("cursor");
        }
    }

    @Override
    public void setExpanded(boolean expanded) {
        itemIcon.setClassName(expanded ? "fa fa-angle-down" : "fa fa-angle-right");
        childrenDiv.setHidden(!expanded);
    }

    @Override
    public void setItemName(String name) {
        itemName.setTextContent(name);
        itemNameInput.setValue(name);
    }

    @Override
    public String getItemName() {
        return itemNameInput.getValue();
    }

    @Override
    public void setItemNameError(boolean hasError) {
        String classes = "uf-navitem-name-input" + (hasError ? " uf-navitem-name-error" : " form-control");
        itemNameInput.setClassName(classes);
    }

    @Override
    public void setItemDescription(String description) {
        itemName.setTitle(description);
    }

    @Override
    public void setItemType(NavItemEditor.ItemType type) {
        itemIcon.getStyle().removeProperty("cursor");

        if (type == NavItemEditor.ItemType.GROUP) {
            itemIcon.setClassName("fa fa-angle-right");
            itemIcon.setTitle("");
        }
        else if (type == NavItemEditor.ItemType.DIVIDER) {
            itemIcon.setClassName("fa fa-minus");
            itemIcon.setTitle("");
        }
        else if (type == NavItemEditor.ItemType.PERSPECTIVE) {
            itemIcon.setClassName("pficon-screen");
            itemIcon.setTitle(NavigationConstants.INSTANCE.perspectiveIconTitle());
        }
        else if (type == NavItemEditor.ItemType.RUNTIME_PERSPECTIVE) {
            itemIcon.setClassName("pficon-virtual-machine");
            itemIcon.setTitle(NavigationConstants.INSTANCE.pageIconTitle());
        }
    }

    @Override
    public void addCommand(String name, Command command) {
        AnchorElement anchor = Document.get().createAnchorElement();
        anchor.setInnerText(name);

        LIElement li = Document.get().createLIElement();
        li.getStyle().setCursor(Style.Cursor.POINTER);
        li.appendChild(anchor);
        commandMenu.appendChild((Node) li);

        Event.sinkEvents(anchor, Event.ONCLICK);
        Event.setEventListener(anchor, event -> {
            if(Event.ONCLICK == event.getTypeInt()) {
                command.execute();
            }
        });
    }

    @Override
    public void addCommandDivider() {
        LIElement li = Document.get().createLIElement();
        li.setClassName("divider");
        commandMenu.appendChild((Node) li);
    }

    @Override
    public void setCommandsEnabled(boolean enabled) {
        itemMenuIcon.setHidden(!enabled);
        commandMenu.setHidden(!enabled);
    }

    @Override
    public void clearCommands() {
        DOMUtil.removeAllChildren(commandMenu);
    }

    @Override
    public void startItemEdition() {
        itemViewDiv.setHidden(true);
        itemEditDiv.setHidden(false);
        itemNameInput.focus();
    }

    @Override
    public void finishItemEdition() {
        itemViewDiv.setHidden(false);
        itemEditDiv.setHidden(true);
    }

    @Override
    public void setContextWidget(IsElement widget) {
        extraDiv.getStyle().setProperty("display", "block");
        DOMUtil.removeAllChildren(extraDiv);
        extraDiv.appendChild(widget.getElement());
    }

    @Override
    public void setItemEditable(boolean editable) {
        if (editable) {
            itemEditIcon.getStyle().removeProperty("display");
        } else {
            itemEditIcon.getStyle().setProperty("display", "none");
        }
    }

    @Override
    public void setItemDeletable(boolean deletable) {
        if (deletable) {
            itemDeleteIcon.getStyle().removeProperty("display");
        } else {
            itemDeleteIcon.getStyle().setProperty("display", "none");
        }
    }

    @EventHandler("itemIcon")
    public void onItemIconClick(ClickEvent event) {
        presenter.expandOrCollapse();
    }

    @EventHandler("itemName")
    public void onItemNameClick(ClickEvent event) {
        presenter.expandOrCollapse();
    }

    @EventHandler("itemEditIcon")
    public void onItemEditClick(ClickEvent event) {
        presenter.startEdition();
    }

    @EventHandler("itemDeleteIcon")
    public void onItemDeleteClick(ClickEvent event) {
        presenter.deleteItemCommand();
    }

    @EventHandler("itemConfirmIcon")
    public void onItemEditOkClick(ClickEvent event) {
        presenter.onChangesOk();
    }

    @EventHandler("itemCancelIcon")
    public void onItemEditCancelClick(ClickEvent event) {
        presenter.cancelEdition();
    }

    @EventHandler("itemNameInput")
    public void onItemNameInputOver(MouseOverEvent event) {
        itemNameInput.focus();
    }

    @EventHandler("itemNameInput")
    public void onItemNameChanged(KeyPressEvent keyEvent) {
        if (keyEvent.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
            presenter.onChangesOk();
        } else {
            presenter.onItemNameChanged();
        }
    }
}


