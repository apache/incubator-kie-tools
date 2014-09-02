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
import org.guvnor.structure.repositories.NewRepositoryEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.uberfire.commons.services.cdi.Startup;

import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.RepositoryService;
import org.uberfire.java.nio.base.version.VersionRecord;

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
    private void init() throws Exception {
        initAuthorMappings();
        initDataSets();
    }

    private void initAuthorMappings() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("author_mappings.properties");
        if (is != null) {
            authorMappings.load(is);
        }
    }

    private void initDataSets() {
        DataSetBuilder dsBuilder = DataSetFactory.newDSBuilder()
                .label(COLUMN_ORG)
                .label(COLUMN_REPO)
                .label(COLUMN_AUTHOR)
                .label(COLUMN_MSG)
                .date(COLUMN_DATE);

        Collection<OrganizationalUnit> orgUnitList = organizationalUnitService.getOrganizationalUnits();
        for (OrganizationalUnit orgUnit : orgUnitList) {
            String org = orgUnit.getName();
            Collection<Repository> repoList = orgUnit.getRepositories();

            for (Repository repo : repoList) {
                String repoAlias = repo.getAlias();

                List<VersionRecord> recordList = repositoryService.getRepositoryHistoryAll(repoAlias);
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

        DataSet dataSet = dsBuilder.buildDataSet();
        dataSet.setUUID(ALL);
        dataSetManager.registerDataSet(dataSet);
    }

    // Catch all the org & repos creation/removal events in order to sync up the contributions data set

    public void onOrganizationUnitCreated(@Observes final NewOrganizationalUnitEvent event) {
        checkNotNull("event", event);
        initDataSets();
    }

    public void onOrganizationUnitRemoved(@Observes final RemoveOrganizationalUnitEvent event) {
        checkNotNull("event", event);
        initDataSets();
    }

    public void onRepositoryCreated(@Observes final NewRepositoryEvent event) {
        checkNotNull("event", event);
        initDataSets();
    }

    public void onRepositoryRemoved(@Observes final RepositoryRemovedEvent event) {
        checkNotNull("event", event);
        initDataSets();
    }
}
