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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.uberfire.ext.metadata.backend.infinispan.proto.schema.Field;
import org.uberfire.ext.metadata.backend.infinispan.proto.schema.Message;
import org.uberfire.ext.metadata.backend.infinispan.proto.schema.Schema;
import org.uberfire.ext.metadata.model.KObject;

import static org.uberfire.ext.metadata.backend.infinispan.utils.AttributesUtil.toProtobufFormat;

public class InfinispanSchemaStore {

    private InfinispanContext infinispanContext;
    private final MappingProvider mappingProvider;

    public InfinispanSchemaStore(InfinispanContext infinispanContext,
                                 MappingProvider mappingProvider) {
        this.infinispanContext = infinispanContext;
        this.mappingProvider = mappingProvider;
    }

    public void updateSchema(KObject kObject) {

        String clustedId = toProtobufFormat(kObject.getClusterId());
        Optional<Schema> storedSchema = this.infinispanContext.getSchema(clustedId);

        if (!storedSchema.isPresent()) {
            Schema newSchema = this.mappingProvider.getMapping(kObject);

            newSchema.getMessages().forEach(message -> {
                message.setFields(this.reorderFields(new ArrayList<>(message.getFields())));
            });

            this.infinispanContext.addSchema(newSchema);
            this.infinispanContext.addProtobufSchema(kObject.getClusterId(),
                                                     newSchema);
        } else {
            String type = toProtobufFormat(kObject.getClusterId() + "_" + kObject.getType().getName());
            Optional<Message> storedMessage = this.getMessage(storedSchema.get(),
                                                              type);

            Set<Message> messages = new HashSet<>();
            if (storedMessage.isPresent()) {
                Message newMessages = this.merge(storedMessage.get(),
                                                 this.getMessage(kObject));
                messages.addAll(storedSchema.get().getMessages());
                messages.remove(storedMessage.get());
                messages.add(newMessages);
            } else {
                Set<Message> newMessages = this.mappingProvider.buildMessages(kObject);
                messages.addAll(newMessages);
                messages.addAll(storedSchema.get().getMessages());
            }
            Schema newSchema = this.mappingProvider.buildSchema(kObject,
                                                                messages);
            this.infinispanContext.addSchema(newSchema);
            this.infinispanContext.addProtobufSchema(kObject.getClusterId(),
                                                     newSchema);
        }
    }

    private Message getMessage(KObject kObject) {
        return this.mappingProvider.buildMessages(kObject)
                .iterator()
                .next();
    }

    protected Message merge(Message storedMessage,
                            Message newMessage) {

        List<Field> fields = new ArrayList<>(storedMessage.getFields());

        newMessage.getFields().forEach(field -> {
            if (!containsField(fields,
                               field) &&
                    !containsField(new ArrayList<>(storedMessage.getFields()),
                                   field)) {
                fields.add(field);
            }
        });

        Set<Field> reorderedFields = reorderFields(fields);

        return new Message(storedMessage.getName(),
                           reorderedFields);
    }

    public boolean containsField(List<Field> fields,
                                 Field field) {
        return fields.stream().anyMatch(f -> field.getName().equals(f.getName()));
    }

    private Set<Field> reorderFields(List<Field> fields) {

        int maxIndex = this.getMaxIndexNumber(fields);

        List<Field> unsavedFields = fields.stream()
                .filter(field -> field.getIndex() == 0)
                .collect(Collectors.toList());

        for (Field field : unsavedFields) {
            maxIndex++;
            field.setIndex(maxIndex);
        }

        return new HashSet<>(fields);
    }

    protected int getMaxIndexNumber(List<Field> fields) {
        return fields.stream()
                .sorted(Comparator.comparingInt(Field::getIndex).reversed())
                .mapToInt(Field::getIndex)
                .findFirst()
                .orElse(0);
    }

    private Optional<Message> getMessage(Schema schema,
                                         String type) {
        return schema.getMessages().stream()
                .filter(message -> message.getName().equals(type))
                .findAny();
    }
}
