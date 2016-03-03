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

package org.kie.workbench.common.screens.server.management.client.events;

public class ServerTemplateListRefresh {

    private String selectServerTemplateId = null;

    public ServerTemplateListRefresh() {

    }

    public ServerTemplateListRefresh( final String selectServerTemplateId ) {
        this.selectServerTemplateId = selectServerTemplateId;
    }

    public String getSelectServerTemplateId() {
        return selectServerTemplateId;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ServerTemplateListRefresh ) ) {
            return false;
        }

        final ServerTemplateListRefresh that = (ServerTemplateListRefresh) o;

        return selectServerTemplateId != null ? selectServerTemplateId.equals( that.selectServerTemplateId ) : that.selectServerTemplateId == null;

    }

    @Override
    public int hashCode() {
        return selectServerTemplateId != null ? selectServerTemplateId.hashCode() : 0;
    }
}
