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

package org.kie.workbench.common.screens.library.client.settings.knowledgebases;

import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.promise.Promise;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.client.promise.Promises;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.knowledgebases.item.KnowledgeBaseItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.ListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.modal.single.AddSingleValueModal;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;

import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;

public class KnowledgeBasesPresenter extends SettingsPresenter.Section {

    private final AddSingleValueModal addKnowledgeBaseModal;
    private final KnowledgeBaseListPresenter knowledgeBaseListPresenter;
    private final View view;

    KModuleModel kModuleModel;

    public interface View extends SettingsPresenter.View.Section<KnowledgeBasesPresenter> {

        Element getKnowledgeBasesTable();
    }

    @Inject
    public KnowledgeBasesPresenter(final View view,
                                   final Event<SettingsSectionChange> settingsSectionChangeEvent,
                                   final Promises promises,
                                   final SettingsPresenter.MenuItem menuItem,
                                   final AddSingleValueModal addKnowledgeBaseModal,
                                   final KnowledgeBaseListPresenter knowledgeBaseListPresenter) {

        super(settingsSectionChangeEvent, menuItem, promises);
        this.addKnowledgeBaseModal = addKnowledgeBaseModal;
        this.knowledgeBaseListPresenter = knowledgeBaseListPresenter;
        this.view = view;
    }

    @Override
    public Promise<Void> setup(final ProjectScreenModel model) {

        this.kModuleModel = model.getKModule();

        view.init(this);

        addKnowledgeBaseModal.setup(LibraryConstants.AddKnowledgeBase, LibraryConstants.Name);

        knowledgeBaseListPresenter.setup(
                view.getKnowledgeBasesTable(),
                model.getKModule().getKBases().values().stream().sorted(comparing(KBaseModel::getName)).collect(Collectors.toList()),
                (kbase, presenter) -> presenter.setup(kbase, this));

        return promises.resolve();
    }

    @Override
    public Promise<Void> save(final String comment,
                              final Supplier<Promise<Void>> chain) {

        kModuleModel.getKBases().clear();
        kModuleModel.getKBases().putAll(
                knowledgeBaseListPresenter.getObjectsList().stream()
                        .collect(Collectors.toMap(KBaseModel::getName, identity())));

        return promises.resolve();
    }

    void openAddKnowledgeBaseModal() {
        addKnowledgeBaseModal.show(knowledgeBaseName -> {
            knowledgeBaseListPresenter.add(newKBaseModel(knowledgeBaseName));
            fireChangeEvent();
        });
    }

    KBaseModel newKBaseModel(final String knowledgeBaseName) {
        final KBaseModel kBaseModel = new KBaseModel();
        kBaseModel.setName(knowledgeBaseName);
        kBaseModel.setDefault(knowledgeBaseListPresenter.getObjectsList().isEmpty());
        return kBaseModel;
    }

    @Override
    public SettingsPresenter.View.Section getView() {
        return view;
    }

    @Override
    public int currentHashCode() {
        return knowledgeBaseListPresenter.hashCode();
    }

    @Dependent
    public static class KnowledgeBaseListPresenter extends ListPresenter<KBaseModel, KnowledgeBaseItemPresenter> {

        @Inject
        public KnowledgeBaseListPresenter(final ManagedInstance<KnowledgeBaseItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
