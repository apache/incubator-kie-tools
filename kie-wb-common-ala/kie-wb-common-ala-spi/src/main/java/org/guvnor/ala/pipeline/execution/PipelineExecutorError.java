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

package org.guvnor.ala.pipeline.execution;

import org.apache.commons.lang.exception.ExceptionUtils;

public class PipelineExecutorError {

    private String error;

    private String errorDetail;

    public PipelineExecutorError() {
    }

    public PipelineExecutorError(String error,
                                 String errorDetail) {
        this.error = error;
        this.errorDetail = errorDetail;
    }

    public PipelineExecutorError(String error,
                                 Throwable throwable) {
        this(error,
             ExceptionUtils.getStackTrace(throwable));
    }

    public String getError() {
        return error;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PipelineExecutorError that = (PipelineExecutorError) o;

        if (error != null ? !error.equals(that.error) : that.error != null) {
            return false;
        }
        return errorDetail != null ? errorDetail.equals(that.errorDetail) : that.errorDetail == null;
    }

    @Override
    public int hashCode() {
        int result = error != null ? error.hashCode() : 0;
        result = 31 * result + (errorDetail != null ? errorDetail.hashCode() : 0);
        return result;
    }
}
