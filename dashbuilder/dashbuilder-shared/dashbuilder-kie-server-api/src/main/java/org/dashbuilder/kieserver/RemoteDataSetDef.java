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

package org.dashbuilder.kieserver;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RemoteDataSetDef extends SQLDataSetDef {

    @NotNull(groups = {RemoteDataSetDefValidation.class})
    @Size(min = 1, groups = {RemoteDataSetDefValidation.class})
    protected String queryTarget;

    @NotNull(groups = {RemoteDataSetDefValidation.class})
    @Size(min = 1, groups = {RemoteDataSetDefValidation.class})
    protected String serverTemplateId;

    public RemoteDataSetDef() {
        super.setProvider(new RuntimeKieServerDataSetProviderType());
    }

    public String getQueryTarget() {
        return queryTarget;
    }

    public void setQueryTarget(String queryTarget) {
        this.queryTarget = queryTarget;
    }

    public String getServerTemplateId() {
        return serverTemplateId;
    }

    public void setServerTemplateId(String serverTemplateId) {
        this.serverTemplateId = serverTemplateId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((queryTarget == null) ? 0 : queryTarget.hashCode());
        result = prime * result + ((serverTemplateId == null) ? 0 : serverTemplateId.hashCode());
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
        RemoteDataSetDef other = (RemoteDataSetDef) obj;
        if (queryTarget == null) {
            if (other.queryTarget != null)
                return false;
        } else if (!queryTarget.equals(other.queryTarget))
            return false;
        if (serverTemplateId == null) {
            if (other.serverTemplateId != null)
                return false;
        } else if (!serverTemplateId.equals(other.serverTemplateId))
            return false;
        return true;
    }

    @Override
    public DataSetDef clone() {
        RemoteDataSetDef def = new RemoteDataSetDef();
        clone(def);
        def.setQueryTarget(getQueryTarget());
        def.setServerTemplateId(getServerTemplateId());
        def.setDbSQL(getDbSQL());
        def.setDataSource(getDataSource());
        return def;
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("UUID=").append(UUID).append("\n");
        out.append("Provider=").append(provider).append("\n");
        out.append("Public=").append(isPublic).append("\n");
        out.append("Push enabled=").append(pushEnabled).append("\n");
        out.append("Push max size=").append(pushMaxSize).append(" Kb\n");
        if (refreshTime != null) {
            out.append("Refresh time=").append(refreshTime).append("\n");
            out.append("Refresh always=").append(refreshAlways).append("\n");
        }
        out.append("Data source=").append(dataSource).append("\n");
        out.append("Query target=").append(queryTarget).append("\n");
        out.append("Server template id=").append(serverTemplateId).append("\n");
        out.append("DB SQL=").append(dbSQL).append("\n");
        out.append("Get all columns=").append(allColumnsEnabled).append("\n");
        out.append("Cache enabled=").append(cacheEnabled).append("\n");
        out.append("Cache max rows=").append(cacheMaxRows).append(" Kb\n");
        return out.toString();
    }

}