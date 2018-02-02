/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.events;

import org.guvnor.common.services.project.model.Module;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;

public abstract class BaseDataSourceEvent {

    private DataSourceDef dataSourceDef;

    private Module module;

    private String sessionId;

    private String identity;

    protected BaseDataSourceEvent(final DataSourceDef dataSourceDef,
                                  final Module module,
                                  final String sessionId,
                                  final String identity) {
        this.dataSourceDef = dataSourceDef;
        this.module = module;
        this.sessionId = sessionId;
        this.identity = identity;
    }

    protected BaseDataSourceEvent(final DataSourceDef dataSourceDef,
                                  final String sessionId,
                                  final String identity) {
        this.dataSourceDef = dataSourceDef;
        this.sessionId = sessionId;
        this.identity = identity;
    }

    public DataSourceDef getDataSourceDef() {
        return dataSourceDef;
    }

    public Module getModule() {
        return module;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getIdentity() {
        return identity;
    }

    public boolean isGlobal() {
        return module == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseDataSourceEvent that = (BaseDataSourceEvent) o;

        if (dataSourceDef != null ? !dataSourceDef.equals(that.dataSourceDef) : that.dataSourceDef != null) {
            return false;
        }
        if (module != null ? !module.equals(that.module) : that.module != null) {
            return false;
        }
        if (sessionId != null ? !sessionId.equals(that.sessionId) : that.sessionId != null) {
            return false;
        }
        return identity != null ? identity.equals(that.identity) : that.identity == null;
    }

    @Override
    public int hashCode() {
        int result = dataSourceDef != null ? dataSourceDef.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (module != null ? module.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (sessionId != null ? sessionId.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (identity != null ? identity.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
