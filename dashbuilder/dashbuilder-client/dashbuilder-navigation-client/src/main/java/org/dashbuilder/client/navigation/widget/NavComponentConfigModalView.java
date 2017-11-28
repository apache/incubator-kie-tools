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
package org.dashbuilder.client.navigation.widget;

import javax.inject.Inject;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.navigation.resources.i18n.NavigationConstants;
import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.shared.TemplateWidgetMapper;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.editor.commons.client.file.popups.CommonModalBuilder;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.ButtonPressed;
import org.uberfire.mvp.Command;

@Templated
public class NavComponentConfigModalView implements NavComponentConfigModal.View {

    NavComponentConfigModal presenter;
    BaseModal modal;
    ButtonPressed buttonPressed = ButtonPressed.CLOSE;

    @Inject
    @DataField
    Div body;

    @Inject
    @DataField
    Div footer;

    @Inject
    @DataField
    Div navGroupDiv;

    @Inject
    @DataField
    Span navGroupLabel;

    @DataField
    @Inject
    Span navGroupHelp;

    @DataField
    @Inject
    Span navGroupSelection;

    @DataField
    @Inject
    UnorderedList navGroupItems;

    @Inject
    @DataField
    Div defaultItemDiv;

    @Inject
    @DataField
    Span defaultItemLabel;

    @DataField
    @Inject
    Span defaultItemHelp;

    @DataField
    @Inject
    Span defaultItemSelection;

    @DataField
    @Inject
    UnorderedList defaultItems;

    @Inject
    @DataField
    Div targetDivDiv;

    @Inject
    @DataField
    Span targetDivLabel;

    @DataField
    @Inject
    Span targetDivHelp;

    @DataField
    @Inject
    Span targetDivSelection;

    @DataField
    @Inject
    UnorderedList targetDivItems;

    @Override
    public void init(NavComponentConfigModal presenter) {
        this.presenter = presenter;
        navGroupLabel.setTextContent(NavigationConstants.INSTANCE.navGroupLabel());
        navGroupHelp.setTitle(NavigationConstants.INSTANCE.navGroupHelp());
        navGroupSelection.setTextContent(NavigationConstants.INSTANCE.navGroupSelectorHint());
        defaultItemLabel.setTextContent(NavigationConstants.INSTANCE.defaultItemLabel());
        defaultItemHelp.setTitle(NavigationConstants.INSTANCE.defaultItemHelp());
        defaultItemSelection.setTextContent(NavigationConstants.INSTANCE.defaultItemSelectorHint());
        targetDivLabel.setTextContent(NavigationConstants.INSTANCE.targetDivLabel());
        targetDivHelp.setTitle(NavigationConstants.INSTANCE.targetDivHelp());
        targetDivSelection.setTextContent(NavigationConstants.INSTANCE.targetDivSelectorHint());

        modal = new CommonModalBuilder()
                .addHeader(NavigationConstants.INSTANCE.navConfigHeader())
                .addBody( body )
                .addFooter( footer )
                .build();

        modal.addHiddenHandler(hiddenEvent -> {
            if (ButtonPressed.CLOSE.equals(buttonPressed)) {
                presenter.onCancel();
            }
        });

        modal.setWidth( "960px" );
    }

    public Modal getModal() {
        return modal;
    }

    @Override
    public Widget asWidget() {
        return TemplateWidgetMapper.get(modal);
    }

    @Override
    public void show() {
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public void setNavGroupEnabled(boolean enabled) {
        navGroupDiv.setClassName("form-group" + (enabled ? "" : " uf-navconfig-disabled"));
    }

    @Override
    public void clearNavGroupItems() {
        DOMUtil.removeAllChildren(navGroupItems);
        navGroupSelection.setTextContent(NavigationConstants.INSTANCE.navGroupSelectorHint());
    }

    @Override
    public void setNavGroupHelpText(String text) {
        navGroupHelp.setTitle(text);
    }

    @Override
    public void setNavGroupSelection(String name, Command onReset) {
        navGroupSelection.setTextContent(name);
        addItem(navGroupItems, name, true, onReset);
    }

    @Override
    public void addNavGroupItem(String name, Command onSelect) {
        addItem(navGroupItems, name, false, onSelect);
    }

    @Override
    public void setDefaultNavItemVisible(boolean enabled) {
        defaultItemDiv.setHidden(!enabled);
    }

    @Override
    public void setDefaultNavItemEnabled(boolean enabled) {
        defaultItemDiv.setClassName("form-group" + (enabled ? "" : " uf-navconfig-disabled"));
    }

    @Override
    public void clearDefaultItems() {
        DOMUtil.removeAllChildren(defaultItems);
        defaultItemSelection.setTextContent(NavigationConstants.INSTANCE.defaultItemSelectorHint());
    }

    @Override
    public void defaultItemsNotFound() {
        clearDefaultItems();
        addDefaultItem(NavigationConstants.INSTANCE.defaultItemsNotFound(), () -> {});
    }

    @Override
    public void setDefaultItemSelection(String name, Command onReset) {
        defaultItemSelection.setTextContent(name);
        addItem(defaultItems, name, true, onReset);
    }

    @Override
    public void addDefaultItem(String name, Command onSelect) {
        addItem(defaultItems, name, false, onSelect);
    }

    @Override
    public void setTargetDivVisible(boolean enabled) {
        targetDivDiv.setHidden(!enabled);
    }

    @Override
    public void clearTargetDivItems() {
        DOMUtil.removeAllChildren(targetDivItems);
        targetDivSelection.setTextContent(NavigationConstants.INSTANCE.targetDivSelectorHint());
    }

    @Override
    public void targetDivsNotFound() {
        clearTargetDivItems();
        addTargetDivItem(NavigationConstants.INSTANCE.targetDivsNotFound(), () -> {});
    }

    @Override
    public void addTargetDivItem(String name, Command onSelect) {
        addItem(targetDivItems, name, false, onSelect);
    }

    @Override
    public void setTargetDivSelection(String name, Command onReset) {
        targetDivSelection.setTextContent(name);
        addItem(targetDivItems, name, true, onReset);
    }

    private void addItem(UnorderedList unorderedList, String name, boolean selected, Command onSelect) {
        AnchorElement anchor = Document.get().createAnchorElement();
        anchor.setInnerText(name);

        LIElement li = Document.get().createLIElement();
        li.getStyle().setCursor(Style.Cursor.POINTER);
        li.appendChild(anchor);
        li.setClassName(selected ? "selected" : "");
        unorderedList.appendChild((Node) li);

        Event.sinkEvents(anchor, Event.ONCLICK);
        Event.setEventListener(anchor, event -> {
            if(Event.ONCLICK == event.getTypeInt()) {
                onSelect.execute();
            }
        });
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("okButton")
    public void okClick(final Event event) {
        buttonPressed = ButtonPressed.OK;
        presenter.onOk();
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("cancelButton")
    public void cancelClick(final Event event) {
        buttonPressed = ButtonPressed.CANCEL;
        presenter.onCancel();
    }
}
