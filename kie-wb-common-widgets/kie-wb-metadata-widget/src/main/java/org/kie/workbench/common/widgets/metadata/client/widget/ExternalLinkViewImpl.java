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
package org.kie.workbench.common.widgets.metadata.client.widget;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.widgets.metadata.client.resources.i18n.MetadataConstants;

@Dependent
@Templated
public class ExternalLinkViewImpl
        extends Composite
        implements ExternalLinkView {

    @Inject
    @DataField("link")
    Anchor link;

    @Inject
    @DataField("linkTextBox")
    TextInput linkTextBox;

    @Inject
    @DataField("editButton")
    Button editButton;

    private ExternalLinkPresenter presenter;

    public ExternalLinkViewImpl() {
        super();
    }

    @Override
    public void init(final ExternalLinkPresenter presenter) {
        this.presenter = presenter;
        link.setTitle(MetadataConstants.INSTANCE.ExternalLinkTip());
    }

    @Override
    public void setEditModeVisibility(boolean editModeVisible) {
        linkTextBox.getStyle()
                .setProperty("visibility",
                             getVisibility(editModeVisible));
    }

    @Override
    public void setLinkModeVisibility(final boolean linkModeVisible) {
        editButton.getStyle()
                .setProperty("visibility",
                             getVisibility(linkModeVisible));
        link.getStyle()
                .setProperty("visibility",
                             getVisibility(linkModeVisible));

        if (linkModeVisible) {
            editButton.getStyle()
                    .removeProperty("width");
            editButton.getStyle()
                    .removeProperty("border");
            editButton.getStyle()
                    .removeProperty("padding");
        } else {

            link.setInnerHTML("");
            link.getStyle()
                    .setProperty("width",
                                 "0px");

            editButton.getStyle()
                    .setProperty("width",
                                 "0px");
            editButton.getStyle()
                    .setProperty("border",
                                 "0px");
            editButton.getStyle()
                    .setProperty("padding",
                                 "0px");
        }
    }

    private String getVisibility(boolean visible) {
        if (visible) {
            return "visible";
        } else {
            return "hidden";
        }
    }

    @Override
    public void setLink(final String value) {
        link.setHref(value);
        link.setInnerHTML(value);
    }

    @Override
    public void setText(final String value) {
        linkTextBox.setValue(value);
    }

    @Override
    public String getTextBoxText() {
        return linkTextBox.getValue();
    }

    @EventHandler("linkTextBox")
    @SinkNative(Event.ONCHANGE | Event.ONKEYUP | Event.ONBLUR)
    public void onLinkTextBoxEvents(Event event) {
        if (event.getTypeInt() == Event.ONBLUR) {
            presenter.onTextChangeDone();
        } else {
            presenter.onTextChange(linkTextBox.getValue());
        }
    }

    @EventHandler("editButton")
    @SinkNative(Event.ONCLICK)
    public void onEdit(Event event) {
        presenter.onEdit();
    }
}
