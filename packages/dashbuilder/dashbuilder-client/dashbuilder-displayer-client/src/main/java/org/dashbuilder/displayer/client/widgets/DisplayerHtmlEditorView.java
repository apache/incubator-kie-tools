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
package org.dashbuilder.displayer.client.widgets;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.displayer.client.resources.i18n.DisplayerHtmlConstants;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.common.client.dom.NodeList;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class DisplayerHtmlEditorView extends Composite
        implements DisplayerHtmlEditor.View {

    @Inject
    @DataField
    FlowPanel contentPanel;

    @Inject
    @DataField
    UnorderedList tabList;

    @Inject
    @DataField
    ListItem previewItem;

    @Inject
    @DataField
    Anchor previewAnchor;

    LIElement selectedItem;
    DisplayerHtmlEditor presenter;

    @Override
    public void init(DisplayerHtmlEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showDisplayer(IsWidget displayer) {
        contentPanel.clear();
        contentPanel.add(displayer);
        previewItem.setClassName("active");
        previewAnchor.setTextContent(DisplayerHtmlConstants.INSTANCE.displayer_html_preview_link());
    }

    @Override
    public void clearSourceCodeItems() {
        removeAllChildren(tabList);
        tabList.appendChild(previewItem);
    }

    @Override
    public void editSourceCodeItem(String name, IsWidget editor) {
        contentPanel.clear();
        contentPanel.add(editor);
    }

    @Override
    public void addSourceCodeItem(String name) {
        AnchorElement anchor = Document.get().createAnchorElement();
        String displayName = DisplayerHtmlConstants.INSTANCE.getString("displayer_source_code_" + name);
        anchor.setInnerText(displayName);

        LIElement li = Document.get().createLIElement();
        li.getStyle().setCursor(Style.Cursor.POINTER);
        li.appendChild(anchor);
        tabList.appendChild((Node) li);

        Event.sinkEvents(anchor, Event.ONCLICK);
        Event.setEventListener(anchor, event -> {
            if(Event.ONCLICK == event.getTypeInt()) {
                presenter.onSourceCodeItemSelected(name);
                if (selectedItem != null) {
                    selectedItem.setClassName("");
                    selectedItem.getStyle().setCursor(Style.Cursor.POINTER);
                }
                selectedItem = li;
                selectedItem.setClassName("active");
                selectedItem.getStyle().setCursor(Style.Cursor.DEFAULT);
                previewItem.setClassName("");
            }
        });
    }

    private void removeAllChildren(org.jboss.errai.common.client.dom.Element element) {
        NodeList nodeList = element.getChildNodes();
        int lenght = nodeList.getLength();
        for (int i=0; i<lenght; i++) {
            element.removeChild(nodeList.item(0));
        }
    }

    @EventHandler("previewAnchor")
    private void onPreviewClicked(ClickEvent event) {
        if (!presenter.showDisplayer()) {
            event.stopPropagation();
        } else {
            previewItem.setClassName("active");
            if (selectedItem != null) {
                selectedItem.setClassName("");
                selectedItem.getStyle().setCursor(Style.Cursor.POINTER);
            }
        }
    }
}
