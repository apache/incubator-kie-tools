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

package org.uberfire.ext.metadata.backend.elastic.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.uberfire.ext.metadata.backend.elastic.metamodel.ElasticMetaObject;
import org.uberfire.ext.metadata.backend.elastic.metamodel.ElasticMetaProperty;
import org.uberfire.ext.metadata.backend.elastic.metamodel.ElasticSearchMappingStore;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.ext.metadata.model.impl.KObjectImpl;
import org.uberfire.ext.metadata.model.impl.KPropertyImpl;
import org.uberfire.ext.metadata.model.schema.MetaObject;
import org.uberfire.ext.metadata.model.schema.MetaProperty;
import org.uberfire.java.nio.base.version.VersionHistory;
import org.uberfire.java.nio.file.attribute.FileTime;

import static org.uberfire.ext.metadata.engine.MetaIndexEngine.FULL_TEXT_FIELD;

public class MappingFieldFactory {

    public static final String CHECKIN_COMMENT = "checkinComment";
    public static final String LAST_MODIFIED_BY = "lastModifiedBy";
    public static final String CREATED_BY = "createdBy";
    public static final String CREATED_DATE = "createdDate";
    public static final String LAST_MODIFIED_DATE = "lastModifiedDate";
    private ElasticSearchMappingStore metaModelStore;

    public MappingFieldFactory(ElasticSearchMappingStore elasticMetaModel) {
        this.metaModelStore = elasticMetaModel;
    }

    public MetaObject build(KObject kObject) {

        ElasticMetaObject metaObject = new ElasticMetaObject(kObject.getType());
        metaObject.addProperty(new ElasticMetaProperty(MetaObject.META_OBJECT_ID,
                                                       kObject.getId(),
                                                       new HashSet<>(Arrays.asList(String.class))));

        metaObject.addProperty(new ElasticMetaProperty(MetaObject.META_OBJECT_TYPE,
                                                       kObject.getType().getName(),
                                                       new HashSet<>(Arrays.asList(String.class))));

        metaObject.addProperty(new ElasticMetaProperty(MetaObject.META_OBJECT_KEY,
                                                       kObject.getKey(),
                                                       new HashSet<>(Arrays.asList(String.class))));

        metaObject.addProperty(new ElasticMetaProperty(MetaObject.META_OBJECT_CLUSTER_ID,
                                                       kObject.getClusterId(),
                                                       new HashSet<>(Arrays.asList(String.class))));

        metaObject.addProperty(new ElasticMetaProperty(MetaObject.META_OBJECT_SEGMENT_ID,
                                                       kObject.getSegmentId(),
                                                       new HashSet<>(Arrays.asList(String.class))));

        final StringBuilder allText = new StringBuilder(kObject.getKey()).append('\n');

        if (kObject.getProperties() != null) {
            kObject.getProperties().forEach(kProperty -> {
                List<ElasticMetaProperty> properties = build(kProperty);
                properties.forEach(elasticMetaProperty -> {
                    metaObject.addProperty(elasticMetaProperty);
                    if (elasticMetaProperty.isSearchable() && !elasticMetaProperty.isBoolean()) {
                        allText.append(elasticMetaProperty.getValue()).append('\n');
                    }
                });
            });
        }

        if (kObject.fullText()) {
            metaObject.addProperty(new ElasticMetaProperty(FULL_TEXT_FIELD,
                                                           allText.toString().toLowerCase(),
                                                           new HashSet<>(Arrays.asList(String.class)),
                                                           false,
                                                           true));
        }

        return metaObject;
    }

