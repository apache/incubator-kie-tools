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

package org.dashbuilder.kieserver.backend.rest;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.dashbuilder.kieserver.KieServerConnectionInfo;

/**
 * Run queries on Kie Server using its REST API
 * 
 */
@ApplicationScoped
public class KieServerQueryClient {

    private static final String REQUEST_MEDIA_TYPE = MediaType.APPLICATION_JSON;

    public static final String QUERY_MAP_RAW = "RawList";

    public static final String QUERY_DEFINITION_URI = "queries/definitions/{id}";
    public static final String QUERY_EXECUTION_URI = QUERY_DEFINITION_URI + "/filtered-data";

    public QueryDefinition getQuery(KieServerConnectionInfo connectionInfo, String uuid) {
        Client client = ClientBuilder.newClient();
        WebTarget target = requestForQueryDefinition(connectionInfo, uuid, client);

        QueryDefinition queryDefinition = target.request()
                                                .accept(REQUEST_MEDIA_TYPE)
                                                .get(QueryDefinition.class);
        client.close();
        return queryDefinition;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<List> query(KieServerConnectionInfo connectionInfo,
                            String uuid,
                            QueryFilterSpec filterSpec,
                            int i,
                            int numberOfRows) {

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(connectionInfo.getLocation().get())
                                 .path(QUERY_EXECUTION_URI)
                                 .resolveTemplate("id", uuid)
                                 .queryParam("mapper", QUERY_MAP_RAW)
                                 .queryParam("page", i)
                                 .queryParam("pageSize", numberOfRows);

        addAuth(connectionInfo, target);

        List<List> response = target.request()
                                    .accept(REQUEST_MEDIA_TYPE)
                                    .post(Entity.entity(filterSpec, REQUEST_MEDIA_TYPE), List.class);
        client.close();
        return response;
    }

    public QueryDefinition replaceQuery(KieServerConnectionInfo connectionInfo, QueryDefinition queryDefinition) {
        Client client = ClientBuilder.newClient();
        WebTarget target = requestForQueryDefinition(connectionInfo, queryDefinition.getName(), client);
        QueryDefinition def = target.request()
                     .accept(REQUEST_MEDIA_TYPE)
                     .put(Entity.entity(queryDefinition, REQUEST_MEDIA_TYPE), QueryDefinition.class);
        client.close();
        return def;

    }

    public void unregisterQuery(KieServerConnectionInfo connectionInfo, String dataSetUUID) {
        Client client = ClientBuilder.newClient();
        WebTarget target = requestForQueryDefinition(connectionInfo, dataSetUUID, client);
        target.request().delete();
        client.close();
    }

    private WebTarget requestForQueryDefinition(KieServerConnectionInfo connectionInfo,
                                                String dataSetUUID,
                                                Client client) {
        WebTarget target = client.target(connectionInfo.getLocation().get())
                                 .path(QUERY_DEFINITION_URI)
                                 .resolveTemplate("id", dataSetUUID);

        addAuth(connectionInfo, target);
        return target;
    }

    private void addAuth(KieServerConnectionInfo connectionInfo, WebTarget target) {
        if (connectionInfo.getUser().isPresent()) {
            String user = connectionInfo.getUser().get();
            String password = connectionInfo.getPassword().orElse("");
            target.register(new BasicAuthFilter(user, password));
        }

        if (connectionInfo.getToken().isPresent()) {
            target.register(new TokenFilter(connectionInfo.getToken().get()));
        }
    }

}