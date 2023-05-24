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

import java.util.HashMap;
import java.util.Map;

import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.dashbuilder.dataset.def.ExternalServiceType;
import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonObject;

import static org.dashbuilder.dataset.json.DataSetDefJSONMarshaller.isBlank;

public class ExternalDefJSONMarshaller implements DataSetDefJSONMarshallerExt<ExternalDataSetDef> {

    public static final ExternalDefJSONMarshaller INSTANCE = new ExternalDefJSONMarshaller();

    public static final String URL = "url";
    public static final String DYNAMIC = "dynamic";
    public static final String EXPRESSION = "expression";
    public static final String CONTENT = "content";
    public static final String HEADERS = "headers";
    public static final String ACCUMULATE = "accumulate";
    public static final String TYPE = "type";

    @Override
    public void fromJson(ExternalDataSetDef def, JsonObject json) {
        var url = json.getString(URL);
        var dynamic = json.getBoolean(DYNAMIC);
        var content = json.getString(CONTENT);
        var expression = json.getString(EXPRESSION);
        var headers = json.getObject(HEADERS);
        var accumulate = json.getBoolean(ACCUMULATE);
        var type = json.getString(TYPE);

        if (isBlank(url) && isBlank(content)) {
            throw new IllegalArgumentException("External Data Sets must have \"url\" or \"content\" field");
        }

        if (!isBlank(url)) {
            def.setUrl(url);
        }

        if (!isBlank(content)) {
            def.setContent(content);
        }

        if (!isBlank(expression)) {
            def.setExpression(expression);
        }

        if (headers != null) {
            var headersMap = getHeaders(headers);
            def.setHeaders(headersMap);
        }

        if (!isBlank(type)) {
            var serviceType = ExternalServiceType.byName(type);
            def.setType(serviceType);
        }

        def.setDynamic(dynamic);
        def.setAccumulate(accumulate);
    }

    @Override
    public void toJson(ExternalDataSetDef def, JsonObject json) {
        json.put(DYNAMIC, def.isDynamic());
        json.put(URL, def.getUrl());
        json.put(EXPRESSION, def.getExpression());
        json.put(CONTENT, def.getContent());
        json.put(ACCUMULATE, def.isAccumulate());

        if (def.getType() != null) {
            json.put(TYPE, def.getType().name());
        }

        if (def.getHeaders() != null) {
            var headers = Json.createObject();
            def.getHeaders().forEach((k, v) -> headers.set(k, Json.create(v)));
            json.set(HEADERS, headers);
        }
    }

    private Map<String, String> getHeaders(JsonObject headers) {
        var headersMap = new HashMap<String, String>();
        for (var key : headers.keys()) {
            headersMap.put(key, headers.getString(key));
        }
        return headersMap;
    }
}
