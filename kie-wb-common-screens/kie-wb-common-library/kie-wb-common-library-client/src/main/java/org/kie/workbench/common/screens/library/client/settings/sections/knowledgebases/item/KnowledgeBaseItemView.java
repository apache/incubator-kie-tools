/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated("#root")
public class KnowledgeBaseItemView implements KnowledgeBaseItemPresenter.View {

    @Inject
    @DataField("is-default")
    private HTMLInputElement isDefault;

    @Inject
    @Named("span")
    @DataField("name")
    private HTMLElement name;

    @Inject
    @DataField("remove-button")
    private HTMLAnchorElement removeButton;

    @Inject
    @DataField("add-included-knowledge-base-button")
    private HTMLAnchorElement addIncludedKnowledgeBaseButton;

    @Inject
    @DataField("add-package-button")
    private HTMLAnchorElement addPackageButton;

    @Inject
    @DataField("included-knowledge-bases-list")
    private HTMLDivElement includedKnowledgeBasesList;

    @Inject
    @DataField("equals-behavior-select-container")
    private HTMLDivElement equalsBehaviorSelectContainer;

    @Inject
    @DataField("event-processing-model-select-container")
    private HTMLDivElement eventProcessingModelSelectContainer;

    @Inject
    @DataField("packages-list")
    private HTMLDivElement packagesList;

    @Inject
    @DataField("knowledge-sessions-link")
    private HTMLAnchorElement knowledgeSessionsLink;

    @Inject
    @Named("span")
    @DataField("knowledge-sessions-count")
    private HTMLElement knowledgeSessionsCount;


    private KnowledgeBaseItemPresenter presenter;

    @EventHandler("remove-button")
    private void onRemoveButtonClicked(final ClickEvent ignore) {
        presenter.remove();
    }

    @EventHandler("add-included-knowledge-base-button")
    private void onAddIncludedKnowledgeBaseButtonClicked(final ClickEvent ignore) {
        presenter.showNewIncludedKnowledgeBaseModal();
    }

    @EventHandler("add-package-button")
    private void onAddPackageButtonClicked(final ClickEvent ignore) {
        presenter.showAddPackageModal();
    }

    @EventHandler("knowledge-sessions-link")
    private void onKnowledgeSessionsLinkClicked(final ClickEvent ignore) {
        presenter.showKnowledgeSessionsModal();
    }

    @EventHandler("is-default")
    private void onIsDefaultChanged(final ChangeEvent ignore) {
        presenter.setDefault(isDefault.checked);
    }

    @Override
    public void init(final KnowledgeBaseItemPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setName(final String name) {
        this.name.textContent = name;
    }

    @Override
    public Element getPackagesListElement() {
        return packagesList;
    }

    @Override
    public Element getIncludedKnowledgeBasesListElement() {
        return includedKnowledgeBasesList;
    }

    @Override
    public void setDefault(final boolean isDefault) {
        this.isDefault.checked = isDefault;
    }

    @Override
    public Element getEqualsBehaviorSelectContainer() {
        return equalsBehaviorSelectContainer;
    }

    @Override
    public Element getEventProcessingModelSelectContainer() {
        return eventProcessingModelSelectContainer;
    }

    @Override
    public void setKnowledgeSessionsCount(final int size) {
        knowledgeSessionsCount.textContent = Integer.toString(size);
    }
}
