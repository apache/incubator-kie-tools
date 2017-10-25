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

import org.guvnor.ala.runtime.RuntimeState;
import org.guvnor.ala.services.api.itemlist.PipelineStageItemList;

public class RuntimeQueryResultItem {

    /**
     * The provider Id of the provider related with this item. Runtimes are always related to a provider so this value
     * must always be present.
     */
    private String providerId;

    private String providerTypeName;

    private String providerVersion;

    /**
     * When known, it's the id of the pipeline related with this item. It'll be usually the case, but there might be
     * cases where the runtime was created directly by using the RuntimeProvisioningService and not by a pipeline.
     */
    private String pipelineId;

    /**
     * When known, it's the UUID of the pipeline execution related with this item. It'll be usually the case, but there
     * might be cases where the runtime was created directly by using the RuntimeProvisioningService and not by a pipeline.
     */
    private String pipelineExecutionId;

    /**
     * When known, it's the execution status of the pipeline related with this item. This status can be on of the
     * following values. SCHEDULED, RUNNING, FINISHED or ERROR.
     * It'll be usually the case, but there
     * might be cases where the runtime was created directly by using the RuntimeProvisioningService and not by a pipeline.
     */
    private String pipelineStatus;

    /**
     * Holds the pipeline execution error message when pipelineStatus == ERROR.
     */
    private String pipelineError;

    /**
     * Holds the pipeline execution error message when pipelineStatus == ERROR.
     */
    private String pipelineErrorDetail;

    /**
     * When a pipeline is involved, holds the information related to the stages of current pipeline. See that there might
     * be cases where the runtime was created directly by using the RuntimeProvisioningService and not by a pipeline.
     */
    private PipelineStageItemList pipelineStageItems;

    /**
     * When known, it's the id of the runtime related with this item. This value might be un-known if the runtime
     * wasn't yet created, e.g. when the pipeline is still executing, or the pipeline execution finished but with errors
     * and no runtime has been produced.
     */
    private String runtimeId;

    /**
     * When known, it's the runtime name for the runtime related wih this item. This value might be un-known if no runtime
     * name was defined when the runtime was created.
     */
    private String runtimeName;

    /**
     * When known, it's the runtime status. This value might be un-known if the runtime
     * wasn't yet created, e.g. when the pipeline is still executing.
     * @see RuntimeState
     */
    private String runtimeStatus;

    /**
     * When known, it's the runtime initial start date. This value might be un-known if the runtime wasn't yet created.
     */
    private String startedAt;

    /**
     * When known, it's the end point that can be used for accessing the runtime.
     */
    private String runtimeEndpoint;

    public RuntimeQueryResultItem() {
    }

    public RuntimeQueryResultItem(String providerId,
                                  String providerTypeName,
                                  String providerVersion,
                                  String pipelineId,
                                  String pipelineExecutionId,
                                  String pipelineStatus,
                                  String pipelineError,
                                  String pipelineErrorDetail,
                                  PipelineStageItemList pipelineStageItems,
                                  String runtimeId,
                                  String runtimeName,
                                  String runtimeStatus,
                                  String startedAt,
                                  String runtimeEndpoint) {
        this.providerId = providerId;
        this.providerTypeName = providerTypeName;
        this.providerVersion = providerVersion;
        this.pipelineId = pipelineId;
        this.pipelineExecutionId = pipelineExecutionId;
        this.pipelineStatus = pipelineStatus;
        this.pipelineError = pipelineError;
        this.pipelineError = pipelineErrorDetail;
        this.pipelineStageItems = pipelineStageItems;
        this.runtimeId = runtimeId;
        this.runtimeName = runtimeName;
        this.runtimeStatus = runtimeStatus;
        this.startedAt = startedAt;
        this.runtimeEndpoint = runtimeEndpoint;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderTypeName() {
        return providerTypeName;
    }

    public void setProviderTypeName(String providerTypeName) {
        this.providerTypeName = providerTypeName;
    }

    public String getProviderVersion() {
        return providerVersion;
    }

    public void setProviderVersion(String providerVersion) {
        this.providerVersion = providerVersion;
    }

    public String getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(String pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getPipelineExecutionId() {
        return pipelineExecutionId;
    }

    public void setPipelineExecutionId(String pipelineExecutionId) {
        this.pipelineExecutionId = pipelineExecutionId;
    }

    public String getPipelineStatus() {
        return pipelineStatus;
    }

    public void setPipelineStatus(String pipelineStatus) {
        this.pipelineStatus = pipelineStatus;
    }

    public String getPipelineError() {
        return pipelineError;
    }

    public void setPipelineError(String pipelineError) {
        this.pipelineError = pipelineError;
    }

    public String getPipelineErrorDetail() {
        return pipelineErrorDetail;
    }

    public void setPipelineErrorDetail(String pipelineErrorDetail) {
        this.pipelineErrorDetail = pipelineErrorDetail;
    }

    public PipelineStageItemList getPipelineStageItems() {
        return pipelineStageItems;
    }

    public void setPipelineStageItems(PipelineStageItemList pipelineStageItems) {
        this.pipelineStageItems = pipelineStageItems;
    }

    public String getRuntimeId() {
        return runtimeId;
    }

    public void setRuntimeId(String runtimeId) {
        this.runtimeId = runtimeId;
    }

    public String getRuntimeName() {
        return runtimeName;
    }

    public void setRuntimeName(String runtimeName) {
        this.runtimeName = runtimeName;
    }

    public String getRuntimeStatus() {
        return runtimeStatus;
    }

    public void setRuntimeStatus(String runtimeStatus) {
        this.runtimeStatus = runtimeStatus;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getRuntimeEndpoint() {
        return runtimeEndpoint;
    }

    public void setRuntimeEndpoint(String runtimeEndpoint) {
        this.runtimeEndpoint = runtimeEndpoint;
    }
}