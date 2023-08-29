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

package org.dashbuilder.dataset.json;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.dashbuilder.dataset.def.ExternalServiceType;
import org.dashbuilder.dataset.def.HttpMethod;
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
    public static final String QUERY = "query";
    public static final String ACCUMULATE = "accumulate";
    public static final String TYPE = "type";
    public static final String JOIN = "join";
    public static final String FORM = "form";
    public static final String METHOD = "method";
    public static final String PATH = "path";

    @Override
    public void fromJson(ExternalDataSetDef def, JsonObject json) {
        var url = json.getString(URL);
        var dynamic = json.getBoolean(DYNAMIC);
        var content = json.getString(CONTENT);
        var expression = json.getString(EXPRESSION);
        var headers = json.getObject(HEADERS);
        var form = json.getObject(FORM);
        var query = json.getObject(QUERY);
        var accumulate = json.getBoolean(ACCUMULATE);
        var type = json.getString(TYPE);
        var path = json.getString(PATH);
        var method = json.getString(METHOD);
        var join = json.getArray(JOIN);

        if (!isBlank(url)) {
            def.setUrl(url);
        }

        if (!isBlank(content)) {
            def.setContent(content);
        }

        if (!isBlank(expression)) {
            def.setExpression(expression);
        }

        if (!isBlank(method)) {
            def.setMethod(HttpMethod.byName(method));
        }

        if (!isBlank(path)) {
            def.setPath(path);
        }

        if (headers != null) {
            var headersMap = getMap(headers);
            def.setHeaders(headersMap);
        }

        if (query != null) {
            var queryMap = getMap(query);
            def.setQuery(queryMap);
        }

        if (form != null) {
            var formMap = getMap(form);
            def.setForm(formMap);
        }

        if (!isBlank(type)) {
            var serviceType = ExternalServiceType.byName(type);
            def.setType(serviceType);
        }

        def.setJoin(new HashSet<>());
        if (join != null) {
            for (var i = 0; i < join.length(); i++) {
                def.getJoin().add(join.getString(i));
            }
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

        if (def.getMethod() != null) {
            json.put(METHOD, def.getMethod().name());
        }

        if (def.getPath() != null) {
            json.put(PATH, def.getPath());
        }

        if (def.getHeaders() != null) {
            var headers = mapToObject(def.getHeaders());
            json.set(HEADERS, headers);
        }

        if (def.getQuery() != null) {
            var query = mapToObject(def.getQuery());
            json.set(QUERY, query);
        }

        if (def.getForm() != null) {
            var form = mapToObject(def.getForm());
            json.set(FORM, form);
        }

        if (def.getJoin() != null) {
            var join = Json.createArray();
            for (var i = 0; i < def.getJoin().size(); i++) {
                join.set(i, join.get(i));
            }
            json.set(JOIN, join);
        }
    }

    private JsonObject mapToObject(Map<String, String> map) {
        var object = Json.createObject();
        map.forEach((k, v) -> object.set(k, Json.create(v)));
        return object;
    }

    private Map<String, String> getMap(JsonObject map) {
        var hashMap = new HashMap<String, String>();
        for (var key : map.keys()) {
            hashMap.put(key, map.getString(key));
        }
        return hashMap;
    }
}
