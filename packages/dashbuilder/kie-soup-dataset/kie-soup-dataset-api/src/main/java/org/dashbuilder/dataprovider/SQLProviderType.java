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
package org.dashbuilder.dataprovider;

import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshallerExt;
import org.dashbuilder.dataset.json.SQLDefJSONMarshaller;

/**
 * For accessing data sets defined as an SQL query over an existing data source.
 */
public class SQLProviderType extends AbstractProviderType<SQLDataSetDef> {

    @Override
    public String getName() {
        return "SQL";
    }

    @Override
    public SQLDataSetDef createDataSetDef() {
        return new SQLDataSetDef();
    }

    @Override
    public DataSetDefJSONMarshallerExt<SQLDataSetDef> getJsonMarshaller() {
        return SQLDefJSONMarshaller.INSTANCE;
    }
}
