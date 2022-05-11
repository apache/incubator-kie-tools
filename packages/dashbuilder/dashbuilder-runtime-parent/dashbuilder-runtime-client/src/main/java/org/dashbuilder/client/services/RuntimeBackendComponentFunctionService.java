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
package org.dashbuilder.client.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.XMLHttpRequest;
import jsinterop.base.Js;
import org.dashbuilder.client.RuntimeClientLoader;
import org.dashbuilder.client.error.ErrorResponseVerifier;
import org.dashbuilder.external.service.BackendComponentFunctionService;
import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonObject;
import org.dashbuilder.json.JsonValue;
import org.jboss.errai.bus.server.annotations.ShadowService;
import org.jboss.resteasy.util.HttpResponseCodes;

@ShadowService
@ApplicationScoped
public class RuntimeBackendComponentFunctionService implements BackendComponentFunctionService {

    @Inject
    ErrorResponseVerifier verifier;

    @Inject
    RuntimeClientLoader loader;

    @Override
    public List<String> listFunctions() {
        if (!loader.hasBackend()) {
            return Collections.emptyList();
        }
        try {
            var xhr = new XMLHttpRequest();
            xhr.open("GET", "/rest/function", false);
            xhr.send();
            verifier.verify(xhr);
            if (xhr.status == 500) {
                throw new RuntimeException("Not able to list functions: " + xhr.responseText);
            }
            String[] functions = Js.cast(Global.JSON.parse(xhr.responseText));
            return Arrays.asList(functions);
        } catch (Exception e) {
            DomGlobal.console.log("Functions service is not available");
            return Collections.emptyList();
        }
    }

    @Override
    public Object callFunction(String name, Map<String, Object> params) {
        var object = buildObject(params);
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "/rest/function/" + name, false);
        xhr.setRequestHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        xhr.send(object.toJson());
        verifier.verify(xhr);
        if (xhr.status == HttpResponseCodes.SC_INTERNAL_SERVER_ERROR) {
            throw new RuntimeException("Not able to invoke function " + name + ": " + xhr.responseText);
        }
        return xhr.responseText;
    }

    private JsonObject buildObject(Map<String, Object> params) {
        var object = Json.createObject();
        params.forEach((k, v) -> {
            JsonValue value = null;
            if (v instanceof Boolean) {
                value = Json.create((boolean) v);
            } else if (v instanceof Number) {
                value = Json.create(((Number) v).doubleValue());
            } else if (v instanceof String) {
                value = Json.create((String) v);
            } else {
                value = Json.create(Global.JSON.stringify(v));
            }
            object.set(k, value);
        });
        return object;
    }

}
