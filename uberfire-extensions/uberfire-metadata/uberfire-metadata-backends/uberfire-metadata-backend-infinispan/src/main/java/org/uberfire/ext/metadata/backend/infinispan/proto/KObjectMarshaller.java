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

package org.uberfire.ext.metadata.backend.infinispan.proto;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.infinispan.protostream.MessageMarshaller;
import org.infinispan.protostream.descriptors.Descriptor;
import org.infinispan.protostream.descriptors.FieldDescriptor;
import org.infinispan.protostream.descriptors.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.ext.metadata.model.impl.KObjectImpl;
import org.uberfire.ext.metadata.model.impl.KPropertyImpl;
import org.uberfire.ext.metadata.model.schema.MetaObject;
import org.uberfire.java.nio.base.version.VersionHistory;
import org.uberfire.java.nio.file.attribute.FileTime;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.uberfire.ext.metadata.backend.infinispan.utils.AttributesUtil.toKPropertyFormat;
import static org.uberfire.ext.metadata.backend.infinispan.utils.AttributesUtil.toProtobufFormat;
import static org.uberfire.ext.metadata.backend.infinispan.utils.InfinispanFieldNames.CHECKIN_COMMENT;
import static org.uberfire.ext.metadata.backend.infinispan.utils.InfinispanFieldNames.CREATED_BY;
import static org.uberfire.ext.metadata.backend.infinispan.utils.InfinispanFieldNames.CREATED_DATE;
import static org.uberfire.ext.metadata.backend.infinispan.utils.InfinispanFieldNames.LAST_MODIFIED_BY;
import static org.uberfire.ext.metadata.backend.infinispan.utils.InfinispanFieldNames.LAST_MODIFIED_DATE;

public class KObjectMarshaller implements MessageMarshaller<KObject> {

    private Logger logger = LoggerFactory.getLogger(KObjectMarshaller.class);

    private static final String CLUSTER_ID = toProtobufFormat(MetaObject.META_OBJECT_CLUSTER_ID);
    private static final String SEGMENT_ID = toProtobufFormat(MetaObject.META_OBJECT_SEGMENT_ID);

    private String typeName;
    private final List<String> mainAttributes = Arrays.asList(MetaObject.META_OBJECT_ID,
                                                              MetaObject.META_OBJECT_TYPE,
                                                              CLUSTER_ID,
                                                              SEGMENT_ID,
                                                              MetaObject.META_OBJECT_KEY,
                                                              MetaObject.META_OBJECT_FULL_TEXT);

    public KObjectMarshaller(String typeName) {

        this.typeName = typeName;
    }

    @Override
    public KObjectImpl readFrom(ProtoStreamReader protoStreamReader) throws IOException {

        Descriptor descriptor = protoStreamReader.getSerializationContext().getMessageDescriptor(this.getTypeName());

        List<KProperty<?>> properties = descriptor.getFields()
                .stream()
                .filter(fieldDescriptor ->
                                isExtension(descriptor.getName())
                )
                .map(field -> (KProperty<?>) new KPropertyImpl(toKPropertyFormat(field.getName()),
                                                               this.read(field,
                                                                         protoStreamReader),
                                                               false)
                )
                .collect(toList());

        String id = getAndRemove(properties,
                                 MetaObject.META_OBJECT_ID);
        String type = getAndRemove(properties,
                                   MetaObject.META_OBJECT_TYPE);
        String clusterId = getAndRemove(properties,
                                        MetaObject.META_OBJECT_CLUSTER_ID);
        String segmentId = getAndRemove(properties,
                                        MetaObject.META_OBJECT_SEGMENT_ID);
        String key = getAndRemove(properties,
                                  MetaObject.META_OBJECT_KEY);
        String fullText = getAndRemove(properties,
                                       MetaObject.META_OBJECT_FULL_TEXT);

        return new KObjectImpl(id,
                               type,
                               clusterId,
                               segmentId,
                               key,
                               properties,
                               !StringUtils.isEmpty(fullText));
    }

    private String getAndRemove(final List<KProperty<?>> properties,
                                final String key) {
        KProperty<?> value = properties.stream().filter(kProperty -> kProperty.getName().equals(key)).findFirst().get();
        properties.remove(value);
        return (String) value.getValue();
    }

