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
package org.dashbuilder.dataset.json;

import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.json.JsonObject;

/**
 * {@link DataSetDefJSONMarshaller} uses this interface to marshall the extended attributes of any class extending
 * {@link DataSetDef}. Every {@link DataSetProviderType} can provide a concrete implementation of this interface (see
 * {@link DataSetProviderType#getJsonMarshaller()}).
 */
public interface DataSetDefJSONMarshallerExt<T extends DataSetDef> {

    /**
     * Updates the given data set def instance with the status stored into the json object passed as a parameter.
     */
    void fromJson(T dataSetDef, JsonObject jsonObject);

    /**
     * Writes into the given json object the status of the data set def instance.
     */
    void toJson(T dataSetDef, JsonObject jsonObject);

}