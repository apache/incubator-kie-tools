/*
 * Copyright 2014 JBoss Inc
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
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetBuilder;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetManager;
import org.guvnor.structure.organizationalunit.NewOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoAddedToOrganizationaUnitEvent;
import org.guvnor.structure.organizationalunit.RepoRemovedFromOrganizationalUnitEvent;
import org.guvnor.structure.repositories.NewRepositoryEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.uberfire.commons.services.cdi.Startup;

import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.RepositoryService;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import static org.uberfire.commons.validation.PortablePreconditions.*;

import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSets.*;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.*;

/**
 * This class is in charge of the initialization of a dashbuilder's data set holding all the
 * contributions made to the any of the managed repositories.
 */
@Startup
@ApplicationScoped
public class ContributorsManager {

    @Inject
    private OrganizationalUnitService organizationalUnitService;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private DataSetManager dataSetManager;

    /**
     * Map holding alias to author name mappings.
     */
    private Properties authorMappings = new Properties();

    @PostConstruct
    private void init() {
        initAuthorMappings();
        initDataSets();
    }

    private void initAuthorMappings() {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("author_mappings.properties");
        if (is != null) {
            try {
                authorMappings.load(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initDataSets() {
        DataSetBuilder dsBuilder = DataSetFactory.newDataSetBuilder()
                .label(COLUMN_ORG)
                .label(COLUMN_REPO)
                .label(COLUMN_AUTHOR)
                .label(COLUMN_MSG)
                .date(COLUMN_DATE);

        Collection<OrganizationalUnit> orgUnitList = organizationalUnitService.getOrganizationalUnits();
        for (OrganizationalUnit orgUnit : orgUnitList) {
            String org = orgUnit.getName();
            Collection<Repository> repoList = orgUnit.getRepositories();

            if (repoList.isEmpty()) {
                dsBuilder.row(org, null, null, "Empty organizational unit", null);
            } else {
                for (Repository repo : repoList) {
                    String repoAlias = repo.getAlias();
                    List<VersionRecord> recordList = repositoryService.getRepositoryHistoryAll(repoAlias);

                    if (recordList.isEmpty()) {
                        dsBuilder.row(org, repoAlias, null, "Empty repository", null);
                    } else {
                        for (VersionRecord record : recordList) {
                            String alias = record.author();
                            String author = authorMappings.getProperty(alias);
                            if (author == null) author = alias;
                            String msg = record.comment();
                            Date date = record.date();
                            dsBuilder.row(org, repoAlias, author, msg, date);
                        }
                    }
                }
            }
        }

        DataSet dataSet = dsBuilder.buildDataSet();
        dataSet.setUUID(ALL);
        dataSetManager.registerDataSet(dataSet);
    }

    // Keep synced the contributions data set with the changes made into the org>repos>commits hierarchy

    public void onRepoAddedToOrgUnit(@Observes final RepoAddedToOrganizationaUnitEvent event) {
        checkNotNull("event", event);
        initDataSets();
    }

    public void onRepoRemovedFromOrgUnit(@Observes final RepoRemovedFromOrganizationalUnitEvent event) {
        checkNotNull("event", event);
        initDataSets();
    }

    public void onOrganizationUnitAdded(@Observes final NewOrganizationalUnitEvent event) {
        checkNotNull("event", event);
        initDataSets();
    }

    public void onOrganizationUnitRemoved(@Observes final RemoveOrganizationalUnitEvent event) {
        checkNotNull("event", event);
        initDataSets();
    }

    public void processResourceAdd(@Observes final ResourceAddedEvent event) {
        checkNotNull("event", event);
        initDataSets();
    }

    public void processResourceDelete(@Observes final ResourceDeletedEvent event) {
        checkNotNull("event", event);
        initDataSets();
    }

    public void processResourceUpdate(@Observes final ResourceUpdatedEvent event) {
        checkNotNull("event", event);
        initDataSets();
    }

    public void processResourceCopied(@Observes final ResourceCopiedEvent event) {
        checkNotNull("event", event);
        initDataSets();
    }

    public void processResourceRenamed(@Observes final ResourceRenamedEvent event) {
        checkNotNull("event", event);
        initDataSets();
    }

    public void processBatchChanges(@Observes final ResourceBatchChangesEvent event) {
        checkNotNull("event", event);
        initDataSets();
    }
}
