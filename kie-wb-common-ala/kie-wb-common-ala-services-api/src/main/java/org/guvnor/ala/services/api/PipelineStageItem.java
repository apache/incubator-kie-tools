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
 * This class models the information about a pipeline stage that was executed or is scheduled for execution.
 */
public class PipelineStageItem {

    /**
     * The name of the pipeline stage.
     */
    private String name;

    /**
     * The status of the stage execution. This status can be on of the following values. SCHEDULED, RUNNING, FINISHED,
     * ERROR.
     */
    private String status;

    /**
     * Holds the stage execution error message when status == ERROR.
     */
    private String stageError;

    /**
     * Holds the stage execution error detail message when status == ERROR.
     */
    private String stageErrorDetail;

    public PipelineStageItem(String name,
                             String status,
                             String stageError,
                             String stageErrorDetail) {
        this.name = name;
        this.status = status;
        this.stageError = stageError;
        this.stageErrorDetail = stageErrorDetail;
    }

    public PipelineStageItem(String name,
                             String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getStageError() {
        return stageError;
    }

    public String getStageErrorDetail() {
        return stageErrorDetail;
    }
}
