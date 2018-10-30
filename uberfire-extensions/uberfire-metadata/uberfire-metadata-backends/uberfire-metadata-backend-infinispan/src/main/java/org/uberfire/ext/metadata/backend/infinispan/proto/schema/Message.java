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

package org.uberfire.ext.metadata.backend.infinispan.proto.schema;

import java.util.HashSet;
import java.util.Set;

import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoMessage;

@ProtoMessage
public class Message {

    private String name;
    private Set<Field> fields;

    public Message() {
        this.fields = new HashSet<>();
    }

    public Message(String name,
                   Set<Field> fields) {

        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    @ProtoField(number = 1)
    public void setName(String name) {
        this.name = name;
    }

    public Set<Field> getFields() {
        return fields;
    }

    @ProtoField(number = 2, javaType = Field.class, collectionImplementation = HashSet.class)
    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }
}
