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
import elemental2.dom.Event;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.settings.util.select.KieEnumSelectElement;
import org.kie.workbench.common.services.shared.kmodule.AssertBehaviorOption;
import org.kie.workbench.common.services.shared.kmodule.EventProcessingOption;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;

@Templated("#root")
public class KnowledgeBaseItemView implements KnowledgeBaseItemPresenter.View {

    @Inject
    @DataField("is-default")
    private HTMLInputElement isDefault;

    @Inject
    @DataField("name")
    private HTMLInputElement name;

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
    private KieEnumSelectElement<AssertBehaviorOption> equalsBehaviorSelect;

    @Inject
    @DataField("event-processing-model-select-container")
    private KieEnumSelectElement<EventProcessingOption> eventProcessingModelSelect;

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
        presenter.addNewIncludedKnowledgeBase();
    }

    @EventHandler("add-package-button")
    private void onAddPackageButtonClicked(final ClickEvent ignore) {
        presenter.addPackage();
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
        this.name.value = name;
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
    public void setKnowledgeSessionsCount(final int size) {
        knowledgeSessionsCount.textContent = Integer.toString(size);
    }

    @Override
    public void setupEqualBehaviorSelect(final KBaseModel kBaseModel) {
        equalsBehaviorSelect.setup(
                AssertBehaviorOption.values(),
                kBaseModel.getEqualsBehavior(),
                equalsBehavior -> {
                    kBaseModel.setEqualsBehavior(equalsBehavior);
                    presenter.fireChangeEvent();
                });
    }

    @Override
    public void setupEventProcessingModelSelect(final KBaseModel kBaseModel) {
        eventProcessingModelSelect.setup(
                EventProcessingOption.values(),
                kBaseModel.getEventProcessingMode(),
                eventProcessingMode -> {
                    kBaseModel.setEventProcessingMode(eventProcessingMode);
                    presenter.fireChangeEvent();
                });
    }

    @EventHandler("name")
    public void onNameChange(final @ForEvent("change") Event event) {
        presenter.onNameChange(name.value);
    }
}
