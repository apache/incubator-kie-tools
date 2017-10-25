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
 * Helper class for building a RuntimeQuery.
 * @see RuntimeQuery
 */
public class RuntimeQueryBuilder {

    private String providerId;

    private String pipelineId;

    private String pipelineExecutionId;

    private String runtimeId;

    private String runtimeName;

    private RuntimeQueryBuilder() {
    }

    public static RuntimeQueryBuilder newInstance() {
        return new RuntimeQueryBuilder();
    }

    public RuntimeQueryBuilder withProviderId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    public RuntimeQueryBuilder withPipelineId(String pipelineId) {
        this.pipelineId = pipelineId;
        return this;
    }

    public RuntimeQueryBuilder withPipelineExecutionId(String pipelineExecutionId) {
        this.pipelineExecutionId = pipelineExecutionId;
        return this;
    }

    public RuntimeQueryBuilder withRuntimeId(String runtimeId) {
        this.runtimeId = runtimeId;
        return this;
    }

    public RuntimeQueryBuilder withRuntimeName(String runtimeName) {
        this.runtimeName = runtimeName;
        return this;
    }

    public RuntimeQuery build() {
        return new RuntimeQuery(providerId,
                                pipelineId,
                                pipelineExecutionId,
                                runtimeId,
                                runtimeName);
    }
}
