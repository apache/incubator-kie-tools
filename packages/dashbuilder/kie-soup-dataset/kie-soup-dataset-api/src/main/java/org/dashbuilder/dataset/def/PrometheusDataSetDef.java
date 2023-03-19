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
package org.dashbuilder.dataset.def;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.validation.groups.PrometheusDataSetDefValidation;

public class PrometheusDataSetDef extends DataSetDef {

    @Size(min = 7, groups = {PrometheusDataSetDefValidation.class})
    @NotNull(groups = {PrometheusDataSetDefValidation.class})
    protected String serverUrl;

    @Size(min = 1, groups = {PrometheusDataSetDefValidation.class})
    @NotNull(groups = {PrometheusDataSetDefValidation.class})
    protected String query;

    private String user;
    private String password;

    public PrometheusDataSetDef() {
        super.setProvider(DataSetProviderType.PROMETHEUS);
        serverUrl = "http://localhost:9090";
        query = "up";
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public DataSetDef clone() {
        PrometheusDataSetDef def = new PrometheusDataSetDef();
        clone(def);
        def.setQuery(getQuery());
        def.setServerUrl(getServerUrl());
        def.setUser(getUser());
        def.setPassword(getPassword());
        return def;
    }

    @Override
    public String toString() {
        return "PrometheusDataSetDef [serverUrl=" + serverUrl + ", query=" + query + ", user=" + user + ", password=" + password + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((query == null) ? 0 : query.hashCode());
        result = prime * result + ((serverUrl == null) ? 0 : serverUrl.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        PrometheusDataSetDef other = (PrometheusDataSetDef) obj;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (query == null) {
            if (other.query != null)
                return false;
        } else if (!query.equals(other.query))
            return false;
        if (serverUrl == null) {
            if (other.serverUrl != null)
                return false;
        } else if (!serverUrl.equals(other.serverUrl))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }

}