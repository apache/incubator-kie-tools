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
package org.dashbuilder.shared.marshalling;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonArray;
import org.dashbuilder.json.JsonObject;
import org.dashbuilder.shared.model.DashbuilderRuntimeMode;
import org.dashbuilder.shared.model.RuntimeModel;
import org.dashbuilder.shared.model.RuntimeServiceResponse;

public class RuntimeServiceResponseJSONMarshaller {

    private static final String RUNTIME_MODEL = "runtimeModel";
    private static final String AVAILABLE_MODELS = "availableModels";
    private static final String MODE = "mode";
    private static final String ALLOW_UPLOAD = "allowUpload";

    private static RuntimeServiceResponseJSONMarshaller instance;

    static {
        instance = new RuntimeServiceResponseJSONMarshaller();
    }

    public static RuntimeServiceResponseJSONMarshaller get() {
        return instance;
    }

    public RuntimeServiceResponse fromJson(String json) {
        return fromJson(Json.parse(json));
    }

    public RuntimeServiceResponse fromJson(JsonObject object) {
        return new RuntimeServiceResponse(DashbuilderRuntimeMode.valueOf(object.getString(MODE)),
                parseRuntimeModel(object.getObject(RUNTIME_MODEL)),
                parseStringArray(object.getArray(AVAILABLE_MODELS)),
                object.getBoolean(ALLOW_UPLOAD));

    }

    public JsonObject toJson(RuntimeServiceResponse service) {
        JsonObject object = Json.createObject();
        object.set(MODE, Json.create(service.getMode().name()));
        object.set(AVAILABLE_MODELS, listToArray(service.getAvailableModels()));
        object.set(ALLOW_UPLOAD, Json.create(service.isAllowUpload()));

        if (service.getRuntimeModelOp().isPresent()) {
            RuntimeModel model = service.getRuntimeModelOp().get();
            object.set(RUNTIME_MODEL, RuntimeModelJSONMarshaller.get().toJson(model));
        }
        return object;
    }

    private JsonArray listToArray(List<String> availableModels) {
        JsonArray array = Json.createArray();
        AtomicInteger i = new AtomicInteger();
        availableModels.forEach(m -> array.set(i.getAndIncrement(), m));
        return array;
    }

    private List<String> parseStringArray(JsonArray array) {
        List<String> availableModels = new ArrayList<>();

        if (array != null) {
            IntStream.range(0, array.length())
                    .mapToObj(array::getString)
                    .forEach(availableModels::add);
        }

        return availableModels;
    }

    private Optional<RuntimeModel> parseRuntimeModel(JsonObject object) {
        return Optional.ofNullable(object).map(RuntimeModelJSONMarshaller.get()::fromJson);
    }

}
