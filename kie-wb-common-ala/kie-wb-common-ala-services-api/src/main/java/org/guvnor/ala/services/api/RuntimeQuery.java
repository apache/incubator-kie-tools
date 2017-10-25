/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.services.api;

/**
 * This class models the query parameters for a query against the runtime system to provide information about the
 * currently provisioned Runtimes, like WildlfyRuntimes, etc.
 */
public class RuntimeQuery {

    /**
     * Filter the results for a particular provider. If null, no provider filtering will be applied.
     */
    private String providerId;

    /**
     * Filter the results for a particular pipeline. If null, no pipeline filtering will be applied.
     */
    private String pipelineId;

    /**
     * Filter the results for a particular pipeline execution. If null, no pipeline execution id filtering will be applied.
     */
    private String pipelineExecutionId;

    /**
     * Filter the results for a particular runtime. If null, no runtime filtering will be applied.
     */
    private String runtimeId;

    /**
     * Filter the results for a particular runtime name. If null, no runtime name filtering will be applied.
     */
    private String runtimeName;

    public RuntimeQuery(String providerId,
                        String pipelineId,
                        String pipelineExecutionId,
                        String runtimeId,
                        String runtimeName) {
        this.providerId = providerId;
        this.pipelineId = pipelineId;
        this.pipelineExecutionId = pipelineExecutionId;
        this.runtimeId = runtimeId;
        this.runtimeName = runtimeName;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getPipelineId() {
        return pipelineId;
    }

    public String getPipelineExecutionId() {
        return pipelineExecutionId;
    }

    public String getRuntimeId() {
        return runtimeId;
    }

    public String getRuntimeName() {
        return runtimeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RuntimeQuery query = (RuntimeQuery) o;

        if (providerId != null ? !providerId.equals(query.providerId) : query.providerId != null) {
            return false;
        }
        if (pipelineId != null ? !pipelineId.equals(query.pipelineId) : query.pipelineId != null) {
            return false;
        }
        if (pipelineExecutionId != null ? !pipelineExecutionId.equals(query.pipelineExecutionId) : query.pipelineExecutionId != null) {
            return false;
        }
        if (runtimeId != null ? !runtimeId.equals(query.runtimeId) : query.runtimeId != null) {
            return false;
        }
        return runtimeName != null ? runtimeName.equals(query.runtimeName) : query.runtimeName == null;
    }

    @Override
    public int hashCode() {
        int result = providerId != null ? providerId.hashCode() : 0;
        result = 31 * result + (pipelineId != null ? pipelineId.hashCode() : 0);
        result = 31 * result + (pipelineExecutionId != null ? pipelineExecutionId.hashCode() : 0);
        result = 31 * result + (runtimeId != null ? runtimeId.hashCode() : 0);
        result = 31 * result + (runtimeName != null ? runtimeName.hashCode() : 0);
        return result;
    }
}
