/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.uberfire.ext.metadata.backend.infinispan.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.uberfire.ext.metadata.analyzer.ElasticSearchAnalyzerWrapper;
import org.uberfire.ext.metadata.backend.infinispan.proto.schema.Field;
import org.uberfire.ext.metadata.backend.infinispan.proto.schema.Message;
import org.uberfire.ext.metadata.backend.infinispan.proto.schema.ProtobufScope;
import org.uberfire.ext.metadata.backend.infinispan.proto.schema.ProtobufType;
import org.uberfire.ext.metadata.backend.infinispan.proto.schema.Schema;
import org.uberfire.ext.metadata.backend.infinispan.utils.AttributesUtil;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.ext.metadata.model.impl.KPropertyImpl;
import org.uberfire.java.nio.base.version.VersionHistory;
import org.uberfire.java.nio.file.attribute.FileTime;

import static org.uberfire.ext.metadata.backend.infinispan.utils.InfinispanFieldNames.CHECKIN_COMMENT;
import static org.uberfire.ext.metadata.backend.infinispan.utils.InfinispanFieldNames.CREATED_BY;
import static org.uberfire.ext.metadata.backend.infinispan.utils.InfinispanFieldNames.CREATED_DATE;
import static org.uberfire.ext.metadata.backend.infinispan.utils.InfinispanFieldNames.LAST_MODIFIED_BY;
import static org.uberfire.ext.metadata.backend.infinispan.utils.InfinispanFieldNames.LAST_MODIFIED_DATE;

public class MappingProvider {

    public static final String ORG_KIE = "org.kie";
    private Analyzer analyzer;

    public MappingProvider(Analyzer analyzer) {

        this.analyzer = analyzer;
    }

    public Schema getMapping(KObject kObject) {
        return this.buildSchema(kObject);
    }

    private Schema buildSchema(KObject kObject) {
        return new Schema(kObject.getClusterId(),
                          ORG_KIE,
                          this.buildMessages(kObject));
    }

    public Schema buildSchema(KObject kObject,
                              Set<Message> messages) {
        return new Schema(kObject.getClusterId(),
                          ORG_KIE,
                          messages);
    }

    public Set<Message> buildMessages(KObject kObject) {

        List<Field> fields = new ArrayList<>();
        fields.add(this.createField(ProtobufScope.OPTIONAL,
                                    ProtobufType.STRING,
                                    "id"));
        fields.add(this.createField(ProtobufScope.REQUIRED,
                                    ProtobufType.STRING,
                                    "type"));
        fields.add(this.createField(ProtobufScope.REQUIRED,
                                    ProtobufType.STRING,
                                    "cluster__id"));
        fields.add(this.createField(ProtobufScope.REQUIRED,
                                    ProtobufType.STRING,
                                    "segment__id"));
        fields.add(this.createField(ProtobufScope.REQUIRED,
                                    ProtobufType.STRING,
                                    "key"));
        fields.add(createField(ProtobufScope.OPTIONAL,
                               ProtobufType.STRING,
                               "fullText",
                               true,
                               true));

        Iterator<KProperty<?>> iterator = kObject.getProperties().iterator();
        while (iterator.hasNext()) {
            KProperty<?> prop = iterator.next();
            fields.addAll(this.build(prop));
        }

        Message message = new Message(sanitize(kObject.getClusterId() + "_" + kObject.getType().getName()),
                                      new HashSet<>(fields));

        return Collections.singleton(message);
    }

    private String sanitize(String name) {
        return AttributesUtil.toProtobufFormat(name);
    }

