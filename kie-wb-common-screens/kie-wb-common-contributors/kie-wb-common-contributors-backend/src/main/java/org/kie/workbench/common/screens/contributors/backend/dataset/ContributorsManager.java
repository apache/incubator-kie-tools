/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.contributors.backend.dataset;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetBuilder;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetGenerator;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.events.DataSetStaleEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.organizationalunit.NewOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoAddedToOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoRemovedFromOrganizationalUnitEvent;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.ext.editor.commons.backend.version.VersionRecordService;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_AUTHOR;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_DATE;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_MSG;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_ORG;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_PROJECT;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_REPO;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSets.GIT_CONTRIB;

/**
 * This class is in charge of the initialization of a data set holding all the
 * contributions made to any of the GIT managed repositories.
 */
@Startup
@ApplicationScoped
public class ContributorsManager implements DataSetGenerator {

    @Inject
    protected DataSetDefRegistry dataSetDefRegistry;

    @Inject
    protected OrganizationalUnitService organizationalUnitService;

    @Inject
    protected WorkspaceProjectService projectService;

    @Inject
    protected VersionRecordService recordService;

    @Inject
    protected Event<DataSetStaleEvent> dataSetStaleEvent;

    /**
     * Map holding alias to author name mappings
     */
    protected Properties authorMappings = new Properties();

    /**
     * The GIT contributors data set definition
     */
    protected DataSetDef dataSetdef = DataSetDefFactory.newBeanDataSetDef()
            .uuid(GIT_CONTRIB)
            .name("GIT Contributors")
            .generatorClass(ContributorsManager.class.getName())
            .label(COLUMN_ORG)
            .label(COLUMN_REPO)
            .label(COLUMN_PROJECT)
            .label(COLUMN_AUTHOR)
            .text(COLUMN_MSG)
            .date(COLUMN_DATE)
            .buildDef();

    @PostConstruct
    protected void init() {
        dataSetdef.setPublic(false);
        dataSetDefRegistry.registerDataSetDef(dataSetdef);

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("author_mappings.properties");
        if (is != null) {
            try {
                authorMappings.load(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public DataSet buildDataSet(Map<String, String> params) {
        final DataSetBuilder dsBuilder = DataSetFactory.newDataSetBuilder();
        for (final DataColumnDef columnDef : dataSetdef.getColumns()) {
            dsBuilder.column(columnDef.getId(),
                             columnDef.getColumnType());
        }

        final Collection<OrganizationalUnit> orgUnitList = organizationalUnitService.getOrganizationalUnits();
        for (final OrganizationalUnit orgUnit : orgUnitList) {
            final String org = orgUnit.getName();
            final Collection<WorkspaceProject> projects = projectService.getAllWorkspaceProjects(orgUnit);

            if (projects.isEmpty()) {
                dsBuilder.row(org,//org
                              null,//repo
                              null,//project
                              null,//author
                              null,//message
                              null);//date

            } else {

                for (final WorkspaceProject project : projects) {
                    final String repoAlias = project.getRepository().getAlias();
                    final String projectName = project.getName();
                    org.uberfire.backend.vfs.Path rootPath = project.getRootPath();
                    final Path projectRoot = Paths.convert(rootPath);
                    final List<VersionRecord> recordList = recordService.loadVersionRecords(projectRoot);

                    if (recordList.isEmpty()) {
                        dsBuilder.row(org, //org
                                      repoAlias,//repo
                                      null,//project
                                      null,//author
                                      "Empty project", //mesage
                                      null);//date
                    } else {
                        for (VersionRecord record : recordList) {
                            String alias = record.author();
                            String author = authorMappings.getProperty(alias);
                            author = author == null ? alias : author;
                            String msg = record.comment();
                            Date date = record.date();
                            dsBuilder.row(org,
                                          repoAlias,
                                          projectName,
                                          author,
                                          msg,
                                          date);
                        }
                    }
                }
            }
        }

        DataSet dataSet = dsBuilder.buildDataSet();
        dataSet.setUUID(GIT_CONTRIB);
        return dataSet;
    }

    protected void invalidateDataSet() {
        dataSetStaleEvent.fire(new DataSetStaleEvent(dataSetdef));
    }

    // Keep synced the contributions data set with the changes made into the org>repos>commits hierarchy

    public void onRepoAddedToOrgUnit(@Observes final RepoAddedToOrganizationalUnitEvent event) {
        checkNotNull("event",
                     event);
        invalidateDataSet();
    }

    public void onRepoRemovedFromOrgUnit(@Observes final RepoRemovedFromOrganizationalUnitEvent event) {
        checkNotNull("event",
                     event);
        invalidateDataSet();
    }

    public void onOrganizationUnitAdded(@Observes final NewOrganizationalUnitEvent event) {
        checkNotNull("event",
                     event);
        invalidateDataSet();
    }

    public void onOrganizationUnitRemoved(@Observes final RemoveOrganizationalUnitEvent event) {
        checkNotNull("event",
                     event);
        invalidateDataSet();
    }

    public void processResourceAdd(@Observes final ResourceAddedEvent event) {
        checkNotNull("event",
                     event);
        invalidateDataSet();
    }

    public void processResourceDelete(@Observes final ResourceDeletedEvent event) {
        checkNotNull("event",
                     event);
        invalidateDataSet();
    }

    public void processResourceUpdate(@Observes final ResourceUpdatedEvent event) {
        checkNotNull("event",
                     event);
        invalidateDataSet();
    }

    public void processResourceCopied(@Observes final ResourceCopiedEvent event) {
        checkNotNull("event",
                     event);
        invalidateDataSet();
    }

    public void processResourceRenamed(@Observes final ResourceRenamedEvent event) {
        checkNotNull("event",
                     event);
        invalidateDataSet();
    }

    public void processBatchChanges(@Observes final ResourceBatchChangesEvent event) {
        checkNotNull("event",
                     event);
        invalidateDataSet();
    }
}
