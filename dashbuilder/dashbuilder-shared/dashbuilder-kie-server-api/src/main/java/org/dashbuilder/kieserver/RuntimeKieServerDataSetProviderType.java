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

import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshallerExt;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RuntimeKieServerDataSetProviderType implements DataSetProviderType<RemoteDataSetDef> {

    public RuntimeKieServerDataSetProviderType() {

    }

    @Override
    public String getName() {
        return "REMOTE";
    }

    @Override
    public RemoteDataSetDef createDataSetDef() {
        RemoteDataSetDef def = new RemoteDataSetDef();
        def.setProvider(this);
        def.setDataSource("${org.kie.server.persistence.ds}");
        return def;
    }

    @Override
    public DataSetDefJSONMarshallerExt<RemoteDataSetDef> getJsonMarshaller() {
        return RemoteDefJSONMarshaller.INSTANCE;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RuntimeKieServerDataSetProviderType)) {
            return false;
        }
        return getName().equals(((RuntimeKieServerDataSetProviderType) obj).getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}