/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class KnowledgeSessionsModalView implements KnowledgeSessionsModal.View {

    @Inject
    @DataField("header")
    private HTMLDivElement header;

    @Inject
    @DataField("body")
    private HTMLDivElement body;

    @Inject
    @DataField("footer")
    private HTMLDivElement footer;

    @Inject
    @DataField("knowledge-sessions-table")
    private HTMLDivElement knowledgeSessionsTable;

    @Inject
    @DataField("add-knowledge-session-button")
    private HTMLButtonElement addKnowledgeSessionButton;

    @Inject
    @DataField("done-button")
    private HTMLButtonElement doneButton;

    private KnowledgeSessionsModal presenter;

    @Override
    public void init(final KnowledgeSessionsModal presenter) {
        this.presenter = presenter;
    }

    @EventHandler("done-button")
    public void onDoneButtonClicked(final ClickEvent ignore) {
        presenter.done();
    }

    @EventHandler("add-knowledge-session-button")
    public void onAddKnowledgeSessionButtonClicked(final ClickEvent ignore) {
        presenter.add();
    }

    @Override
    public String getHeader() {
        return header.textContent;
    }

    @Override
    public HTMLElement getBody() {
        return body;
    }

    @Override
    public HTMLElement getFooter() {
        return footer;
    }

    @Override
    public HTMLElement getKnowledgeSessionsTable() {
        return knowledgeSessionsTable;
    }
}