    private Set<Field> build(KProperty<?> prop) {

        Class<?> aClass = prop.getValue().getClass();

        if (Enum.class.isAssignableFrom(aClass)) {
            return buildDefaultField(prop,
                                     ProtobufType.STRING);
        } else if (aClass == String.class) {
            return buildDefaultField(prop,
                                     ProtobufType.STRING);
        } else if (aClass == Boolean.class) {
            return buildDefaultField(prop,
                                     ProtobufType.BOOL);
        } else if (aClass == Integer.class) {
            return buildDefaultField(prop,
                                     ProtobufType.INT32);
        } else if (aClass == Double.class) {
            return buildDefaultField(prop,
                                     ProtobufType.DOUBLE);
        } else if (aClass == Long.class) {
            return buildDefaultField(prop,
                                     ProtobufType.INT64);
        } else if (aClass == Float.class) {
            return buildDefaultField(prop,
                                     ProtobufType.FLOAT);
        } else if (FileTime.class.isAssignableFrom(aClass)) {
            return buildDefaultField(prop,
                                     ProtobufType.INT64);
        } else if (Date.class.isAssignableFrom(aClass)) {
            return buildDefaultField(prop,
                                     ProtobufType.INT64);
        } else if (VersionHistory.class.isAssignableFrom(aClass)) {
            return this.build((VersionHistory) prop.getValue());
        } else if (Collection.class.isAssignableFrom(aClass)) {
            return buildDefaultField(prop,
                                     ProtobufType.STRING);
        } else {
            return buildDefaultField(prop,
                                     ProtobufType.STRING);
        }
    }

    private Set<Field> buildDefaultField(KProperty<?> prop,
                                         ProtobufType int32) {
        return Collections.singleton(this.createField(ProtobufScope.OPTIONAL,
                                                      int32,
                                                      this.sanitize(prop.getName()),
                                                      prop.isSortable(),
                                                      prop.isSearchable()));
    }

    private Set<Field> build(VersionHistory versionHistory) {

        Set<Field> fields = new HashSet<>();

        if (versionHistory.records().size() != 0) {

            final int lastIndex = versionHistory.records().size() - 1;

            fields.addAll(this.build(new KPropertyImpl<>(CHECKIN_COMMENT,
                                                         versionHistory.records().get(lastIndex).comment(),
                                                         true)));

            fields.addAll(this.build(new KPropertyImpl<>(CREATED_BY,
                                                         versionHistory.records().get(0).author(),
                                                         true)));

            fields.addAll(this.build(new KPropertyImpl<>(CREATED_DATE,
                                                         versionHistory.records().get(0).date(),
                                                         true)));

            fields.addAll(this.build(new KPropertyImpl<>(LAST_MODIFIED_BY,
                                                         versionHistory.records().get(lastIndex).author(),
                                                         true)));

            fields.addAll(this.build(new KPropertyImpl<>(LAST_MODIFIED_DATE,
                                                         versionHistory.records().get(lastIndex).date(),
                                                         true)));
        }

        return fields;
    }

    private String createAnalyzerField(Field field) {

        return this.createAnalyzerField(field.getName(),
                                        field.getType(),
                                        field.isSearchable());
    }

    private String createAnalyzerField(String name,
                                       String type,
                                       boolean isSearchable) {

        if (this.analyzer instanceof ElasticSearchAnalyzerWrapper) {
            if (ProtobufType.STRING.name().toLowerCase().equals(type.toLowerCase()) && isSearchable) {
                ElasticSearchAnalyzerWrapper elasticSearchAnalyzerWrapper = (ElasticSearchAnalyzerWrapper) analyzer;
                return elasticSearchAnalyzerWrapper.getFieldAnalyzer(name);
            }
        } else {
            throw new IllegalArgumentException("ElasticSearchAnalyzerWrapper is expected to be compatible with Elasticsearch");
        }
        return "";
    }

    private Field createField(ProtobufScope scope,
                              ProtobufType type,
                              String name) {
        return new Field(scope,
                         type,
                         name,
                         this.createAnalyzerField(name,
                                                  type.toString(),
                                                  false));
    }

    private Field createField(ProtobufScope scope,
                              ProtobufType type,
                              String name,
                              boolean sortable,
                              boolean searchable) {
        return new Field(scope,
                         type,
                         name,
                         sortable,
                         searchable,
                         this.createAnalyzerField(name,
                                                  type.toString(),
                                                  searchable));
    }
}
