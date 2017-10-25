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

package org.guvnor.ala.ui.model;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Class for getting information about a runtime.
 */
@Portable
public class Runtime
        extends AbstractHasKeyObject<RuntimeKey> {

    private String name;
    private PipelineExecutionTrace pipelineTrace;
    private String status;
    private String endpoint;
    private String createdDate;

    public Runtime(@MapsTo("key") final RuntimeKey key,
                   @MapsTo("name") final String name,
                   @MapsTo("pipelineTrace") final PipelineExecutionTrace pipelineTrace,
                   @MapsTo("status") final String status,
                   @MapsTo("endpoint") final String endpoint,
                   @MapsTo("createdDate") final String createdDate) {
        super(key);
        this.name = name;
        this.pipelineTrace = pipelineTrace;
        this.status = status;
        this.endpoint = endpoint;
        this.createdDate = createdDate;
    }

    public Runtime(RuntimeKey key) {
        super(key);
    }

    public Runtime(final RuntimeKey key,
                   final String status,
                   final String endpoint,
                   final String createdDate) {
        super(key);
        this.status = status;
        this.endpoint = endpoint;
        this.createdDate = createdDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPipelineTrace(final PipelineExecutionTrace pipelineTrace) {
        this.pipelineTrace = pipelineTrace;
    }

    public String getStatus() {
        return status;
    }

    public PipelineExecutionTrace getPipelineTrace() {
        return pipelineTrace;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(final String endpoint) {
        this.endpoint = endpoint;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Runtime runtime = (Runtime) o;

        if (name != null ? !name.equals(runtime.name) : runtime.name != null) {
            return false;
        }
        if (pipelineTrace != null ? !pipelineTrace.equals(runtime.pipelineTrace) : runtime.pipelineTrace != null) {
            return false;
        }
        if (status != null ? !status.equals(runtime.status) : runtime.status != null) {
            return false;
        }
        if (endpoint != null ? !endpoint.equals(runtime.endpoint) : runtime.endpoint != null) {
            return false;
        }
        return createdDate != null ? createdDate.equals(runtime.createdDate) : runtime.createdDate == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (pipelineTrace != null ? pipelineTrace.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (endpoint != null ? endpoint.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = ~~result;
        return result;
    }
}