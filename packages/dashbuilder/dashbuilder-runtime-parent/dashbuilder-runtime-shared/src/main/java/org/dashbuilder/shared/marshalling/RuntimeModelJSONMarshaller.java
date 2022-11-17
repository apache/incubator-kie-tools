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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;

import elemental2.dom.DomGlobal;
import org.dashbuilder.dataprovider.DataSetProvider;
import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonObject;
import org.dashbuilder.json.JsonType;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.dashbuilder.navigation.json.NavTreeJSONMarshaller;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.dashbuilder.shared.model.GlobalSettings;
import org.dashbuilder.shared.model.RuntimeModel;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

@ApplicationScoped
public class RuntimeModelJSONMarshaller {

    private static final String GLOBAL = "global";
    private static String LAST_MODIFIED = "lastModified";
    private static String NAV_TREE = "navTree";
    private static String LAYOUT_TEMPLATES = "layoutTemplates";
    private static String PAGES = "pages";
    private static String EXTERNAL_DATASET_DEFS = "datasets";
    private static String PROPERTIES = "properties";

    static final String NAV_GROUP_ID = "__runtime_dashboards";
    static final String NAV_GROUP_NAME = "Dashboards";
    static final String NAV_GROUP_DESC = "Dashboards";

    private static RuntimeModelJSONMarshaller instance;

    DataSetDefJSONMarshaller defMarshaller;

    static {
        instance = new RuntimeModelJSONMarshaller();
        instance.defMarshaller = new DataSetDefJSONMarshaller(new DataSetProviderRegistry() {

            @Override
            public void registerDataProvider(DataSetProvider dataProvider) {}

            @Override
            public DataSetProviderType getProviderTypeByName(String name) {
                return DataSetProviderType.EXTERNAL;
            }

            @Override
            public DataSetProvider getDataSetProvider(DataSetProviderType type) {
                return null;
            }

            @Override
            public Set<DataSetProviderType> getAvailableTypes() {
                return null;
            }
        });
    }

    public static RuntimeModelJSONMarshaller get() {
        return instance;
    }

    public JsonObject toJson(RuntimeModel model) {
        var jsonObject = Json.createObject();
        var navTreeJson = NavTreeJSONMarshaller.get().toJson(model.getNavTree());
        var ltArray = Json.createArray();
        var externalDefsArray = Json.createArray();
        var propertiesObject = Json.createObject();

        jsonObject.set(LAST_MODIFIED, Json.create(model.getLastModified()));
        jsonObject.set(NAV_TREE, navTreeJson);

        var i = new AtomicInteger();
        model.getLayoutTemplates().forEach(lt -> {
            ltArray.set(i.getAndIncrement(), LayoutTemplateJSONMarshaller.get().toJson(lt));
        });
        jsonObject.set(LAYOUT_TEMPLATES, ltArray);

        i.set(0);
        model.getClientDataSets()
                .forEach(def -> externalDefsArray.set(i.getAndIncrement(), defMarshaller.toJsonObject(def)));
        jsonObject.set(EXTERNAL_DATASET_DEFS, externalDefsArray);

        model.getProperties().forEach((k, v) -> propertiesObject.set(k, Json.create(v)));
        jsonObject.set(PROPERTIES, propertiesObject);

        return jsonObject;
    }

    public RuntimeModel fromJson(String json) {
        return fromJson(toJsonObject(json));
    }

    public Map<String, String> retrieveProperties(String json) {
        return extractProperties(toJsonObject(json));
    }

    GlobalSettings retrieveGlobalSettings(JsonObject json) {
        var settingsJson = json.get(GLOBAL);
        if (settingsJson != null &&
            JsonType.OBJECT == settingsJson.getType()) {
            var settingsJsonObject = json.getObject(GLOBAL);
            return GlobalSettingsJSONMarshaller.get().fromJson(settingsJsonObject);
        }
        return new GlobalSettings();
    }

    private JsonObject toJsonObject(String json) {
        JsonObject object = null;
        try {
            object = Json.parse(json);
        } catch (Exception e) {
            DomGlobal.console.debug(e);
            throw new IllegalArgumentException("Error parsing Content");
        }

        if (object == null || object.getType() != JsonType.OBJECT) {
            throw new IllegalArgumentException("Content is not valid");
        }
        return object;
    }