    private List<ElasticMetaProperty> build(KProperty<?> property) {
        if (Enum.class.isAssignableFrom(property.getValue().getClass())) {
            return Arrays.asList(new ElasticMetaProperty(property.getName(),
                                                         property.getValue().toString().toLowerCase(),
                                                         new HashSet<>(Arrays.asList(String.class)),
                                                         property.isSortable(),
                                                         property.isSearchable()));
        }

        if (FileTime.class.isAssignableFrom(property.getValue().getClass())) {
            return Arrays.asList(new ElasticMetaProperty(property.getName(),
                                                         String.valueOf(((FileTime) property.getValue()).toMillis()),
                                                         new HashSet<>(Arrays.asList(Long.class)),
                                                         property.isSortable(),
                                                         property.isSearchable()));
        }

        if (Date.class.isAssignableFrom(property.getValue().getClass())) {
            return Arrays.asList(new ElasticMetaProperty(property.getName(),
                                                         String.valueOf(((Date) property.getValue()).getTime()),
                                                         new HashSet<>(Arrays.asList(Long.class)),
                                                         property.isSortable(),
                                                         property.isSearchable()));
        }

        if (Collection.class.isAssignableFrom(property.getValue().getClass())) {

            Collection<Object> values = ((Collection) property.getValue());
            String content = values.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(" "));

            return Arrays.asList(new ElasticMetaProperty(property.getName(),
                                                         content,
                                                         new HashSet<>(Arrays.asList(String.class)),
                                                         property.isSortable(),
                                                         property.isSearchable()));
        }

        if (VersionHistory.class.isAssignableFrom(property.getValue().getClass())) {
            final VersionHistory versionHistory = (VersionHistory) property.getValue();
            return build(versionHistory);
        }

        return Arrays.asList(new ElasticMetaProperty(property.getName(),
                                                     property.getValue().toString(),
                                                     new HashSet<>(Arrays.asList(property.getValue().getClass())),
                                                     property.isSortable(),
                                                     property.isSearchable()));
    }

    private List<ElasticMetaProperty> build(VersionHistory versionHistory) {
        if (versionHistory.records().size() == 0) {
            return Collections.emptyList();
        }

        final int lastIndex = versionHistory.records().size() - 1;
        List<ElasticMetaProperty> properties = new ArrayList<>();

        properties.addAll(build(buildKProperty(CHECKIN_COMMENT,
                                               versionHistory.records().get(lastIndex).comment(),
                                               true)));
        properties.addAll(build(buildKProperty(LAST_MODIFIED_BY,
                                               versionHistory.records().get(lastIndex).author(),
                                               true)));

        properties.addAll(build(buildKProperty(CREATED_BY,
                                               versionHistory.records().get(0).author(),
                                               true)));

        properties.addAll(build(buildKProperty(CREATED_DATE,
                                               versionHistory.records().get(0).date(),
                                               true)));
        properties.addAll(build(buildKProperty(LAST_MODIFIED_DATE,
                                               versionHistory.records().get(lastIndex).date(),
                                               true)));

        return properties;
    }

    private <T> KProperty<T> buildKProperty(String name,
                                            T value,
                                            boolean searchable) {
        return new KProperty<T>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public T getValue() {
                return value;
            }

            @Override
            public boolean isSearchable() {
                return searchable;
            }
        };
    }

    public KObject fromDocument(Map<String, ?> document) {
        String clustedId = String.valueOf(document.get(MetaObject.META_OBJECT_CLUSTER_ID));
        String type = String.valueOf(document.get(MetaObject.META_OBJECT_TYPE));

        MetaObject metaModel = metaModelStore.getMetaObject(clustedId,
                                                            type);

        Collection<MetaProperty> properties = metaModel.getProperties();
        List<KProperty<?>> kProperties = buildKProperties(document,
                                                          properties);

        return new KObjectImpl(String.valueOf(document.get(MetaObject.META_OBJECT_ID)),
                               type,
                               clustedId,
                               String.valueOf(document.get(MetaObject.META_OBJECT_SEGMENT_ID)),
                               String.valueOf(document.get(MetaObject.META_OBJECT_KEY)),
                               kProperties,
                               false);
    }

    private List<KProperty<?>> buildKProperties(Map<String, ?> document,
                                                Collection<MetaProperty> properties) {
        return properties.stream().map(metaProperty -> new KPropertyImpl<>(metaProperty.getName(),
                                                                           document.get(metaProperty.getName()),
                                                                           metaProperty.isSearchable()))
                .collect(Collectors.toList());
    }
}
