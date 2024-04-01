/*
 * Copyright 2024 Red Hat, Inc. and/or its affiliates.
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

package org.kie.sonataflow.swf.tools.runtime.rpc;

import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.common.annotation.NonBlocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.kie.sonataflow.swf.tools.runtime.dataindex.DataIndexClient;

@ApplicationScoped
public class SonataFlowQuarkusExtensionJsonRPCService {
    public static final String ALL_WORKFLOWS_IDS_QUERY = "{ \"operationName\": \"getAllProcessesIds\", \"query\": \"query getAllProcessesIds{  ProcessInstances{ id } }\" }";

    private final ObjectMapper mapper;
    private final DataIndexClient dataIndexClient;

    @Inject
    public SonataFlowQuarkusExtensionJsonRPCService(ObjectMapper mapper, @RestClient DataIndexClient dataIndexClient) {
        this.mapper = mapper;
        this.dataIndexClient = dataIndexClient;
    }

    @NonBlocking
    public String queryWorkflowsCount() {
        return doQuery(() -> this.dataIndexClient.queryWorkflows(ALL_WORKFLOWS_IDS_QUERY).getData().getWorkflows().size());
    }

    private String doQuery(Supplier<Integer> countSupplier) {
        try {
            return String.valueOf(countSupplier.get());
        } catch (Exception ex) {
            return "-";
        }
    }

}
