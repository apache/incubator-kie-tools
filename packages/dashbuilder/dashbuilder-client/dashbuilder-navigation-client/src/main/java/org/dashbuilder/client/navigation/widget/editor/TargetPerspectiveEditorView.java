/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.client.navigation.resources.i18n.NavigationConstants;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Templated
public class TargetPerspectiveEditorView implements TargetPerspectiveEditor.View, IsElement {

    @Inject
    @DataField
    Div perspectiveSelectorDiv;

    @Inject
    @DataField
    Div groupSelectorDiv;

    @Inject
    @DataField
    Span navGroupSelection;

    @Inject
    @DataField
    UnorderedList navGroupItems;

    @Inject
    @DataField
    Span perspectiveSelectorHelp;

    @Inject
    @DataField
    Span navGroupSelectorHelp;

    TargetPerspectiveEditor presenter;

    @Override
    public void init(TargetPerspectiveEditor presenter) {
        this.presenter = presenter;
        this.perspectiveSelectorHelp.setTitle(NavigationConstants.INSTANCE.navItemEditorPerspectiveHelp());
        this.navGroupSelectorHelp.setTitle(NavigationConstants.INSTANCE.navItemEditorGroupHelp());
    }

    @Override
    public void setPerspectiveSelector(IsWidget perspectiveDropDown) {
        perspectiveDropDown.asWidget().getElement().getStyle().setWidth(150, Style.Unit.PX);
        DOMUtil.removeAllChildren(perspectiveSelectorDiv);
        DOMUtil.appendWidgetToElement(perspectiveSelectorDiv, perspectiveDropDown);
    }

    @Override
    public void clearNavGroupItems() {
        DOMUtil.removeAllChildren(navGroupItems);
        navGroupSelection.setTextContent(NavigationConstants.INSTANCE.navGroupSelectorHint());
    }

    @Override
    public void setNavGroupEnabled(boolean enabled) {
        if (enabled) {
            groupSelectorDiv.getStyle().removeProperty("display");
            navGroupSelectorHelp.getStyle().removeProperty("display");
        } else {
            groupSelectorDiv.getStyle().setProperty("display", "none");
            navGroupSelectorHelp.getStyle().setProperty("display", "none");
        }
    }

    @Override
    public void addNavGroupItem(String name, Command onSelect) {
        addItem(navGroupItems, name, false, onSelect);
    }

    @Override
    public void setNavGroupSelection(String name, Command onReset) {
        navGroupSelection.setTextContent(name);
        navGroupSelection.setTitle(name);
        addItem(navGroupItems, name, true, onReset);
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
}

