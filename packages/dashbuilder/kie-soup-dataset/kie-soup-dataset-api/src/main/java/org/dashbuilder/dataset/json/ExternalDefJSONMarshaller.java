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
package org.dashbuilder.dataset.json;

import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.dashbuilder.json.JsonObject;

import static org.dashbuilder.dataset.json.DataSetDefJSONMarshaller.isBlank;

public class ExternalDefJSONMarshaller implements DataSetDefJSONMarshallerExt<ExternalDataSetDef> {

    public static final ExternalDefJSONMarshaller INSTANCE = new ExternalDefJSONMarshaller();

    public static final String URL = "url";
    public static final String DYNAMIC = "dynamic";

    @Override
    public void fromJson(ExternalDataSetDef def, JsonObject json) {
        var url = json.getString(URL);
        var dynamic = json.getBoolean(DYNAMIC);

        if (!isBlank(url)) {
            def.setUrl(url);
        }

        def.setDynamic(dynamic);
    }

    @Override
    public void toJson(ExternalDataSetDef def, JsonObject json) {
        json.put(DYNAMIC, def.isDynamic());
        json.put(URL, def.getUrl());
    }

}
