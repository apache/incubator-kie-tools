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

package org.guvnor.common.services.project.client.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.guvnor.common.services.project.client.resources.ProjectResources;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

public class ConflictingRepositoriesPopupViewImpl
        extends BaseModal
        implements ConflictingRepositoriesPopupView {

    interface Binder
            extends UiBinder<Widget, ConflictingRepositoriesPopupViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    Heading header;

    @UiField(provided = true)
    CellTable<MavenRepositoryMetadata> table = new CellTable<MavenRepositoryMetadata>();

    private final GenericModalFooter footer = new GenericModalFooter();

    private Presenter presenter;

    private List<MavenRepositoryMetadata> repositories = new ArrayList<MavenRepositoryMetadata>();
    private ListDataProvider<MavenRepositoryMetadata> dataProvider = new ListDataProvider<MavenRepositoryMetadata>();

    TextColumn<MavenRepositoryMetadata> repositoryIdColumn;
    TextColumn<MavenRepositoryMetadata> repositoryUrlColumn;
    TextColumn<MavenRepositoryMetadata> repositorySourceColumn;

    public ConflictingRepositoriesPopupViewImpl() {
        setTitle(ProjectResources.CONSTANTS.ConflictingRepositoriesTitle());
        setBody(uiBinder.createAndBindUi(this));
        add(footer);
        setup();
    }

    private void setup() {
        //Setup table
        table.setStriped(true);
        table.setCondensed(true);
        table.setBordered(true);

        //Columns
        repositoryIdColumn = new TextColumn<MavenRepositoryMetadata>() {

            @Override
            public String getValue(final MavenRepositoryMetadata metadata) {
                return metadata.getId();
            }
        };
        repositoryUrlColumn = new TextColumn<MavenRepositoryMetadata>() {

            @Override
            public String getValue(final MavenRepositoryMetadata metadata) {
                return metadata.getUrl();
            }
        };
        repositorySourceColumn = new TextColumn<MavenRepositoryMetadata>() {

            @Override
            public String getValue(final MavenRepositoryMetadata metadata) {
                switch (metadata.getSource()) {
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
        this.presenter = presenter;
    }

    @Override
    public void clear() {
        footer.clear();
    }

    @Override
    public void setContent(final GAV gav,
                           final Set<MavenRepositoryMetadata> repositories) {
        this.repositories = sortRepositories(repositories);
        this.dataProvider.setList(this.repositories);
        this.header.setText(ProjectResources.CONSTANTS.ConflictingRepositoriesGAVDescription(gav.getGroupId(),
                                                                                             gav.getArtifactId(),
                                                                                             gav.getVersion()));
    }

    private List<MavenRepositoryMetadata> sortRepositories(final Set<MavenRepositoryMetadata> repositories) {
        final List<MavenRepositoryMetadata> sortedRepositories = new ArrayList<MavenRepositoryMetadata>();
        sortedRepositories.addAll(repositories);
        Collections.sort(sortedRepositories,
                (md1, md2) -> {
                    if (md1.getSource().equals(md2.getSource())) {
                        return md1.getId().compareToIgnoreCase(md2.getId());
                    }
                    return md1.getSource().ordinal() - md2.getSource().ordinal();
                });
        return sortedRepositories;
    }

    @Override
    public void addOKButton() {
        footer.addButton(CommonConstants.INSTANCE.OK(),
                         presenter::hide,
                         IconType.PLUS,
                         ButtonType.PRIMARY);
    }

    @Override
    public void addOverrideButton() {
        footer.addButton(ProjectResources.CONSTANTS.ConflictingRepositoriesOverride(),
                         presenter::override,
                         ButtonType.DEFAULT);
    }
}
