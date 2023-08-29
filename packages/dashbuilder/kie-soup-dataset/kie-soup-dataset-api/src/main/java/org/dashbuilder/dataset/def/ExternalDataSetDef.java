/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.dashbuilder.dataset.def;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.dashbuilder.dataprovider.DataSetProviderType;

import static org.dashbuilder.dataset.json.DataSetDefJSONMarshaller.isBlank;

public class ExternalDataSetDef extends DataSetDef {

    private String url;

    private boolean dynamic;

    private String expression;

    private String content;

    private Map<String, String> headers = new HashMap<>();

    private Map<String, String> query = new HashMap<>();

    private Map<String, String> form = new HashMap<>();

    private boolean accumulate;

    private ExternalServiceType type;

    private HttpMethod method = HttpMethod.GET;

    private String path = "";

    private Collection<String> join;

    public ExternalDataSetDef() {
        super.setProvider(DataSetProviderType.EXTERNAL);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Collection<String> getJoin() {
        return join;
    }

    public void setJoin(Collection<String> join) {
        this.join = join;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean isAccumulate() {
        return accumulate;
    }

    public void setAccumulate(boolean accumulate) {
        this.accumulate = accumulate;
    }

    public ExternalServiceType getType() {
        return type;
    }

    public void setType(ExternalServiceType type) {
        this.type = type;
    }

    public void setQuery(Map<String, String> query) {
        this.query = query;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    public Map<String, String> getForm() {
        return form;
    }

    public void setForm(Map<String, String> form) {
        this.form = form;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void validate() {
        super.validate();
        if (isBlank(url) && isBlank(content) && (join == null || join.isEmpty())) {
            throw new IllegalArgumentException("Data Sets must have \"url\", \"content\" or \"join\" field");
        }
    }

    @Override
    public DataSetDef clone() {
        var def = new ExternalDataSetDef();
        clone(def);
        def.setUrl(getUrl());
        def.setDynamic(isDynamic());
        def.setHeaders(getHeaders());
        def.setAccumulate(isAccumulate());
        def.setContent(getContent());
        def.setType(getType());
        def.setJoin(getJoin());
        def.setQuery(getQuery());
        def.setForm(getForm());
        def.setMethod(getMethod());
        def.setPath(getPath());
        return def;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        var other = (ExternalDataSetDef) obj;
        return Objects.equals(content, other.content) &&
               dynamic == other.dynamic &&
               Objects.equals(expression, other.expression) &&
               Objects.equals(headers, other.headers) &&
               Objects.equals(url, other.url) &&
               Objects.equals(accumulate, other.accumulate) &&
               Objects.equals(type, other.type) &&
               Objects.equals(join, other.join) &&
               Objects.equals(query, other.query) &&
               Objects.equals(form, other.form) &&
               Objects.equals(method, other.method) &&
               Objects.equals(path, other.path);
    }

    public String toString() {
        var out = new StringBuilder();
        out.append("UUID=").append(UUID).append("\n");
        out.append("Provider=").append(provider).append("\n");
        out.append("Public=").append(isPublic).append("\n");
        out.append("Push enabled=").append(pushEnabled).append("\n");
        out.append("Push max size=").append(pushMaxSize).append(" Kb\n");
        out.append("URL=").append(url).append("\n");
        out.append("Dynamic=").append(dynamic).append("\n");
        out.append("Expression=").append(expression).append("\n");
        out.append("Content=").append(content).append("\n");
        out.append("Headers=").append(headers).append("\n");
        out.append("Accumulate=").append(accumulate).append("\n");
        out.append("Type=").append(type).append("\n");
        out.append("Join=").append(join).append("\n");
        out.append("Query=").append(query).append("\n");
        out.append("Form=").append(form).append("\n");
        out.append("Method=").append(method).append("\n");
        out.append("Path=").append(path);
        return out.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                url,
                dynamic,
                expression,
                content,
                headers,
                accumulate,
                type,
                join,
                query,
                form,
                method,
                path);
    }

}
