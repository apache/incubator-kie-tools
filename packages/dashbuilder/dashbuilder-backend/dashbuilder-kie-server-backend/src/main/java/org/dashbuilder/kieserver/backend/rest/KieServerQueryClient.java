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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dashbuilder.kieserver.KieServerConnectionInfo;

/**
 * Run queries on Kie Server using its REST API
 * 
 */
@ApplicationScoped
public class KieServerQueryClient {

    private static final String PROCESS_ID_PARAM = "processId";
    private static final String CONTAINER_ID_PARAM = "containerId";

    private static final String DEFAULT_REQUEST_MEDIA_TYPE = MediaType.APPLICATION_JSON;

    public static final String QUERY_MAP_RAW = "RawList";

    public static final String QUERY_DEFINITION_URI = "queries/definitions/{id}";
    public static final String QUERY_EXECUTION_URI = QUERY_DEFINITION_URI + "/filtered-data";
    public static final String PROCESS_SVG_URI = "containers/{" + CONTAINER_ID_PARAM + "}/images/processes/{" +
            PROCESS_ID_PARAM + "}";

    public static ObjectMapper objectMapper;

    @PostConstruct
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    public QueryDefinition getQuery(KieServerConnectionInfo connectionInfo, String uuid) {
        var client = ClientBuilder.newClient();
        var target = requestForQueryDefinition(connectionInfo, uuid, client);
        var queryDefinition = target.request()
                .accept(DEFAULT_REQUEST_MEDIA_TYPE)
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
        var client = ClientBuilder.newClient();
        var specJson = toJson(filterSpec);
        var target = client.target(connectionInfo.getLocation().get())
                .path(QUERY_EXECUTION_URI)
                .resolveTemplate("id", uuid)
                .queryParam("mapper", QUERY_MAP_RAW)
                .queryParam("page", i)
                .queryParam("pageSize", numberOfRows);

        addAuth(connectionInfo, target);

        var response = target.request()
                .accept(DEFAULT_REQUEST_MEDIA_TYPE)
                .post(Entity.entity(specJson, DEFAULT_REQUEST_MEDIA_TYPE), List.class);
        client.close();
        return response;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<List> queryAll(KieServerConnectionInfo connectionInfo,
                               String uuid,
                               QueryFilterSpec spec) {
        var client = ClientBuilder.newClient();
        var target = client.target(connectionInfo.getLocation().get())
                .path(QUERY_EXECUTION_URI)
                .resolveTemplate("id", uuid)
                .queryParam("mapper", QUERY_MAP_RAW)
                .queryParam("pageSize", -1);
        var specJson = toJson(spec);
        addAuth(connectionInfo, target);
        var response = target.request()
                .accept(DEFAULT_REQUEST_MEDIA_TYPE)
                .post(Entity.entity(specJson, DEFAULT_REQUEST_MEDIA_TYPE), List.class);
        client.close();
        return response;
    }

    public QueryDefinition replaceQuery(KieServerConnectionInfo connectionInfo, QueryDefinition queryDefinition) {
        var client = ClientBuilder.newClient();
        var target = requestForQueryDefinition(connectionInfo, queryDefinition.getName(), client);
        var queryDefJson = toJson(queryDefinition);
        var entity = Entity.entity(queryDefJson, DEFAULT_REQUEST_MEDIA_TYPE);
        var def = target.request()
                .accept(DEFAULT_REQUEST_MEDIA_TYPE)
                .put(entity, QueryDefinition.class);
        client.close();
        return def;
    }

    public void unregisterQuery(KieServerConnectionInfo connectionInfo, String dataSetUUID) {
        var client = ClientBuilder.newClient();
        var target = requestForQueryDefinition(connectionInfo, dataSetUUID, client);
        target.request().delete();
        client.close();
    }

    public String processSVG(KieServerConnectionInfo connectionInfo, String containerId, String processId) {
        var location = connectionInfo.getLocation();
        if (location.isPresent()) {
            var client = ClientBuilder.newClient();
            var target = client.target(location.get())
                    .path(PROCESS_SVG_URI)
                    .resolveTemplate(CONTAINER_ID_PARAM, containerId)
                    .resolveTemplate(PROCESS_ID_PARAM, processId);
            addAuth(connectionInfo, target);
            var svg = target.request().get(String.class);
            client.close();
            return svg;
        }

        throw new RuntimeException("Location for Kie Server is required. Check configuration.");
    }

    private WebTarget requestForQueryDefinition(KieServerConnectionInfo connectionInfo,
                                                String dataSetUUID,
                                                Client client) {
        var target = client.target(connectionInfo.getLocation().get())
                .path(QUERY_DEFINITION_URI)
                .resolveTemplate("id", dataSetUUID);

        addAuth(connectionInfo, target);
        return target;
    }

    private void addAuth(KieServerConnectionInfo connectionInfo, WebTarget target) {
        if (connectionInfo.getUser().isPresent()) {
            var user = connectionInfo.getUser().get();
            var password = connectionInfo.getPassword().orElse("");
            target.register(new BasicAuthFilter(user, password));
        }

        if (connectionInfo.getToken().isPresent()) {
            target.register(new TokenFilter(connectionInfo.getToken().get()));
        }
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing object", e);
        }
    }

}
