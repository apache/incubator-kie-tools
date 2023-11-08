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


package org.kie.workbench.common.stunner.core.definition.jsadapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import elemental2.core.Function;
import elemental2.core.JsObject;
import elemental2.core.Reflect;
import jakarta.enterprise.context.ApplicationScoped;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;

@ApplicationScoped
public class JsDefinitionAdapter implements DefinitionAdapter<Object> {

    private final Map<String, String> categories;
    private final Map<String, String> labels;
    private final Map<String, String> nameFields;
    private final Map<String, Class<? extends ElementFactory>> elementFactories;

    private StunnerTranslationService translationService;

    public JsDefinitionAdapter() {
        categories = new HashMap<String, String>();
        labels = new HashMap<String, String>();
        nameFields = new HashMap<String, String>();
        elementFactories = new HashMap<String, Class<? extends ElementFactory>>();
    }

    @Override
    public DefinitionId getId(Object pojo) {
        String defId = getJsDefinitionId(pojo);
        return DefinitionId.build(defId);
    }

    public static String getJsDefinitionId(Object pojo) {
        return pojo.getClass().getName();
    }

    @Override
    public String getCategory(Object pojo) {
        String id = getJsDefinitionId(pojo);
        return categories.get(id);
    }

    @Override
    public Class<? extends ElementFactory> getElementFactory(Object pojo) {
        return elementFactories.get(getCategory(pojo));
    }

    @Override
    public String getTitle(Object pojo) {
        String id = getJsDefinitionId(pojo);
        return translationService.getDefinitionTitle(id);
    }

    @Override
    public String getDescription(Object pojo) {
        String id = getJsDefinitionId(pojo);
        return translationService.getDefinitionDescription(id);
    }

    @Override
    public String[] getLabels(Object pojo) {
        String id = getJsDefinitionId(pojo);
        String raw = labels.get(id);
        return raw.isEmpty() ? new String[0] : raw.split(",");
    }

    @Override
    public String[] getPropertyFields(Object pojo) {
        // Exclude native js object properties and functions
        return JsObject.getOwnPropertyNames(pojo)
                .filter((prop, i, jsArray) -> (!(prop.contains("__") || Reflect.get(pojo, prop) instanceof Function)))
                .asArray(new String[0]);
    }

    @Override
    public Optional<?> getProperty(Object pojo, String field) {
        JsDefinitionProperty property = new JsDefinitionProperty(pojo, field);
        return Optional.of(property);
    }

    @Override
    public String getMetaPropertyField(Object pojo, PropertyMetaTypes metaType) {
        if (metaType == PropertyMetaTypes.NAME) {
            String id = getJsDefinitionId(pojo);
            String name = nameFields.get(id);
            return null != name ? name : "name";
        }
        // Only Name is supported
        throw new UnsupportedOperationException("Unsupported PropertyMetaType: " + metaType.name());
    }

    @Override
    public Class<? extends ElementFactory> getGraphFactoryType(Object pojo) {
        return getElementFactory(pojo);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean accepts(Class<?> type) {
        return true;
    }

    public void setCategory(String definitionId, String category) {
        categories.put(definitionId, category);
    }

    public void setElementFactory(String category, Class<? extends ElementFactory> factory) {
        elementFactories.put(category, factory);
    }

    public void setLabels(String definitionId, String[] definitionLabels) {
        labels.put(definitionId, Arrays.stream(definitionLabels).collect(Collectors.joining(",")));
    }

    public void setDefinitionNameField(String definitionId, String nameField) {
        nameFields.put(definitionId, nameField);
    }

    public void setTranslationService(StunnerTranslationService translationService) {
        this.translationService = translationService;
    }
}
