/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.forms.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.guvnor.common.services.project.client.resources.ProjectResources;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;

public class RepositoriesWidgetViewImpl
        extends Composite
        implements RepositoriesWidgetView {

    interface Binder
            extends UiBinder<Widget, RepositoriesWidgetViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField(provided = true)
    CellTable<ModuleRepositories.ModuleRepository> table = new CellTable<ModuleRepositories.ModuleRepository>();

    private List<ModuleRepositories.ModuleRepository> repositories = new ArrayList<ModuleRepositories.ModuleRepository>();
    private ListDataProvider<ModuleRepositories.ModuleRepository> dataProvider = new ListDataProvider<ModuleRepositories.ModuleRepository>();

    Column<ModuleRepositories.ModuleRepository, Boolean> repositoryIncludeColumn;
    TextColumn<ModuleRepositories.ModuleRepository> repositoryIdColumn;
    TextColumn<ModuleRepositories.ModuleRepository> repositoryUrlColumn;
    TextColumn<ModuleRepositories.ModuleRepository> repositorySourceColumn;

    public RepositoriesWidgetViewImpl() {
        setup();
        initWidget(uiBinder.createAndBindUi(this));
    }

    private void setup() {
        //Setup table
        table.setStriped(true);
        table.setCondensed(true);
        table.setBordered(true);

        //Columns
        final CheckboxCell cbCell = new CheckboxCell();
        repositoryIncludeColumn = new Column<ModuleRepositories.ModuleRepository, Boolean>(cbCell) {
            @Override
            public Boolean getValue(final ModuleRepositories.ModuleRepository repository) {
                return repository.isIncluded();
            }
        };

        repositoryIdColumn = new TextColumn<ModuleRepositories.ModuleRepository>() {

            @Override
            public String getValue(final ModuleRepositories.ModuleRepository repository) {
                return repository.getMetadata().getId();
            }
        };
        repositoryUrlColumn = new TextColumn<ModuleRepositories.ModuleRepository>() {

            @Override
            public String getValue(final ModuleRepositories.ModuleRepository repository) {
                return repository.getMetadata().getUrl();
            }
        };
        repositorySourceColumn = new TextColumn<ModuleRepositories.ModuleRepository>() {

            @Override
            public String getValue(final ModuleRepositories.ModuleRepository repository) {
                switch (repository.getMetadata().getSource()) {
                    case LOCAL:
                        return ProjectResources.CONSTANTS.RepositorySourceLocal();
                    case PROJECT:
                        return ProjectResources.CONSTANTS.RepositorySourceProject();
                    case SETTINGS:
                        return ProjectResources.CONSTANTS.RepositorySourceSettings();
                    case DISTRIBUTION_MANAGEMENT:
                        return ProjectResources.CONSTANTS.RepositorySourceDistributionManagement();
                }
                return ProjectResources.CONSTANTS.RepositorySourceUnknown();
            }
        };

        table.addColumn(repositoryIncludeColumn,
                        new TextHeader(ProjectEditorResources.CONSTANTS.RepositoryInclude()));
        table.addColumn(repositoryIdColumn,
                        new TextHeader(ProjectResources.CONSTANTS.RepositoryId()));
        table.addColumn(repositoryUrlColumn,
                        new TextHeader(ProjectResources.CONSTANTS.RepositoryUrl()));
        table.addColumn(repositorySourceColumn,
                        new TextHeader(ProjectResources.CONSTANTS.RepositorySource()));

        //Link data
        dataProvider.addDataDisplay(table);
        dataProvider.setList(repositories);
    }

    @Override
    public void init(final Presenter presenter) {
        repositoryIncludeColumn.setFieldUpdater(new FieldUpdater<ModuleRepositories.ModuleRepository, Boolean>() {
            @Override
            public void update(final int index,
                               final ModuleRepositories.ModuleRepository repository,
                               final Boolean value) {
                presenter.setIncludeRepository(repository,
                                               Boolean.TRUE.equals(value));
            }
        });
    }

    @Override
    public void setContent(final Set<ModuleRepositories.ModuleRepository> repositories,
                           final boolean isReadOnly) {
        this.repositories = sortRepositories(repositories);
        this.dataProvider.setList(this.repositories);
        ((CheckboxCell) this.repositoryIncludeColumn.getCell()).setEnabled(!isReadOnly);
    }

    private List<ModuleRepositories.ModuleRepository> sortRepositories(final Set<ModuleRepositories.ModuleRepository> repositories) {
        final List<ModuleRepositories.ModuleRepository> sortedRepositories = new ArrayList<ModuleRepositories.ModuleRepository>();
        sortedRepositories.addAll(repositories);
        Collections.sort(sortedRepositories,
                         new Comparator<ModuleRepositories.ModuleRepository>() {
                             @Override
                             public int compare(final ModuleRepositories.ModuleRepository pr1,
                                                final ModuleRepositories.ModuleRepository pr2) {
                                 final MavenRepositoryMetadata md1 = pr1.getMetadata();
                                 final MavenRepositoryMetadata md2 = pr2.getMetadata();
                                 if (md1.getSource().equals(md2.getSource())) {
                                     return md1.getId().compareToIgnoreCase(md2.getId());
                                 }
                                 return md1.getSource().ordinal() - md2.getSource().ordinal();
                             }
                         });
        return sortedRepositories;
    }
}
