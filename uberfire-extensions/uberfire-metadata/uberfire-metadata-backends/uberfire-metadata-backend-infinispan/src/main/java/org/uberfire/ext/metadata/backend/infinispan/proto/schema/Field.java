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

import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoMessage;

@ProtoMessage
public class Field {

    private ProtobufScope scope;
    private String type;
    private String name;
    private Integer index;
    private Boolean sortable;
    private Boolean searchable;
    private String analyzer;

    public Field() {
    }

    public Field(ProtobufScope scope,
                 String type,
                 String name,
                 int index) {
        this(scope,
             type,
             name,
             index,
             false,
             false,
             "");
    }

    public Field(ProtobufScope scope,
                 ProtobufType type,
                 String name,
                 String analyzer) {
        this(scope,
             type,
             name,
             0,
             false,
             false,
             analyzer);
    }

    public Field(ProtobufScope scope,
                 ProtobufType type,
                 String name,
                 boolean sortable,
                 boolean searchable,
                 String analyzer) {
        this(scope,
             type,
             name,
             0,
             sortable,
             searchable,
             analyzer);
    }

    public Field(ProtobufScope scope,
                 ProtobufType type,
                 String name,
                 int index) {
        this(scope,
             type,
             name,
             index,
             false,
             false,
             "");
    }

    public Field(ProtobufScope scope,
                 ProtobufType type,
                 String name,
                 int index,
                 boolean sortable,
                 boolean searchable,
                 String analyzer) {
        this(scope,
             type.toString(),
             name,
             index,
             sortable,
             searchable,
             analyzer);
    }

    public Field(ProtobufScope scope,
                 String type,
                 String name,
                 int index,
                 boolean sortable,
                 boolean searchable,
                 String analyzer) {
        this.scope = scope;
        this.type = type;
        this.name = name;
        this.index = index;
        this.sortable = sortable;
        this.searchable = searchable;
        this.analyzer = analyzer;
    }

    public ProtobufScope getScope() {
        return scope;
    }

    @ProtoField(number = 1)
    public void setScope(ProtobufScope scope) {
        this.scope = scope;
    }

    public String getType() {
        return type;
    }

    @ProtoField(number = 2)
    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    @ProtoField(number = 3)
    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    @ProtoField(number = 4)
    public void setIndex(Integer index) {
        this.index = index;
    }

    public Boolean isSortable() {
        return sortable;
    }

    @ProtoField(number = 5)
    public void setSortable(Boolean sortable) {
        this.sortable = sortable;
    }

    public Boolean isSearchable() {
        return searchable;
    }

    @ProtoField(number = 6)
    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }

    public String getAnalyzer() {
        return analyzer;
    }

    @ProtoField(number = 7)
    public void setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
    }
}
