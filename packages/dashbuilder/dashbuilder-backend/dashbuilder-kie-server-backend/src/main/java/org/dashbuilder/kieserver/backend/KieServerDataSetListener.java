/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.kieserver.backend;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.events.DataSetDefModifiedEvent;
import org.dashbuilder.dataset.events.DataSetDefRegisteredEvent;
import org.dashbuilder.dataset.events.DataSetDefRemovedEvent;
import org.dashbuilder.kieserver.KieServerConnectionInfo;
import org.dashbuilder.kieserver.KieServerConnectionInfoProvider;
import org.dashbuilder.kieserver.RemoteDataSetDef;
import org.dashbuilder.kieserver.backend.rest.KieServerQueryClient;
import org.dashbuilder.kieserver.backend.rest.QueryDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keeps Kie Server up to date with remote dataset changes
 *
 */
@ApplicationScoped
public class KieServerDataSetListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(KieServerDataSetListener.class);

    @Inject
    KieServerQueryClient queryClient;

    @Inject
    KieServerConnectionInfoProvider connectionInfoProvider;

    void onDataSetDefRegisteredEvent(@Observes DataSetDefRegisteredEvent event) {
        DataSetDef def = event.getDataSetDef();
        replaceQueryInKieServers(def);
    }

    void onDataSetDefModifiedEvent(@Observes DataSetDefModifiedEvent event) {
        DataSetDef def = event.getNewDataSetDef();
        replaceQueryInKieServers(def);
    }

    void onDataSetDefRemovedEvent(@Observes DataSetDefRemovedEvent event) {
        DataSetDef def = event.getDataSetDef();
        if (def instanceof RemoteDataSetDef) {
            try {
                KieServerConnectionInfo connectionInfo = connectionInfoProvider.verifiedConnectionInfo((RemoteDataSetDef) def);
                queryClient.unregisterQuery(connectionInfo, def.getUUID());
                LOGGER.info("Data set definition {} ({}) deletion event processed", def.getUUID(), def.getName());
            } catch (Exception e) {
                LOGGER.warn("Not able to delete query in server for removed dataset definition {} ", def.getName());
                LOGGER.debug("Not able to delete query in server for removed dataset definition {}", def.getName(), e);
            }
        }
    }

    protected void replaceQueryInKieServers(DataSetDef def) {
        if (def instanceof RemoteDataSetDef && ((RemoteDataSetDef) def).getServerTemplateId() != null) {
            try {
                KieServerConnectionInfo connectionInfo = connectionInfoProvider.verifiedConnectionInfo((RemoteDataSetDef) def);
                QueryDefinition queryDefinition = QueryDefinition.builder()
                                                                 .name(def.getUUID())
                                                                 .source(((RemoteDataSetDef) def).getDataSource())
                                                                 .target(((RemoteDataSetDef) def).getQueryTarget())
                                                                 .expression(((RemoteDataSetDef) def).getDbSQL())
                                                                 .build();

                queryClient.replaceQuery(connectionInfo, queryDefinition);
                LOGGER.info("Data set definition {} ({}) modification event processed", def.getUUID(), def.getName());
            } catch (Exception e) {
                LOGGER.warn("Not able to replace query in server for dataset definition {} ", def.getName());
                LOGGER.debug("Not able to replace query in server for dataset definition {} ", def.getName(), e);
            }
        }
    }

}