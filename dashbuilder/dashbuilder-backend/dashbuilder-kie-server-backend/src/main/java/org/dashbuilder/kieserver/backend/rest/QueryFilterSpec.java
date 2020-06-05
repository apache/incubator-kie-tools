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

import java.util.Arrays;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "query-filter-spec")
public class QueryFilterSpec {

    @XmlElement(name = "order-by")
    private String orderBy;
    @XmlElement(name = "order-asc")
    private boolean ascending;
    @XmlElement(name = "query-params")
    private QueryParam[] parameters;
    @XmlElement(name = "result-column-mapping")
    private Map<String, String> columnMapping;
    @XmlElement(name = "order-by-clause")
    private String orderByClause;


    public QueryFilterSpec() {
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public QueryParam[] getParameters() {
        return parameters;
    }

    public void setParameters(QueryParam[] parameters) {
        this.parameters = parameters;
    }

    public Map<String, String> getColumnMapping() {
        return columnMapping;
    }

    public void setColumnMapping(Map<String, String> columnMapping) {
        this.columnMapping = columnMapping;
    }

    public String getOrderByClause() {
        return this.orderByClause;
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    @Override
    public String toString() {
        return "QueryFilterSpec{" + "orderBy='" + orderBy + '\'' + ", ascending=" + ascending + ", parameters=" + Arrays.toString(parameters) + '}';
    }

}