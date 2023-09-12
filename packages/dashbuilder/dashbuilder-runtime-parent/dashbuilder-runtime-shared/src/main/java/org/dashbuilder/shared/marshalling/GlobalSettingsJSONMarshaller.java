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

package org.dashbuilder.shared.marshalling;

import java.util.Arrays;
import java.util.Optional;

import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.Mode;
import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;
import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonObject;
import org.dashbuilder.shared.model.GlobalSettings;

public class GlobalSettingsJSONMarshaller {

    private static final String MODE = "mode";
    private static final String DATASET = "dataset";
    private static final String ALLOW_URL_PROPERTIES = "allowUrlProperties";
    private static final Mode DEFAULT_MODE = Mode.LIGHT;

    private static GlobalSettingsJSONMarshaller instance;

    static {
        instance = new GlobalSettingsJSONMarshaller();
    }

    public static GlobalSettingsJSONMarshaller get() {
        return instance;
    }

    public GlobalSettings fromJson(String json) {
        return fromJson(Json.parse(json));
    }

    public GlobalSettings fromJson(JsonObject json) {
        var globalSettings = new GlobalSettings();
        var mode = DEFAULT_MODE;
        var allowUrlProperties = false;
        var displayerSettings = new DisplayerSettings();
        ExternalDataSetDef dataSetDef = null;

        if (json != null) {
            mode = retrieveMode(json);
            allowUrlProperties = retrieveAllowUrlProperties(json);
            var displayerSettingsObj = json.getObject(Arrays.asList(LayoutTemplateJSONMarshaller.SETTINGS,
                    LayoutTemplateJSONMarshaller.DISPLAYER));
            try {
                displayerSettings = DisplayerSettingsJSONMarshaller.get().fromJsonObject(displayerSettingsObj, false);
            } catch (Exception e) {
                displayerSettings.setError("Error on global dataset declaration: " + e.getMessage());
            }

            var datasetDefJson = json.getObject(DATASET);
            try {
                var marshaller = new DataSetDefJSONMarshaller(DataSetProviderType.EXTERNAL);
                dataSetDef = (ExternalDataSetDef) marshaller.fromJsonObj(datasetDefJson);

            } catch (Exception e) {
                // ignore def and use empty global settings
            }

        }

        displayerSettings.setMode(mode);
        globalSettings.setMode(mode);
        globalSettings.setSettings(displayerSettings);
        globalSettings.setDataSetDef(dataSetDef);
        globalSettings.setAllowUrlProperties(allowUrlProperties);
        return globalSettings;

    }

    private boolean retrieveAllowUrlProperties(JsonObject json) {
        if (json.has(ALLOW_URL_PROPERTIES)) {
            var allowUrlProperties = json.getString(ALLOW_URL_PROPERTIES);
            return Boolean.TRUE.toString().equalsIgnoreCase(allowUrlProperties);
        }
        return false;
    }

    private Mode retrieveMode(JsonObject json) {
        if (json.has(MODE)) {
            var modeStr = json.getString(MODE);
            return Optional.ofNullable(Mode.getByName(modeStr)).orElse(DEFAULT_MODE);
        }
        return DEFAULT_MODE;
    }

}
