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

package org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases;

import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.promise.Promise;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.KnowledgeBaseItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionView;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.kie.workbench.common.widgets.client.widget.ListPresenter;
import org.uberfire.client.promise.Promises;

import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;

public class KnowledgeBasesPresenter extends Section<ProjectScreenModel>  {

    private final KnowledgeBaseListPresenter knowledgeBaseListPresenter;
    private final View view;

    KModuleModel kModuleModel;

    public interface View extends SectionView<KnowledgeBasesPresenter> {

        Element getKnowledgeBasesTable();
    }

    @Inject
    public KnowledgeBasesPresenter(final View view,
                                   final Event<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent,
                                   final Promises promises,
                                   final MenuItem<ProjectScreenModel> menuItem,
                                   final KnowledgeBaseListPresenter knowledgeBaseListPresenter) {

        super(settingsSectionChangeEvent, menuItem, promises);
        this.knowledgeBaseListPresenter = knowledgeBaseListPresenter;
        this.view = view;
    }

    @Override
    public Promise<Void> setup(final ProjectScreenModel model) {

        this.kModuleModel = model.getKModule();

        view.init(this);

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

    void addKnowledgeBase() {
        knowledgeBaseListPresenter.add(newKBaseModel(""));
        fireChangeEvent();
    }

    KBaseModel newKBaseModel(final String knowledgeBaseName) {
        final KBaseModel kBaseModel = new KBaseModel();
        kBaseModel.setName(knowledgeBaseName);
        kBaseModel.setDefault(knowledgeBaseListPresenter.getObjectsList().isEmpty());
        return kBaseModel;
    }

    @Override
    public SectionView<?> getView() {
        return view;
    }

    @Override
    public int currentHashCode() {
        return knowledgeBaseListPresenter.getObjectsList().hashCode();
    }

    @Dependent
    public static class KnowledgeBaseListPresenter extends ListPresenter<KBaseModel, KnowledgeBaseItemPresenter> {

        @Inject
        public KnowledgeBaseListPresenter(final ManagedInstance<KnowledgeBaseItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
