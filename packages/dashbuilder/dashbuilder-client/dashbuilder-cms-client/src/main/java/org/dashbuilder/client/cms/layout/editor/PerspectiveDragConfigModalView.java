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
package org.dashbuilder.client.cms.layout.editor;

import javax.inject.Inject;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerConstants;
import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Label;
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

@Templated
public class PerspectiveDragConfigModalView implements PerspectiveDragConfigModal.View {

    PerspectiveDragConfigModal presenter;
    BaseModal modal;
    ButtonPressed buttonPressed = ButtonPressed.CLOSE;

    @Inject
    @DataField
    Div body;

    @Inject
    @DataField
    Div footer;

    @DataField
    @Inject
    Span selectorHelp;

    @DataField
    @Inject
    Span currentSelection;

    @DataField
    @Inject
    UnorderedList selectorItems;

    @DataField
    @Inject
    Label navItemLabel;

    @Override
    public void init(PerspectiveDragConfigModal presenter) {
        this.presenter = presenter;
        currentSelection.setTextContent(ContentManagerConstants.INSTANCE.perspectiveDragSelectorHint());
        navItemLabel.setTextContent(ContentManagerConstants.INSTANCE.perspectiveDragSelectorLabel());

        modal = new CommonModalBuilder()
                .addHeader(ContentManagerConstants.INSTANCE.perspectiveDragComponentHeader())
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
    public void clearItems() {
        DOMUtil.removeAllChildren(selectorItems);
    }

    @Override
    public void setHelpText(String text) {
        selectorHelp.setTitle(text);
    }

    @Override
    public void setCurrentSelection(String name) {
        currentSelection.setTextContent(name);
    }

    @Override
    public void addItem(String name, Command onSelect) {
        AnchorElement anchor = Document.get().createAnchorElement();
        anchor.setInnerText(name);

        LIElement li = Document.get().createLIElement();
        li.getStyle().setCursor(Style.Cursor.POINTER);
        li.appendChild(anchor);
        selectorItems.appendChild((Node) li);

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
