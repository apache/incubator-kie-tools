/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataprovider.prometheus.client;

import java.util.Collections;
import java.util.List;

public class QueryResponseBuilder {
    
    private Status status;
    private ResultType resultType;
    private String errorType;
    private String error;
    private List<Result> results = Collections.emptyList();
    
    
    private QueryResponseBuilder() {
        // empty
    }
    
    public static QueryResponseBuilder newQueryResponseBuilder() {
        return new QueryResponseBuilder();
    }
    
    public QueryResponseBuilder status(Status status) {
        this.status = status;
        return this;
    }
    
    public QueryResponseBuilder resultType(ResultType resultType) {
        this.resultType = resultType;
        return this;
    }
    
    public QueryResponseBuilder errorType(String errorType) {
        this.errorType = errorType;
        return this;
    }
    
    public QueryResponseBuilder error(String error) {
        this.error = error;
        return this;
    }
    
    public QueryResponseBuilder results( List<Result> results) {
        this.results = results;
        return this;
    }
    
    public QueryResponse build() {
        QueryResponse queryResponse = new QueryResponse();
        queryResponse.setStatus(this.status);
        queryResponse.setResultType(this.resultType);
        queryResponse.setErrorType(this.errorType);
        queryResponse.setError(this.error);
        queryResponse.setResults(this.results);
        return queryResponse;
    }
    
}