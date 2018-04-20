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

package org.kie.workbench.common.screens.library.client.settings.sections.externaldataobjects;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.promise.Promise;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionView;
import org.kie.workbench.common.screens.library.client.settings.util.list.ListPresenter;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.widgets.configresource.client.widget.unbound.AddImportPopup;
import org.uberfire.client.promise.Promises;

public class ExternalDataObjectsPresenter extends Section<ProjectScreenModel>  {

    private final View view;
    private final ImportsListPresenter itemPresenters;
    private final AddImportPopup addImportPopup;

    private Imports imports;

    public interface View extends SectionView<ExternalDataObjectsPresenter> {

        void remove(final ExternalDataObjectsItemPresenter.View view);

        void add(final ExternalDataObjectsItemPresenter.View view);

        Element getImportsTable();
    }

    @Inject
    public ExternalDataObjectsPresenter(final View view,
                                        final Promises promises,
                                        final MenuItem<ProjectScreenModel> menuItem,
                                        final AddImportPopup addImportPopup,
                                        final ImportsListPresenter itemPresenters,
                                        final Event<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent) {

        super(settingsSectionChangeEvent, menuItem, promises);
        this.view = view;
        this.itemPresenters = itemPresenters;
        this.addImportPopup = addImportPopup;
    }

    @Override
    public Promise<Void> setup(final ProjectScreenModel model) {

        imports = model.getProjectImports().getImports();

        view.init(this);

        itemPresenters.setup(view.getImportsTable(),
                             imports.getImports(),
                             (import_, presenter) -> presenter.setup(import_, this));

        return promises.resolve();
    }

    public void openAddPopup() {
        addImportPopup.show();
        addImportPopup.setCommand(() -> addImport(addImportPopup.getImportType()));
    }

    void addImport(final String typeName) {
        itemPresenters.add(new Import(typeName));
        fireChangeEvent();
    }

    @Override
    public int currentHashCode() {
        return imports.hashCode();
    }

    @Override
    public SectionView<?> getView() {
        return view;
    }

    @Dependent
    public static class ImportsListPresenter extends ListPresenter<Import, ExternalDataObjectsItemPresenter> {

        @Inject
        public ImportsListPresenter(final ManagedInstance<ExternalDataObjectsItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
