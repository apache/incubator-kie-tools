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
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.KnowledgeBasesPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.includedknowledgebases.IncludedKnowledgeBaseItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions.KnowledgeSessionsModal;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.packages.PackageItemPresenter;
import org.kie.workbench.common.services.shared.kmodule.SingleValueItemObjectModel;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.widgets.client.widget.ListItemPresenter;
import org.kie.workbench.common.widgets.client.widget.ListItemView;
import org.kie.workbench.common.widgets.client.widget.ListPresenter;

@Dependent
public class KnowledgeBaseItemPresenter extends ListItemPresenter<KBaseModel, KnowledgeBasesPresenter, KnowledgeBaseItemPresenter.View> {

    private final Event<DefaultKnowledgeBaseChange> defaultKnowledgeBaseChangeEvent;
    private final KnowledgeSessionsModal knowledgeSessionsModal;
    private final IncludedKnowledgeBasesListPresenter includedKnowledgeBasesListPresenter;
    private final PackageListPresenter packageListPresenter;

    KBaseModel kBaseModel;
    KnowledgeBasesPresenter parentPresenter;

    @Inject
    public KnowledgeBaseItemPresenter(final View view,
                                      final Event<DefaultKnowledgeBaseChange> defaultKnowledgeBaseChangeEvent,
                                      final KnowledgeSessionsModal knowledgeSessionsModal,
                                      final IncludedKnowledgeBasesListPresenter includedKnowledgeBasesListPresenter,
                                      final PackageListPresenter packageListPresenter) {
        super(view);
        this.defaultKnowledgeBaseChangeEvent = defaultKnowledgeBaseChangeEvent;
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

        view.setupEqualBehaviorSelect(kBaseModel);
        view.setupEventProcessingModelSelect(kBaseModel);

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

    public void onNameChange(final String name){
        kBaseModel.setName(name);
        fireChangeEvent();
    }

    public void fireChangeEvent() {
        parentPresenter.fireChangeEvent();
    }

    @Override
    public KBaseModel getObject() {
        return kBaseModel;
    }

    public void addNewIncludedKnowledgeBase() {
        includedKnowledgeBasesListPresenter.add(new SingleValueItemObjectModel(""));
        parentPresenter.fireChangeEvent();;
    }

    public void addPackage() {
        packageListPresenter.add(new SingleValueItemObjectModel(""));
        parentPresenter.fireChangeEvent();
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

        void setupEqualBehaviorSelect(final KBaseModel kBaseModel);

        void setupEventProcessingModelSelect(final KBaseModel kBaseModel);

        void setKnowledgeSessionsCount(final int size);

    }

    @Dependent
    public static class IncludedKnowledgeBasesListPresenter extends ListPresenter<SingleValueItemObjectModel, IncludedKnowledgeBaseItemPresenter> {

        @Inject
        public IncludedKnowledgeBasesListPresenter(ManagedInstance<IncludedKnowledgeBaseItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    @Dependent
    public static class PackageListPresenter extends ListPresenter<SingleValueItemObjectModel, PackageItemPresenter> {

        @Inject
        public PackageListPresenter(ManagedInstance<PackageItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
