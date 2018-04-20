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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.Element;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.KnowledgeBasesPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.includedknowledgebases.IncludedKnowledgeBaseItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions.KnowledgeSessionsModal;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.packages.PackageItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.select.KieEnumSelectElement;
import org.kie.workbench.common.screens.library.client.settings.util.list.ListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.list.ListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.list.ListItemView;
import org.kie.workbench.common.screens.library.client.settings.util.modal.single.AddSingleValueModal;
import org.kie.workbench.common.services.shared.kmodule.AssertBehaviorOption;
import org.kie.workbench.common.services.shared.kmodule.EventProcessingOption;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;

@Dependent
public class KnowledgeBaseItemPresenter extends ListItemPresenter<KBaseModel, KnowledgeBasesPresenter, KnowledgeBaseItemPresenter.View> {

    private final Event<DefaultKnowledgeBaseChange> defaultKnowledgeBaseChangeEvent;
    private final KieEnumSelectElement<AssertBehaviorOption> equalsBehaviorSelect;
    private final KieEnumSelectElement<EventProcessingOption> eventProcessingModeSelect;
    private final AddSingleValueModal addIncludedKnowledgeBaseModal;
    private final AddSingleValueModal addPackageModal;
    private final KnowledgeSessionsModal knowledgeSessionsModal;
    private final IncludedKnowledgeBasesListPresenter includedKnowledgeBasesListPresenter;
    private final PackageListPresenter packageListPresenter;

    KBaseModel kBaseModel;
    KnowledgeBasesPresenter parentPresenter;

    @Inject
    public KnowledgeBaseItemPresenter(final View view,
                                      final Event<DefaultKnowledgeBaseChange> defaultKnowledgeBaseChangeEvent,
                                      final KieEnumSelectElement<AssertBehaviorOption> equalsBehaviorSelect,
                                      final KieEnumSelectElement<EventProcessingOption> eventProcessingModeSelect,
                                      final AddSingleValueModal addIncludedKnowledgeBaseModal,
                                      final AddSingleValueModal addPackageModal,
                                      final KnowledgeSessionsModal knowledgeSessionsModal,
                                      final IncludedKnowledgeBasesListPresenter includedKnowledgeBasesListPresenter,
                                      final PackageListPresenter packageListPresenter) {
        super(view);
        this.defaultKnowledgeBaseChangeEvent = defaultKnowledgeBaseChangeEvent;
        this.equalsBehaviorSelect = equalsBehaviorSelect;
        this.eventProcessingModeSelect = eventProcessingModeSelect;
        this.addIncludedKnowledgeBaseModal = addIncludedKnowledgeBaseModal;
        this.addPackageModal = addPackageModal;
        this.knowledgeSessionsModal = knowledgeSessionsModal;
        this.includedKnowledgeBasesListPresenter = includedKnowledgeBasesListPresenter;
        this.packageListPresenter = packageListPresenter;
    }

    @Override
    public KnowledgeBaseItemPresenter setup(final KBaseModel kBaseModel,
                                            final KnowledgeBasesPresenter parentPresenter) {
        this.kBaseModel = kBaseModel;
        this.parentPresenter = parentPresenter;

        view.init(this);

        view.setName(kBaseModel.getName());
        view.setDefault(kBaseModel.isDefault());
        view.setKnowledgeSessionsCount(kBaseModel.getKSessions().size());

        knowledgeSessionsModal.setup(this);

        addIncludedKnowledgeBaseModal.setup(LibraryConstants.AddIncludedKnowledgeBase, LibraryConstants.Name);
        addPackageModal.setup(LibraryConstants.AddPackage, LibraryConstants.PackageName);

        equalsBehaviorSelect.setup(
                view.getEqualsBehaviorSelectContainer(),
                AssertBehaviorOption.values(),
                kBaseModel.getEqualsBehavior(),
                equalsBehavior -> {
                    kBaseModel.setEqualsBehavior(equalsBehavior);
                    fireChangeEvent();
                });

        eventProcessingModeSelect.setup(
                view.getEventProcessingModelSelectContainer(),
                EventProcessingOption.values(),
                kBaseModel.getEventProcessingMode(),
                eventProcessingMode -> {
                    kBaseModel.setEventProcessingMode(eventProcessingMode);
                    fireChangeEvent();
                });

        includedKnowledgeBasesListPresenter.setup(
                view.getIncludedKnowledgeBasesListElement(),
                kBaseModel.getIncludes(),
                (knowledgeBaseName, presenter) -> presenter.setup(knowledgeBaseName, this));

        packageListPresenter.setup(
                view.getPackagesListElement(),
                kBaseModel.getPackages(),
                (packageName, presenter) -> presenter.setup(packageName, this));

        return this;
    }

    @Override
    public void remove() {
        super.remove();
        fireChangeEvent();
    }

    public void fireChangeEvent() {
        parentPresenter.fireChangeEvent();
    }

    @Override
    public KBaseModel getObject() {
        return kBaseModel;
    }

    public void showNewIncludedKnowledgeBaseModal() {
        addIncludedKnowledgeBaseModal.show(kBaseName -> {
            includedKnowledgeBasesListPresenter.add(kBaseName);
            parentPresenter.fireChangeEvent();
        });
    }

    public void showAddPackageModal() {
        addPackageModal.show(packageName -> {
            packageListPresenter.add(packageName);
            parentPresenter.fireChangeEvent();
        });
    }

    public void showKnowledgeSessionsModal() {
        knowledgeSessionsModal.show();
    }

    public void signalAddedOrRemoved() {
        view.setKnowledgeSessionsCount(kBaseModel.getKSessions().size());
        parentPresenter.fireChangeEvent();
    }

    public void setDefault(final boolean isDefault) {
        kBaseModel.setDefault(isDefault);
        defaultKnowledgeBaseChangeEvent.fire(new DefaultKnowledgeBaseChange(kBaseModel));
        parentPresenter.fireChangeEvent();
    }

    public void onDefaultKnowledgeSessionChanged(@Observes final DefaultKnowledgeBaseChange event) {
        if (!event.getNewDefault().equals(kBaseModel)) {
            kBaseModel.setDefault(false);
        }
    }

    public interface View extends ListItemView<KnowledgeBaseItemPresenter>,
                                  IsElement {

        void setName(final String name);

        Element getPackagesListElement();

        Element getIncludedKnowledgeBasesListElement();

        void setDefault(final boolean isDefault);

        Element getEqualsBehaviorSelectContainer();

        Element getEventProcessingModelSelectContainer();

        void setKnowledgeSessionsCount(final int size);
    }

    @Dependent
    public static class IncludedKnowledgeBasesListPresenter extends ListPresenter<String, IncludedKnowledgeBaseItemPresenter> {

        @Inject
        public IncludedKnowledgeBasesListPresenter(ManagedInstance<IncludedKnowledgeBaseItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    @Dependent
    public static class PackageListPresenter extends ListPresenter<String, PackageItemPresenter> {

        @Inject
        public PackageListPresenter(ManagedInstance<PackageItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