    public RuntimeModel fromJson(JsonObject jsonObject) {
        var navTreeJSONObject = jsonObject.getObject(NAV_TREE);
        var ltArray = jsonObject.getArray(LAYOUT_TEMPLATES);
        var externalDefsArray = jsonObject.getArray(EXTERNAL_DATASET_DEFS);
        var lastModified = jsonObject.getNumber(LAST_MODIFIED);
        var layoutTemplates = new ArrayList<LayoutTemplate>();
        var externalDefs = new ArrayList<ExternalDataSetDef>();
        var nPages = 0;
        if (ltArray == null) {
            ltArray = jsonObject.getArray(PAGES);
        }

        if (ltArray == null || ltArray.length() == 0) {
            throw new IllegalArgumentException("At least one page is required");
        }

        if (JsonType.ARRAY != ltArray.getType()) {
            throw new IllegalArgumentException("Pages must be a list");
        }

        try {
            nPages = ltArray.length();
        } catch (Exception e) {
            throw new IllegalArgumentException("Pages must be a list", e);
        }

        LayoutTemplateJSONMarshaller.get().resetPageCounter();
        for (int i = 0; i < nPages; i++) {
            var ltJson = ltArray.getObject(i);
            if (ltJson != null && ltJson.getType() == JsonType.OBJECT) {
                try {
                    layoutTemplates.add(LayoutTemplateJSONMarshaller.get().fromJson(ltJson));
                } catch (Exception e) {
                    throw new RuntimeException("Error reading page " + (i + 1) + "\n" + e.getMessage(), e);
                }
            }
        }
        if (externalDefsArray != null) {
            var nDatasets = 0;
            try {
                nDatasets = externalDefsArray.length();
            } catch (Exception e) {
                throw new RuntimeException("Data sets must be a list of data set definitions", e);
            }
            for (int i = 0; i < nDatasets; i++) {
                try {
                    var defJson = externalDefsArray.getObject(i).toJson();
                    externalDefs.add((ExternalDataSetDef) defMarshaller.fromJson(defJson));
                } catch (Exception e) {
                    throw new RuntimeException("Error reading data set definition " + (i + 1) + "\n" + e.getMessage(),
                            e);
                }
            }
        }

        var navTree = NavTreeJSONMarshaller.get().fromJson(navTreeJSONObject);

        if (navTree == null || navTree.getRootItems().isEmpty()) {
            navTree = navTreeForTemplates(layoutTemplates);
        }

        var properties = extractProperties(jsonObject);

        var globalSettings = retrieveGlobalSettings(jsonObject);

        return new RuntimeModel(navTree,
                layoutTemplates,
                lastModified.longValue(),
                externalDefs,
                properties,
                globalSettings);
    }

    private HashMap<String, String> extractProperties(JsonObject jsonObject) {
        var properties = new HashMap<String, String>();
        try {
            var propertiesObject = jsonObject.getObject(PROPERTIES);
            if (propertiesObject != null && propertiesObject.getType() == JsonType.OBJECT) {
                for (String key : propertiesObject.keys()) {
                    var val = propertiesObject.getString(key);
                    val = val == null ? "" : val;
                    properties.put(key, val);
                }
            }
        } catch (Exception e) {
            DomGlobal.console.debug(e);
            DomGlobal.console.log("Invalid properties");
        }
        return properties;
    }

    private NavTree navTreeForTemplates(List<LayoutTemplate> layoutTemplates) {
        var treeBuilder = new NavTreeBuilder();
        return buildLayoutTemplatesGroup(layoutTemplates, treeBuilder).build();
    }

    private NavTreeBuilder buildLayoutTemplatesGroup(List<LayoutTemplate> layoutTemplates, NavTreeBuilder treeBuilder) {
        treeBuilder.group(NAV_GROUP_ID, NAV_GROUP_NAME, NAV_GROUP_DESC, false);
        layoutTemplates.forEach(lt -> {
            var ctx = NavWorkbenchCtx.perspective(lt.getName());
            treeBuilder.item(lt.getName(), lt.getName(), "", true, ctx);
        });
        treeBuilder.endGroup();
        return treeBuilder;
    }

}
