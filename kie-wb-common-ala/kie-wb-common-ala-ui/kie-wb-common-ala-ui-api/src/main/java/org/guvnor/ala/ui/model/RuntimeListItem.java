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
 * Convenient class for getting the result of query operations against the provisioning runtime system.
 */
@Portable
public class RuntimeListItem {

    private String itemLabel;

    private Runtime runtime;

    private PipelineExecutionTrace pipelineTrace;

    public RuntimeListItem(@MapsTo("itemLabel") final String itemLabel,
                           @MapsTo("runtime") final Runtime runtime,
                           @MapsTo("pipelineTrace") final PipelineExecutionTrace pipelineTrace) {
        this.itemLabel = itemLabel;
        this.runtime = runtime;
        this.pipelineTrace = pipelineTrace;
    }

    public RuntimeListItem(final String itemLabel,
                           final Runtime runtime) {
        this.itemLabel = itemLabel;
        this.runtime = runtime;
    }

    public RuntimeListItem(final String itemLabel,
                           final PipelineExecutionTrace pipelineTrace) {
        this.itemLabel = itemLabel;
        this.pipelineTrace = pipelineTrace;
    }

    public String getItemLabel() {
        return itemLabel;
    }

    public Runtime getRuntime() {
        return runtime;
    }

    public PipelineExecutionTrace getPipelineTrace() {
        return pipelineTrace;
    }

    public boolean isRuntime() {
        return runtime != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RuntimeListItem that = (RuntimeListItem) o;

        if (itemLabel != null ? !itemLabel.equals(that.itemLabel) : that.itemLabel != null) {
            return false;
        }
        if (runtime != null ? !runtime.equals(that.runtime) : that.runtime != null) {
            return false;
        }
        return pipelineTrace != null ? pipelineTrace.equals(that.pipelineTrace) : that.pipelineTrace == null;
    }

    @Override
    public int hashCode() {
        int result = itemLabel != null ? itemLabel.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (runtime != null ? runtime.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (pipelineTrace != null ? pipelineTrace.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
