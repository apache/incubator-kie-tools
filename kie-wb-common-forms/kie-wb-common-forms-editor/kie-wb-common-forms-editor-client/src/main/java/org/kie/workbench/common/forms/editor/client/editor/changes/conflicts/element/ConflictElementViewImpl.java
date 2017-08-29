/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.client.editor.changes.conflicts.element;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class ConflictElementViewImpl implements IsElement,
                                                ConflictElementView {

    public static final String HIDDEN_CLASS_NAME = "conflict-element-hidden";

    private Presenter presenter;

    @Inject
    @DataField
    @Named("strong")
    private HTMLElement fieldContainer;

    @Inject
    @DataField
    private Span helpMessageContainer;

    @Inject
    @DataField
    private Anchor showMore;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    public void showConflict(String field,
                             String message) {
        fieldContainer.setTextContent(field);
        helpMessageContainer.setTextContent(message);
    }

    @Override
    public void setMessage(String message) {
        helpMessageContainer.setTextContent(message);
    }

    @Override
    public void setShowMoreText(String text) {
        showMore.setTextContent(text);
    }

    @EventHandler("showMore")
    @SinkNative(Event.ONCLICK)
    public void onClick(ClickEvent clickEvent) {
        showMore.blur();
        presenter.onShowMoreClick();
    }
}