    @Override
    public void writeTo(ProtoStreamWriter protoStreamWriter,
                        KObject kObject) throws IOException {

        Descriptor descriptor = protoStreamWriter.getSerializationContext().getMessageDescriptor(this.getTypeName());

        TreeMap<Integer, KProperty<?>> props = new TreeMap<>();

        this.addKProperty(props,
                          descriptor,
                          MetaObject.META_OBJECT_ID,
                          kObject.getId());
        this.addKProperty(props,
                          descriptor,
                          MetaObject.META_OBJECT_TYPE,
                          kObject.getType().getName());
        this.addKProperty(props,
                          descriptor,
                          CLUSTER_ID,
                          kObject.getClusterId());
        this.addKProperty(props,
                          descriptor,
                          SEGMENT_ID,
                          kObject.getSegmentId());
        this.addKProperty(props,
                          descriptor,
                          MetaObject.META_OBJECT_KEY,
                          kObject.getKey());

        kObject.getProperties()
                .iterator()
                .forEachRemaining(kprop ->
                                          Optional.ofNullable(descriptor.findFieldByName(toProtobufFormat(kprop.getName())))
                                                  .ifPresent(field -> props
                                                          .putIfAbsent(field.getNumber(),
                                                                       kprop)));

        if (kObject.fullText()) {
            this.addKProperty(props,
                              descriptor,
                              MetaObject.META_OBJECT_FULL_TEXT,
                              props.values().stream()
                                      .filter(kProperty -> kProperty.isSearchable() && !(kProperty.getValue() instanceof Boolean))
                                      .map(kProperty -> String.valueOf(kProperty.getValue()).toLowerCase())
                                      .collect(joining("\n")));
        }

        props.keySet().forEach((number) -> {
            try {
                KProperty<?> kProperty = props.get(number);
                this.writeField(toProtobufFormat(kProperty.getName()),
                                kProperty.getValue(),
                                protoStreamWriter);
            } catch (IOException e) {
                logger.error("error",
                             e);
            }
        });
    }

    @Override
    public Class<? extends KObjectImpl> getJavaClass() {
        return KObjectImpl.class;
    }

    @Override
    public String getTypeName() {
        return this.typeName;
    }

    private void writeField(String name,
                            Object value,
                            ProtoStreamWriter writer) throws IOException {

        Class<?> aClass = value.getClass();

        if (Enum.class.isAssignableFrom(aClass)) {
            writer.writeString(name,
                               value.toString());
        }
        if (aClass == String.class) {
            writer.writeString(name,
                               value.toString());
        }
        if (aClass == Boolean.class) {
            writer.writeBoolean(name,
                                (Boolean) value);
        }

        if (aClass == Integer.class) {
            writer.writeInt(name,
                            (Integer) value);
        }

        if (aClass == Double.class) {
            writer.writeDouble(name,
                               (Double) value);
        }

        if (aClass == Long.class) {
            writer.writeLong(name,
                             (Long) value);
        }

        if (aClass == Float.class) {
            writer.writeFloat(name,
                              (Float) value);
        }

        if (FileTime.class.isAssignableFrom(aClass)) {
            writer.writeLong(name,
                             ((FileTime) value).toMillis());
        }

        if (Date.class.isAssignableFrom(aClass)) {
            writer.writeLong(name,
                             ((Date) value).getTime());
        }

        if (VersionHistory.class.isAssignableFrom(aClass)) {
            this.writeField((VersionHistory) value,
                            writer);
        }

        if (Collection.class.isAssignableFrom(aClass)) {
            final StringBuilder sb = new StringBuilder();
            for (final java.lang.Object oValue : (Collection) value) {
                sb.append(oValue).append(' ');
            }

            writer.writeString(name,
                               sb.toString());
        }
    }

    private void writeField(VersionHistory versionHistory,
                            ProtoStreamWriter writer) throws IOException {

        if (versionHistory.records().size() != 0) {

            final int lastIndex = versionHistory.records().size() - 1;

            this.writeField(CHECKIN_COMMENT,
                            versionHistory.records().get(lastIndex).comment(),
                            writer);

            this.writeField(CREATED_BY,
                            versionHistory.records().get(0).author(),
                            writer);

            this.writeField(CREATED_DATE,
                            versionHistory.records().get(0).date(),
                            writer);

            this.writeField(LAST_MODIFIED_BY,
                            versionHistory.records().get(lastIndex).author(),
                            writer);

            this.writeField(LAST_MODIFIED_DATE,
                            versionHistory.records().get(lastIndex).date(),
                            writer);
        }
    }

    private Object read(FieldDescriptor field,
                        ProtoStreamReader protoStreamReader) {
        JavaType javaType = field.getJavaType();

        try {
            if (JavaType.INT.equals(javaType)) {
                return protoStreamReader.readInt(field.getName());
            } else if (JavaType.BOOLEAN.equals(javaType)) {
                return protoStreamReader.readBoolean(field.getName());
            } else if (JavaType.DOUBLE.equals(javaType)) {
                return protoStreamReader.readDouble(field.getName());
            } else if (JavaType.FLOAT.equals(javaType)) {
                return protoStreamReader.readFloat(field.getName());
            } else if (JavaType.LONG.equals(javaType)) {
                return protoStreamReader.readLong(field.getName());
            } else {
                return protoStreamReader.readString(field.getName());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private KProperty<?> addKProperty(Map<Integer, KProperty<?>> props,
                                      Descriptor descriptor,
                                      String key,
                                      String value) {
        return props.put(descriptor.findFieldByName(toProtobufFormat(key)).getNumber(),
                         new KPropertyImpl<>(key,
                                             value,
                                             false));
    }

    private boolean isExtension(final String name) {
        return !this.getMainAttributes().contains(name);
    }

    private List<String> getMainAttributes() {
        return this.mainAttributes;
    }
}
