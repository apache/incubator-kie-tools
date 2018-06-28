/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.metadata.backend.elastic.metamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.uberfire.ext.metadata.backend.elastic.index.ElasticSearchIndexProvider;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.ext.metadata.model.impl.MetaObjectImpl;
import org.uberfire.ext.metadata.model.impl.MetaPropertyImpl;
import org.uberfire.ext.metadata.model.schema.MetaObject;
import org.uberfire.ext.metadata.model.schema.MetaProperty;

public class ElasticSearchMappingStore {

    protected static final String ES_MAPPING_PROPERTIES = "properties";
    protected static final String ES_MAPPING_TYPE = "type";
    protected static final String JAVA_LANG = "java.lang.";
    private final ElasticSearchIndexProvider indexProvider;

    public ElasticSearchMappingStore(ElasticSearchIndexProvider indexProvider) {
        this.indexProvider = indexProvider;
    }

    public void updateMetaModel(KObject object,
                                MetaObject metaObject) {

        List<MetaProperty> dirtyProperties = new ArrayList<>();
        Optional<MappingMetaData> mappingOptional = this.indexProvider.getMapping(object.getClusterId(),
                                                                                  object.getType().getName());
        if (!mappingOptional.isPresent()) {
            this.indexProvider.putMapping(object.getClusterId(),
                                          object.getType().getName(),
                                          metaObject);
        } else {
            Map<String, Object> mapping = mappingOptional.get().getSourceAsMap();
            Optional.ofNullable(object.getProperties())
                    .ifPresent(kProperties -> {
                        for (final KProperty property : object.getProperties()) {
                            Object config = mapping.get(property.getName());
                            if (config == null) {
                                Optional<MetaProperty> prop = metaObject.getProperty(property.getName());
                                prop.ifPresent(metaProperty -> dirtyProperties.add(metaProperty));
                            }
                        }
                    });

            this.indexProvider.putMapping(object.getClusterId(),
                                          object.getType().getName(),
                                          dirtyProperties);
        }
    }

    public MetaObject getMetaObject(String clusterId,
                                    String type) {

        Optional<MappingMetaData> mapping = this.indexProvider.getMapping(clusterId,
                                                                          type);

        Map<String, Object> entries = mapping
                .map(mappingMetaData -> mappingMetaData.sourceAsMap())
                .orElse(Collections.emptyMap());

        MetaObject meta = this.createMetaObjects(type,
                                                 entries);

        return meta;
    }

    private MetaObject createMetaObjects(String type,
                                         Map<String, Object> entries) {

        Set<MetaProperty> children = inspectTree(Optional.empty(),
                                                 entries);

        return new MetaObjectImpl(() -> type,
                                  children);
    }

    protected Set<MetaProperty> inspectTree(Optional<String> key,
                                            Map<String, Object> mapping) {
        if (mapping.get(ES_MAPPING_PROPERTIES) == null) {
            String t = mapping.get(ES_MAPPING_TYPE).toString();
            return Collections.singleton(new MetaPropertyImpl(key.get(),
                                                              false,
                                                              false,
                                                              Collections.singleton(this.convertType(t))));
        } else {
            Map<String, Object> fields = (Map<String, Object>) mapping.get(ES_MAPPING_PROPERTIES);
            Set<MetaProperty> metaProperties = fields.entrySet()
                    .stream()
                    .map(field -> inspectTree(Optional.of(key.map(k -> k + ".").orElse("") + field.getKey()),
                                              (Map<String, Object>) field.getValue()))
                    .flatMap(x -> x.stream())
                    .collect(Collectors.toSet());

            return metaProperties;
        }
    }

    protected Class<?> convertType(String type) {
        if (type.equalsIgnoreCase(ElasticSearchIndexProvider.ES_TEXT_TYPE) ||
                type.equalsIgnoreCase(ElasticSearchIndexProvider.ES_KEYWORD_TYPE)) {
            return String.class;
        } else {
            return loadClass(type);
        }
    }

    protected Class<?> loadClass(String type) {

        try {
            return ElasticSearchMappingStore.class.getClassLoader().loadClass(JAVA_LANG + StringUtils.capitalize(type));
        } catch (ClassNotFoundException e) {
            throw new ElasticsearchMappingException("Error transforming type to class",
                                                    e);
        }
    }
}
